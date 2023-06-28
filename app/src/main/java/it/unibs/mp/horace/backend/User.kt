package it.unibs.mp.horace.backend

import android.net.Uri

data class User(
    var uid: String,
    var username: String,
    var email: String,
    var photoUrl: Uri? = null,
    var fcmToken: String? = null
) {
    companion object {
        const val COLLECTION_NAME = "users"
        const val EMAIL_FIELD = "email"
        const val USERNAME_FIELD = "username"
        const val PHOTO_FIELD = "photo_uri"
        const val UID_FIELD = "uid"
        const val FCM_TOKEN_FIELD = "fcm_token"

        fun fromHashMap(hashMap: HashMap<String, Any>): User {
            val photoUri = if ((hashMap[PHOTO_FIELD] as String) == "") {
                null
            } else {
                Uri.parse(hashMap[PHOTO_FIELD] as String)
            }

            val fcmToken = if ((hashMap[FCM_TOKEN_FIELD] as String) == "") {
                null
            } else {
                hashMap[FCM_TOKEN_FIELD] as String
            }

            return User(
                hashMap[UID_FIELD] as String,
                hashMap[USERNAME_FIELD] as String,
                hashMap[EMAIL_FIELD] as String,
                photoUri,
                fcmToken
            )
        }
    }

    fun fitsSearch(searchText: String): Boolean {
        return username.lowercase().contains(searchText.lowercase()) || email.lowercase()
            .contains(searchText.lowercase())
    }

    fun toHashMap(): HashMap<String, String> {
        return hashMapOf(
            EMAIL_FIELD to email,
            UID_FIELD to uid,
            USERNAME_FIELD to (username),
            PHOTO_FIELD to (photoUrl?.toString() ?: ""),
            FCM_TOKEN_FIELD to (fcmToken ?: "")
        )
    }
}