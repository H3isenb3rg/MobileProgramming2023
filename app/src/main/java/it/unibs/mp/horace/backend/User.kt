package it.unibs.mp.horace.backend

import android.net.Uri

data class User(
    var username: String, var email: String, var uid: String, var photoUrl: Uri? = null
) {
    companion object {
        const val COLLECTION_NAME = "users"
        const val EMAIL_FIELD = "email"
        const val USERNAME_FIELD = "username"
        const val PHOTO_FIELD = "photo_uri"
        const val UID_FIELD = "uid"
    }

    fun toHashMap(): HashMap<String, String> {
        return hashMapOf(
            EMAIL_FIELD to email,
            UID_FIELD to uid,
            USERNAME_FIELD to (username ?: ""),
            PHOTO_FIELD to (photoUrl?.toString() ?: "")
        )
    }

    fun fitsSearch(searchText: String): Boolean {
        return username.lowercase().contains(searchText.lowercase()) || email.lowercase()
            .contains(searchText.lowercase())
    }
}