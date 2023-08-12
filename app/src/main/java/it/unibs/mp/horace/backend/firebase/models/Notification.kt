package it.unibs.mp.horace.backend.firebase.models

import java.time.LocalDateTime

data class Notification(
    val id: String,
    val type: Int,
    val senderUid: String?,
    val dateSent: String = LocalDateTime.now().toString(),
    var isAccepted: Boolean = false,
    var isRead: Boolean = false
) {
    companion object {
        const val COLLECTION_NAME = "notifications"

        const val TYPE_FRIEND_INVITATION = 0
        const val TYPE_WORKGROUP_INVITATION = 1
        const val TYPE_FRIEND_ACCEPTED = 2
        const val TYPE_WORKGROUP_ACCEPTED = 3

        const val DEFAULT_EXPIRATION_DAYS = 1
        const val FRIEND_INVITATION_EXPIRATION_DAYS = 7
        const val WORKGROUP_INVITATION_EXPIRATION_DAYS = 1

        const val IS_READ_FIELD = "isRead"

        fun parse(data: Map<String, Any>): Notification {
            return Notification(
                id = data["id"] as String,
                type = (data["type"] as Long).toInt(),
                senderUid = data["senderUid"] as String?,
                dateSent = data["dateSent"] as String,
                isAccepted = data["isAccepted"] as Boolean,
                isRead = data["isRead"] as Boolean
            )
        }
    }

    // No-argument constructor required for Firestore.
    @Suppress("unused")
    constructor() : this("", 0, null)

    private val expiresAfterDays = when (type) {
        TYPE_FRIEND_INVITATION -> FRIEND_INVITATION_EXPIRATION_DAYS
        TYPE_WORKGROUP_INVITATION -> WORKGROUP_INVITATION_EXPIRATION_DAYS
        else -> DEFAULT_EXPIRATION_DAYS
    }

    /**
     * Checks if the notification is expired.
     */
    val isExpired: Boolean
        get() = LocalDateTime.parse(dateSent)
            .plusDays(expiresAfterDays.toLong()) < LocalDateTime.now()
}