package it.unibs.mp.horace.backend

import android.net.Uri
import java.net.URI

data class User(var name: String?, var email: String, var uid: String, var photoURI: Uri? = null) {
    companion object {
        const val COLLECTION_NAME: String = "users"
        const val EMAIL_FIELD: String = "email"
        const val USERNAME_FIELD: String = "username"
        const val PHOTO_FIELD: String = "photo_uri"
        const val UID_FIELD: String = "uid"
    }

    fun toHashMap(): HashMap<String, String> {
        val map = hashMapOf(
            EMAIL_FIELD to email,
            UID_FIELD to uid
        )
        name?.let { map[USERNAME_FIELD] = it }
        photoURI?.let { map[PHOTO_FIELD] = it.toString() }

        return map
    }
}