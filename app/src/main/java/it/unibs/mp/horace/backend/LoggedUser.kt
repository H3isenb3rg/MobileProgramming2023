package it.unibs.mp.horace.backend

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

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
    private var storage = Firebase.storage

    private val user: FirebaseUser

    var username: String?
        get() = user.displayName
        set(value) {
            val usrProfileChangeBuilder = UserProfileChangeRequest.Builder().setDisplayName(value)
            user.updateProfile(usrProfileChangeBuilder.build()).addOnCompleteListener {
                updateUserDocument()
                Log.d(TAG, "Username Update Success")
            }.addOnFailureListener {
                Log.w(TAG, "Error while updating username")
            }
        }

    var email: String
        get() = user.email!!
        set(value) {
            user.updateEmail(value).addOnCompleteListener {
                updateUserDocument()
                Log.d(TAG, "Email Update Success")
            }.addOnFailureListener {
                Log.w(TAG, "Error while updating email")
            }
        }

    /**
     * The profile photo of the user.
     * No default value is provided if no user photo is available.
     */
    var photoUrl: Uri?
        get() = user.photoUrl
        set(value) {
            val photoRef = storage.reference.child("images/profile/${user.uid}")
            photoRef.putFile(value!!).addOnCompleteListener {
                photoRef.downloadUrl.addOnCompleteListener {
                    val updates =
                        userProfileChangeRequest {
                            photoUri = it.result
                        }
                    user.updateProfile(updates).addOnCompleteListener {
                        updateUserDocument()
                        Log.d(TAG, "Photo URI Update Success")
                    }.addOnFailureListener {
                        Log.w(TAG, "Error while updating Photo URI")
                    }
                }
            }.addOnFailureListener {
                Log.w(TAG, "Error while updating Photo URI")
            }
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

    // TODO: Add friends
    val friends: List<User> = listOf()

    val workGroup: List<User> = listOf()

    val friendsNotInWorkGroup: List<User> = friends.filter { it !in workGroup }

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

    private fun updateUserDocument() {
        val user = User(username, email, user.uid, photoUrl)

        db.collection(User.COLLECTION_NAME).document(user.uid)
            .set(user.toHashMap(), SetOptions.merge()).addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: $documentReference")
            }.addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }
}