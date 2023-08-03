package it.unibs.mp.horace.models

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import it.unibs.mp.horace.R
import it.unibs.mp.horace.backend.CurrentUser
import kotlinx.coroutines.tasks.await

data class User(
    var uid: String,
    var email: String,
    var username: String?,
    var photoUrl: Uri? = null
) {
    companion object {
        const val COLLECTION_NAME = "users"
        const val FRIENDS_COLLECTION_NAME = "friends"
        const val WORKGROUP_COLLECTION_NAME = "workgroup"
        const val UID_FIELD = "uid"
        const val EMAIL_FIELD = "email"
        const val PHOTO_URL_FIELD = "photoUrl"
        const val PROFILE_PHOTO_FIELD = "profilePhoto"
        const val USERNAME_FIELD = "username"
        suspend fun fetchUser(uid: String): User? {
            var user: User? = null
            Firebase.firestore.collection(COLLECTION_NAME).document(uid).get()
                .addOnSuccessListener {
                    if (it == null) {
                        user = null
                        return@addOnSuccessListener
                    }
                    user = it.data?.let { it1 -> User.parse(it1) }
                }.addOnFailureListener {
                    user = null
                }.await()
            return user
        }

        fun parse(raw_data: Map<String, Any>): User {
            val uid = raw_data[UID_FIELD].toString()
            val email = raw_data[EMAIL_FIELD].toString()

            var username: String? = null
            if (raw_data[USERNAME_FIELD] != null) {
                username = raw_data[USERNAME_FIELD].toString()
            }

            var photoUrl: Uri? = null
            if (raw_data[PHOTO_URL_FIELD] != null) {
                photoUrl = Uri.parse(raw_data[PROFILE_PHOTO_FIELD].toString())
            }

            return User(uid, email, username, photoUrl)
        }
    }

    // No-argument constructor required for Firestore.
    constructor() : this("", "", null)

    /**
     * Returns the user's profile photo, or the default one if it's null.
     */
    val profilePhoto: Any get() = photoUrl ?: R.drawable.default_profile_photo

    /**
     * Checks if the user matches the search text.
     */
    fun fitsSearch(searchText: String): Boolean {
        return (username?.lowercase()
            ?.contains(searchText.lowercase()) == true) || email.lowercase()
            .contains(searchText.lowercase())
    }
}