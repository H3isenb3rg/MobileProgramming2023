package it.unibs.mp.horace.backend

import android.net.Uri

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
        const val EMAIL_FIELD = "email"
        const val USERNAME_FIELD = "username"
        const val PHOTO_FIELD = "photo_uri"
        const val UID_FIELD = "uid"
    }

    fun fitsSearch(searchText: String): Boolean {
        return (username?.lowercase()
            ?.contains(searchText.lowercase()) == true) || email.lowercase()
            .contains(searchText.lowercase())
    }

    fun toHashMap(): HashMap<String, String> {
        return hashMapOf(
            EMAIL_FIELD to email,
            UID_FIELD to uid,
            USERNAME_FIELD to (username ?: ""),
            PHOTO_FIELD to (photoUrl?.toString() ?: "")
        )
    }
}