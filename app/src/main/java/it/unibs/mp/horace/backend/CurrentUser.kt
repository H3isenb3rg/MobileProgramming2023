package it.unibs.mp.horace.backend

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

class CurrentUser {

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
    }

    private val auth: FirebaseAuth = Firebase.auth
    private var db: FirebaseFirestore = Firebase.firestore
    private var storage = Firebase.storage

    // The Firebase user, where the authentication data is stored.
    private val firebaseUser: FirebaseUser

    // The document reference to the user, to update the user data.
    // Should always be synced to the firebaseUser.
    private val userDocument: DocumentReference

    // The user data, stored in the database.
    private val userData: User

    // To track which fields have been changed.
    private var changesTracker = ProfileChangesTracker()

    init {
        val loggedUser = auth.currentUser
        if (loggedUser != null) {
            // User is signed in
            firebaseUser = loggedUser
        } else {
            // User is not signed in
            throw IllegalAccessError("User is not logged")
        }

        userDocument = db.collection(User.COLLECTION_NAME).document(loggedUser.uid)
        userData = User.fromHashMap(userDocument.get().result?.data!! as HashMap<String, Any>)
    }

    var username: String
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

    var fcmToken: String?
        get() = userData.fcmToken
        set(value) {
            userData.fcmToken = value
        }

    /**
     * The authentication provider of the user.
     */
    val provider: Provider
        get() {
            val providers = firebaseUser.providerData.map { it.providerId }

            return when {
                providers.contains("google.com") -> Provider.GOOGLE
                providers.contains("facebook.com") -> Provider.FACEBOOK
                else -> Provider.EMAIL
            }
        }

    // TODO: Add friends
    val friends: List<User> = listOf(
        User("Mario Rossi", "mario@example.com", "0001", null),
        User("Luigi Bianchi", "luigi@example.com", "0002", null)
    )

    val workGroup: List<User> = listOf()

    val friendsNotInWorkGroup: List<User> = friends.filter { it !in workGroup }

    /**
     * Updates both the authentication data and the user document.
     */
    fun update() {
        runBlocking {
            updateAuth()
            updateUserDocument()
        }

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
                val photoRef = storage.reference.child("images/profile/${firebaseUser.uid}")

                photoRef.putFile(userData.photoUrl!!).await()
                val url = photoRef.downloadUrl.await()

                userData.photoUrl = url
                val updates = userProfileChangeRequest {
                    photoUri = url
                }
                firebaseUser.updateProfile(updates).await()
            }
        }

        // Reset changes tracker
        changesTracker = ProfileChangesTracker()
    }

    /**
     * Updates the user document.
     */
    private suspend fun updateUserDocument() {
        userDocument.set(userData.toHashMap(), SetOptions.merge()).await()
    }
}