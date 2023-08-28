package it.unibs.mp.horace.backend.firebase.models

import java.time.LocalDateTime

data class Notification(
    val id: String,
    val type: Int,
    val senderUid: String?,
    val timeSent: LocalDateTime = LocalDateTime.now(),
    var isAccepted: Boolean = false,
    var isRead: Boolean = false
) {
    companion object {
        const val COLLECTION_NAME = "notifications"

        const val ID_FIELD = "id"
        const val TYPE_FIELD = "type"
        const val SENDER_UID_FIELD = "senderUid"
        const val TIME_SENT_FIELD = "timeSent"
        const val IS_ACCEPTED_FIELD = "isAccepted"
        const val IS_READ_FIELD = "isRead"

        const val TYPE_FRIEND_INVITATION = 0
        const val TYPE_WORKGROUP_INVITATION = 1
        const val TYPE_FRIEND_ACCEPTED = 2
        const val TYPE_WORKGROUP_ACCEPTED = 3

        const val DEFAULT_EXPIRATION_DAYS = 1
        const val FRIEND_INVITATION_EXPIRATION_DAYS = 7
        const val WORKGROUP_INVITATION_EXPIRATION_DAYS = 1

        fun parse(data: Map<String, Any>): Notification {
            return Notification(
                id = data["id"] as String,
                type = (data["type"] as Long).toInt(),
                senderUid = data["senderUid"] as String?,
                timeSent = LocalDateTime.parse(data["dateSent"] as String),
                isAccepted = data["isAccepted"] as Boolean? ?: false,
                isRead = data["isRead"] as Boolean? ?: false
            )
        }
    }

    // No-argument constructor required for Firestore.
    @Suppress("unused")
    constructor() : this("", 0, "")

    fun stringify(): HashMap<String, Any> {
        val entryMap: HashMap<String, Any> = hashMapOf(
            ID_FIELD to id,
            TYPE_FIELD to type.toString(),
            TIME_SENT_FIELD to timeSent.toString(),
            IS_ACCEPTED_FIELD to isAccepted,
            IS_READ_FIELD to isRead
        )

        senderUid?.let {
            entryMap[SENDER_UID_FIELD] = senderUid.toString()
        }

        return entryMap
    }

    private val expiresAfterDays = when (type) {
        TYPE_FRIEND_INVITATION -> FRIEND_INVITATION_EXPIRATION_DAYS
        TYPE_WORKGROUP_INVITATION -> WORKGROUP_INVITATION_EXPIRATION_DAYS
        else -> DEFAULT_EXPIRATION_DAYS
    }

    /**
     * Checks if the notification is expired.
     */
    val isExpired: Boolean
        get() = timeSent
            .plusDays(expiresAfterDays.toLong()) < LocalDateTime.now()
}