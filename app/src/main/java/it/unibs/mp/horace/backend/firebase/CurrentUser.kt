package it.unibs.mp.horace.backend.firebase

import android.net.Uri
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import it.unibs.mp.horace.backend.LeaderboardItem
import it.unibs.mp.horace.backend.firebase.models.TimeEntry
import it.unibs.mp.horace.backend.firebase.models.User
import it.unibs.mp.horace.backend.firebase.models.User.Companion.FRIENDS_COLLECTION_NAME
import it.unibs.mp.horace.backend.firebase.models.User.Companion.WORKGROUP_COLLECTION_NAME
import kotlinx.coroutines.tasks.await

class CurrentUser {
    companion object {
        private const val ERROR_USER_NOT_SIGNED_IN = "User is not signed in"
        private const val PROVIDER_GOOGLE_ID = "google.com"
        private const val PROVIDER_FACEBOOK_ID = "facebook.com"
    }

    /**
     * Authentication providers.
     */
    enum class Provider {
        EMAIL, GOOGLE, FACEBOOK
    }

    /**
     * Tracks which profile fields have been changed.
     */
    class ProfileChangesTracker {
        var emailChanged = false
        var photoChanged = false
        var usernameChanged = false

        fun anyChanged(): Boolean = emailChanged || photoChanged || usernameChanged
    }

    /**
     * The Firebase Firestore database.
     */
    private val db: FirebaseFirestore = Firebase.firestore

    /**
     * The Firebase user, where the authentication data is stored.
     */
    private val firebaseUser: FirebaseUser

    /**
     * The document reference to the user, to update the user data.
     * Should always be synced to the firebaseUser.
     */
    private val userDocument: DocumentReference

    /**
     * The reference to the user photo, to update the user photo.
     */
    private val photoRef: StorageReference

    /**
     * The user data, to avoid fetching it from the database every time.
     */
    private val userData: User

    /**
     * Tracks which profile fields have been changed.
     */
    private var changesTracker = ProfileChangesTracker()

    init {
        val loggedUser = Firebase.auth.currentUser
        if (loggedUser != null) {
            // User is signed in
            firebaseUser = loggedUser
        } else {
            // User is not signed in
            throw IllegalAccessError(ERROR_USER_NOT_SIGNED_IN)
        }

        firebaseUser.apply {
            userDocument = db.collection(User.COLLECTION_NAME).document(uid)
            photoRef = Firebase.storage.reference.child("images/profile/${uid}")

            // Initialize user data to firebase values, so there's no need to block
            // the thread to wait for the data to be fetched from the database.
            userData = User(
                uid, email!!, displayName, photoUrl
            )
        }
    }

    val uid: String
        get() = userData.uid

    var username: String?
        get() = userData.username
        set(value) {
            changesTracker.usernameChanged = true
            userData.username = value
        }

    var email: String
        get() = userData.email
        set(value) {
            changesTracker.emailChanged = true
            userData.email = value
        }

    /**
     * The profile photo of the user.
     * No default value is provided if no user photo is available.
     */
    var photoUrl: Uri?
        get() = userData.photoUrl
        set(value) {
            changesTracker.photoChanged = true
            userData.photoUrl = value
        }

    /**
     * The authentication provider of the user.
     */
    val provider: Provider
        get() {
            val providers = firebaseUser.providerData.map { it.providerId }

            return when {
                providers.contains(PROVIDER_GOOGLE_ID) -> Provider.GOOGLE
                providers.contains(PROVIDER_FACEBOOK_ID) -> Provider.FACEBOOK
                else -> Provider.EMAIL
            }
        }

    /**
     * The friends of the current user.
     */
    suspend fun friends(): List<User> {
        // Get the friends ids
        val friendsIds = userDocument.collection(FRIENDS_COLLECTION_NAME).get().await()
            .mapNotNull { it.getString(User.UID_FIELD) }

        if (friendsIds.isEmpty()) {
            return emptyList()
        }

        // Get the friends data from the ids
        return db.collection(User.COLLECTION_NAME).whereIn(User.UID_FIELD, friendsIds).get().await()
            .mapNotNull { User.parse(it.data) }
    }

    suspend fun weeklyLeaderboard(): List<LeaderboardItem> {
        val leaderboard: MutableList<LeaderboardItem> = mutableListOf()

        // Get the friends ids
        val friendsIds = userDocument.collection(FRIENDS_COLLECTION_NAME).get().await()
            .mapNotNull { it.getString(User.UID_FIELD) }

        // If the user has no friends, return an empty list
        if (friendsIds.isEmpty()) {
            return emptyList()
        }

        friends().forEach {
            val friendEntries = db.collection(User.COLLECTION_NAME).document(it.uid)
                .collection(TimeEntry.COLLECTION_NAME).get().await()
                .mapNotNull { entry -> TimeEntry.parse(entry.data) }

            val lastWeekEntries = friendEntries.filter { entry ->
                entry.isInCurrentWeek
            }

            leaderboard.add(
                LeaderboardItem(it, lastWeekEntries.sumOf { entry -> entry.points })
            )
        }

        // Add the current user to the leaderboard
        val userPointsInLastWeek = userDocument.collection(TimeEntry.COLLECTION_NAME).get().await()
            .mapNotNull { TimeEntry.parse(it.data) }.sumOf { it.points }
        leaderboard.add(
            LeaderboardItem(userData, userPointsInLastWeek)
        )

        return leaderboard
    }

