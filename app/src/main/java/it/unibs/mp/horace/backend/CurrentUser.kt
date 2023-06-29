package it.unibs.mp.horace.backend

import android.net.Uri
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
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
            userDocument = Firebase.firestore.collection(User.COLLECTION_NAME).document(uid)
            photoRef = Firebase.storage.reference.child("images/profile/${uid}")

            // Initialize user data to firebase values, so there's no need to block
            // the thread to wait for the data to be fetched from the database.
            userData = User(
                uid, displayName, email!!, photoUrl
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

    // TODO: Add friends
    val friends: List<User> = listOf(
        User("0001", "Mario Rossi", "mario@example.com"),
        User("0002", "Luigi Bianchi", "luigi@example.com")
    )

    val workGroup: List<User> = listOf()

    val friendsNotInWorkGroup: List<User> = friends.filter { it !in workGroup }

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