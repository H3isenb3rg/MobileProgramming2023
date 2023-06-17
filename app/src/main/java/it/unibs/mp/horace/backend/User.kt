package it.unibs.mp.horace.backend

data class User(var name: String, var email: String, var uid: String) {
    companion object {
        const val COLLECTION_NAME: String = "users"
        const val EMAIL_FIELD: String = "email"
        const val USERNAME_FIELD: String = "username"
        const val UID_FIELD: String = "uid"
    }
}