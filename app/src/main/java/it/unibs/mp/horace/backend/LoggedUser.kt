package it.unibs.mp.horace.backend

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class LoggedUser {
    companion object {
        const val TAG = "LoggedUserClass"
    }

    /**
     * Authentication providers.
     */
    enum class Provider {
        EMAIL, GOOGLE, FACEBOOK
    }

    private val auth: FirebaseAuth = Firebase.auth
    private var db: FirebaseFirestore = Firebase.firestore

    private val user: FirebaseUser

    var username: String?
        get() = user.displayName
        set(value) {
            val usrProfileChangeBuilder = UserProfileChangeRequest.Builder()
            usrProfileChangeBuilder.displayName = value
            user.updateProfile(usrProfileChangeBuilder.build())
            updateUserDocument(hashMapOf(User.USERNAME_FIELD to value!!))
        }

    var email: String
        get() = user.email!!
        set(value) {
            user.updateEmail(value)
            updateUserDocument(hashMapOf(User.EMAIL_FIELD to value))
        }

    val uid: String
        get() = user.uid

    /**
     * The profile photo of the user.
     * No default value is provided if no user photo is available.
     */
    var photoUrl: Uri?
        get() = user.photoUrl
        set(value: Uri?) {
            // TODO: Update user photo
        }

    /**
     * The authentication provider of the user.
     */
    val provider: Provider
        get() {
            val providers = user.providerData.map { it.providerId }

            return when {
                providers.contains("google.com") -> Provider.GOOGLE
                providers.contains("facebook.com") -> Provider.FACEBOOK
                else -> Provider.EMAIL
            }
        }

    init {
        val loggedUser = auth.currentUser
        if (loggedUser != null) {
            // User is signed in
            user = loggedUser
        } else {
            // No user is signed in
            throw IllegalAccessError("User is not logged")
        }
    }

    fun createUserDocument() {
        val userData = hashMapOf(
            User.EMAIL_FIELD to email, User.UID_FIELD to uid
        )
        this.updateUserDocument(userData)
    }

    private fun updateUserDocument(user: HashMap<String, String>) {
        db.collection(User.COLLECTION_NAME).document(uid).set(user, SetOptions.merge())
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: $documentReference")
            }.addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }
}