    /**
     * The workgroup of the current user.
     */
    suspend fun workGroup(): List<User> {
        // Get the workgroup ids
        val workgroupIds = userDocument.collection(WORKGROUP_COLLECTION_NAME).get().await()
            .mapNotNull { it.getString(User.UID_FIELD) }

        if (workgroupIds.isEmpty()) {
            return emptyList()
        }

        // Get the workgroup data from the ids
        return db.collection(User.COLLECTION_NAME).whereIn(User.UID_FIELD, workgroupIds).get()
            .await().mapNotNull { User.parse(it.data) }
    }

    suspend fun friendsNotInWorkGroup(): List<User> {
        // Get the workgroup ids
        val workgroupIds = userDocument.collection(WORKGROUP_COLLECTION_NAME).get().await()
            .mapNotNull { it.getString(User.UID_FIELD) }

        // If workgroup is empty, return all friends
        if (workgroupIds.isEmpty()) {
            return friends()
        }

        // Return all friends not in workgroup
        return userDocument.collection(FRIENDS_COLLECTION_NAME)
            .whereNotIn(User.UID_FIELD, workgroupIds).get().await()
            .mapNotNull { User.parse(it.data) }
    }

    /**
     * Updates both the authentication data and the user document.
     */
    suspend fun update() {
        if (!changesTracker.anyChanged()) {
            return
        }

        // Auth should be updated before document,
        // so the new photo url is available
        updateAuth()
        updateUserDocument()

        // Reset changes tracker
        changesTracker = ProfileChangesTracker()
    }

    /**
     * Updates the authentication data.
     */
    private suspend fun updateAuth() {
        // Only updates fields that have been changed
        changesTracker.apply {
            if (emailChanged) {
                firebaseUser.updateEmail(userData.email).await()
            }

            if (usernameChanged) {
                val updates = userProfileChangeRequest {
                    displayName = userData.username
                }
                firebaseUser.updateProfile(updates).await()
            }

            if (photoChanged) {
                // Allows eliminating the photo if it's null
                val url = if (userData.photoUrl != null) {
                    // Remove current photo
                    photoRef.delete().await()

                    // Upload new photo
                    photoRef.putFile(userData.photoUrl!!).await()
                    photoRef.downloadUrl.await()
                } else {
                    null
                }

                userData.photoUrl = url
                val updates = userProfileChangeRequest {
                    photoUri = url
                }
                firebaseUser.updateProfile(updates).await()
            }
        }
    }

    /**
     * Updates the user document.
     */
    private suspend fun updateUserDocument() {
        userDocument.set(userData.stringify()).await()
    }

    /**
     * Deletes the current user from both Firebase Auth and Firestore.
     * NB: This really should be done by a cloud function or server, not by the client.
     * It's also very inefficient and should be improved in a production app.
     */
    suspend fun delete() {
        // Delete user from friends' friends list
        val friendsIds = userDocument.collection(FRIENDS_COLLECTION_NAME).get().await()
            .mapNotNull { it.getString(User.UID_FIELD) }

        db.collection(User.COLLECTION_NAME).whereIn(User.UID_FIELD, friendsIds).get().await()
            .forEach { friendDocument ->
                friendDocument.reference.collection(FRIENDS_COLLECTION_NAME).document(uid).delete()
                    .await()
            }

        // Delete user from friends' work group
        val workgroupIds = userDocument.collection(WORKGROUP_COLLECTION_NAME).get().await()
            .mapNotNull { it.getString(User.UID_FIELD) }

        db.collection(User.COLLECTION_NAME).whereIn(User.UID_FIELD, workgroupIds).get().await()
            .forEach { workgroupDocument ->
                workgroupDocument.reference.collection(WORKGROUP_COLLECTION_NAME).document(uid)
                    .delete().await()
            }

        // Delete friends subcollection
        userDocument.collection(FRIENDS_COLLECTION_NAME).get().await().forEach {
            it.reference.delete().await()
        }

        // Delete workgroup subcollection
        userDocument.collection(WORKGROUP_COLLECTION_NAME).get().await().forEach {
            it.reference.delete().await()
        }

        firebaseUser.delete().await()
        userDocument.delete().await()
        photoRef.delete().await()
    }
}