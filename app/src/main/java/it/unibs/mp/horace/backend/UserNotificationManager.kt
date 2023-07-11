package it.unibs.mp.horace.backend

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import it.unibs.mp.horace.backend.User.Companion.FRIENDS_COLLECTION_NAME
import it.unibs.mp.horace.backend.User.Companion.WORKGROUP_COLLECTION_NAME
import kotlinx.coroutines.tasks.await

class UserNotificationManager {
    private val user = CurrentUser()
    private val db: FirebaseFirestore = Firebase.firestore
    private val userDocument: DocumentReference =
        db.collection(User.COLLECTION_NAME).document(user.uid)

    /**
     * The notifications sent to the current user.
     */
    suspend fun notifications(): List<Notification> {
        return userDocument.collection(Notification.COLLECTION_NAME).get().await()
            .toObjects(Notification::class.java)
    }

    /**
     * Marks the specified notification as read.
     */
    suspend fun markNotificationAsRead(notification: Notification) {
        // If the notification is already read, don't do anything
        if (notification.isRead) {
            return
        }

        notification.isRead = true
        userDocument.collection(Notification.COLLECTION_NAME).document(notification.id)
            .set(notification).await()
    }

    /**
     * Accepts the specified notification.
     */
    suspend fun acceptInvitation(notification: Notification) {
        if (notification.senderUid == null) {
            throw IllegalArgumentException("The notification must have a sender.")
        }

        // The document of the sender.
        val senderDocument = db.collection(User.COLLECTION_NAME).document(notification.senderUid)

        // The response type.
        var responseType: Int = Notification.TYPE_FRIEND_ACCEPTED

        if (notification.type == Notification.TYPE_FRIEND_INVITATION) {
            // Add invitation sender to the current user friends.
            userDocument.collection(FRIENDS_COLLECTION_NAME).add(Friend(notification.senderUid))
                .await()

            // Add current user to the invitation sender friends.
            senderDocument.collection(FRIENDS_COLLECTION_NAME).add(Friend(user.uid)).await()
        } else {
            userDocument.collection(WORKGROUP_COLLECTION_NAME)
                .add(WorkGroupMember(notification.senderUid)).await()

            senderDocument.collection(WORKGROUP_COLLECTION_NAME).add(WorkGroupMember(user.uid))
                .await()

            responseType = Notification.TYPE_WORKGROUP_ACCEPTED
        }

        // Generate a new notification ID
        val response = senderDocument.collection(Notification.COLLECTION_NAME).document()
        // Set actual content of the response notification
        response.set(Notification(response.id, responseType, user.uid)).await()

        // Update the invitation status
        notification.isAccepted = true
        userDocument.collection(Notification.COLLECTION_NAME).document(notification.id)
            .set(notification).await()
    }

    /**
     * Sends a friend invitation to the specified user.
     */
    suspend fun sendFriendRequest(user: User) {
        sendInvitation(user, Notification.TYPE_FRIEND_INVITATION)
    }

    /**
     * Sends a work group invitation to the specified user.
     */
    suspend fun sendWorkGroupRequest(user: User) {
        sendInvitation(user, Notification.TYPE_WORKGROUP_INVITATION)
    }

    /**
     * Sends an invitation to the specified user.
     */
    private suspend fun sendInvitation(destinationUser: User, type: Int) {
        val collection = if (type == Notification.TYPE_FRIEND_INVITATION) {
            FRIENDS_COLLECTION_NAME
        } else {
            WORKGROUP_COLLECTION_NAME
        }

        // Check if the user is already a friend/workgroup member
        if (userDocument.collection(collection).whereEqualTo(User.UID_FIELD, destinationUser.uid)
                .get().await().any()
        ) {
            throw IllegalArgumentException()
        }

        // The invitation document reference for the destination user
        val destInvitations = db.collection(User.COLLECTION_NAME).document(destinationUser.uid)
            .collection(Notification.COLLECTION_NAME)

        // Check if there's already a pending invitation of the same type sent by the current user
        val hasPendingInvitation = destInvitations.get().await().any {
            val notification = it.toObject(Notification::class.java)
            notification.type == type && !notification.isExpired && notification.senderUid == destinationUser.uid
        }

        // If there's already a pending invitation, don't send another one
        if (hasPendingInvitation) {
            return
        }

        // Otherwise, send the invitation
        val ref = destInvitations.document()
        ref.set(Notification(ref.id, type, user.uid)).await()
    }


    fun addOnNotificationListener(callback: (List<Notification>) -> Unit) {
        userDocument.collection(Notification.COLLECTION_NAME)
            .whereEqualTo(Notification.IS_READ_FIELD, false).addSnapshotListener { snapshot, e ->
                if (e != null) {
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val notifications = snapshot.toObjects(Notification::class.java)
                    if (notifications.isNotEmpty()) {
                        callback(notifications)
                    }
                }
            }
    }
}
