package it.unibs.mp.horace.backend

import android.net.Uri
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import it.unibs.mp.horace.backend.User.Companion.FRIENDS_COLLECTION_NAME
import it.unibs.mp.horace.backend.User.Companion.WORKGROUP_COLLECTION_NAME
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

    // TODO: These should not necessarily be tied to the CurrentUser class
    val activities: MutableList<Activity> = mutableListOf()
    val areas: MutableList<Area> = mutableListOf()
    val timeEntries: MutableList<TimeEntry> = mutableListOf()

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
            .toObjects(User::class.java)
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
            .await().toObjects(User::class.java)
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
            .whereNotIn(User.UID_FIELD, workgroupIds).get().await().toObjects(User::class.java)
    }

    /**
     * The invitations sent to the current user.
     */
    suspend fun invitations(): List<Invitation> {
        return userDocument.collection(Invitation.COLLECTION_NAME).get().await()
            .toObjects(Invitation::class.java)
    }

    /**
     * Accepts the specified invitation.
     */
    suspend fun acceptInvitation(invitation: Invitation) {
        val collection = if (invitation.type == Invitation.TYPE_FRIEND_INVITATION) {
            FRIENDS_COLLECTION_NAME
        } else {
            WORKGROUP_COLLECTION_NAME
        }

        // Add invitation sender to the current user friends/workgroup
        userDocument.collection(collection).add(invitation.senderUid).await()

        // Add current user to the invitation sender friends/workgroup
        db.collection(User.COLLECTION_NAME).document(invitation.senderUid).collection(collection)
            .add(firebaseUser.uid).await()

        // Update the invitation status
        invitation.accepted = true
        userDocument.collection(Invitation.COLLECTION_NAME).document(invitation.id).set(invitation)
            .await()
    }

    /**
     * Sends a friend invitation to the specified user.
     */
    suspend fun sendFriendRequest(user: User) {
        // If the user is already a friend, throw an exception


        sendInvitation(user, Invitation.TYPE_FRIEND_INVITATION)
    }

    /**
     * Sends a work group invitation to the specified user.
     */
    suspend fun sendWorkGroupRequest(user: User) {
        sendInvitation(user, Invitation.TYPE_WORKGROUP_INVITATION)
    }

    /**
     * Sends an invitation to the specified user.
     */
    private suspend fun sendInvitation(user: User, type: Int) {
        val collection = if (type == Invitation.TYPE_FRIEND_INVITATION) {
            FRIENDS_COLLECTION_NAME
        } else {
            WORKGROUP_COLLECTION_NAME
        }

        // Check if the user is already a friend/workgroup member
        if (userDocument.collection(collection).whereEqualTo(User.UID_FIELD, user.uid)
                .get().await().any()
        ) {
            throw IllegalArgumentException()
        }

        // The invitation document reference for the destination user
        val destInvitations = db.collection(User.COLLECTION_NAME).document(user.uid)
            .collection(Invitation.COLLECTION_NAME)

        // Check if there's already a pending invitation of the same type sent by the current user
        val hasPendingInvitation = destInvitations.get().await().any {
            val invitation = it.toObject(Invitation::class.java)
            invitation.type == type && !invitation.isExpired && invitation.senderUid == firebaseUser.uid
        }

        // If there's already a pending invitation, don't send another one
        if (hasPendingInvitation) {
            return
        }

        // Otherwise, send the invitation
        val ref = destInvitations.document()
        ref.set(Invitation(ref.id, user.uid, type)).await()
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
        userDocument.set(userData.toHashMap(), SetOptions.merge()).await()
    }
}