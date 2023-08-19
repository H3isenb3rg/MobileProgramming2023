package it.unibs.mp.horace.backend.firebase.models

import android.net.Uri
import it.unibs.mp.horace.R

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
        const val USERNAME_FIELD = "username"

        /**
         * Parses a [Map] into a [User] object.
         */
        fun parse(data: Map<String, Any>): User {
            val uid = data[UID_FIELD].toString()
            val email = data[EMAIL_FIELD].toString()
            val username = if (data[USERNAME_FIELD] != null) {
                data[USERNAME_FIELD].toString()
            } else {
                null
            }
            val photoUrl = if (data[PHOTO_URL_FIELD] != null) {
                Uri.parse(data[PHOTO_URL_FIELD].toString())
            } else {
                null
            }

            return User(uid, email, username, photoUrl)
        }
    }

    /**
     * Convert the user to a map of data.
     */
    fun stringify(): Map<String, Any> {
        val data = mutableMapOf<String, Any>()
        data[UID_FIELD] = uid
        data[EMAIL_FIELD] = email

        if (username != null) {
            data[USERNAME_FIELD] = username.toString()
        }

        if (photoUrl != null) {
            data[PHOTO_URL_FIELD] = photoUrl.toString()
        }

        return data
    }

    // No-argument constructor required for Firestore.
    constructor() : this("", "", null)

    /**
     * Returns the user's profile photo, or the default one if it's null.
     */
    val profilePhoto: Any get() = photoUrl ?: R.drawable.ic_default_profile_photo

    /**
     * Checks if the user matches the search text.
     */
    fun fitsSearch(searchText: String): Boolean {
        return (username?.lowercase()
            ?.contains(searchText.lowercase()) == true) || email.lowercase()
            .contains(searchText.lowercase())
    }
}