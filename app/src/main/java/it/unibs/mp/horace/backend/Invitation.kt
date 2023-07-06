package it.unibs.mp.horace.backend

import com.google.firebase.firestore.DocumentId
import java.time.LocalDateTime

data class Invitation(
    @DocumentId
    val id: String,
    val senderUid: String,
    val type: Int,
    val dateSent: LocalDateTime = LocalDateTime.now(),
    var accepted: Boolean = false,
) {

    companion object {
        const val COLLECTION_NAME = "invitations"
        const val TYPE_FRIEND_INVITATION = 0
        const val TYPE_WORKGROUP_INVITATION = 0

        const val FRIEND_INVITATION_EXPIRATION_DAYS = 7
        const val WORKGROUP_INVITATION_EXPIRATION_DAYS = 1
    }

    private val expiresAfterDays =
        when (type) {
            TYPE_FRIEND_INVITATION -> FRIEND_INVITATION_EXPIRATION_DAYS
            else -> WORKGROUP_INVITATION_EXPIRATION_DAYS
        }

    val isExpired: Boolean
        get() = dateSent.plusDays(expiresAfterDays.toLong()).isBefore(LocalDateTime.now())
}