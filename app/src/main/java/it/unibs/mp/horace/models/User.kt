package it.unibs.mp.horace.models

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