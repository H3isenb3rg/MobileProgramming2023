package it.unibs.mp.horace.ui.notifications

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.android.material.card.MaterialCardView
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import it.unibs.mp.horace.R
import it.unibs.mp.horace.backend.firebase.models.Notification
import it.unibs.mp.horace.backend.firebase.models.User

class NotificationsAdapter(
    private val context: Context,
    private val dataset: List<Notification>,
    private val onAction: (Notification) -> Unit
) : RecyclerView.Adapter<NotificationsAdapter.ItemViewHolder>() {

    private val usersCollection: CollectionReference =
        Firebase.firestore.collection(User.COLLECTION_NAME)

    // The base class for the view holder.
    open class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var message: TextView = view.findViewById(R.id.textview_message)
        var action: Button = view.findViewById(R.id.button_action)
        var container: MaterialCardView = view.findViewById(R.id.notificationContainer)
    }

    // View holder for friend notifications.
    class FriendViewHolder(view: View) : ItemViewHolder(view) {
        var profilePhoto: ImageView = view.findViewById(R.id.image_view_photo)
    }

    // View holder for workgroup notifications.
    class WorkGroupViewHolder(view: View) : ItemViewHolder(view) {
        var icon: ImageView = view.findViewById(R.id.imageview_icon)
    }

    // Returns the view type of the item at position for the purposes of view recycling.
    override fun getItemViewType(position: Int): Int {
        return dataset[position].type
    }

    // Called when RecyclerView needs a new ViewHolder of the given type.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        fun inflate(layout: Int) =
            LayoutInflater.from(parent.context).inflate(layout, parent, false)

        return when (viewType) {
            Notification.TYPE_FRIEND_INVITATION -> FriendViewHolder(inflate(R.layout.notification_friend_request))
            Notification.TYPE_FRIEND_ACCEPTED -> FriendViewHolder(inflate(R.layout.notification_friend_accepted))
            Notification.TYPE_WORKGROUP_INVITATION -> WorkGroupViewHolder(inflate(R.layout.notification_workgroup_request))
            Notification.TYPE_WORKGROUP_ACCEPTED -> WorkGroupViewHolder(inflate(R.layout.notification_workgroup_accepted))
            else -> throw IllegalStateException("Invalid notification type")
        }
    }

    // Total number of items in the data set held by the adapter.
    override fun getItemCount(): Int {
        return dataset.size
    }

    // Called by RecyclerView to display the data at the specified position.
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = dataset[position]

        if (item.isExpired) {
            holder.container.setBackgroundColor(com.google.android.material.R.attr.colorSurface)
            holder.action.visibility = View.INVISIBLE
        }

        // If the notification has no sender, we can stop here.
        if (item.senderUid == null) {
            return
        }

        // Retrieve the sender's data.
        usersCollection.document(item.senderUid).get().addOnSuccessListener {
            // This should never fail.
            val user = it.toObject(User::class.java)!!

            // Populate the view holder depending on the notification type.
            when (item.type) {
                Notification.TYPE_FRIEND_INVITATION -> {
                    (holder as FriendViewHolder).apply {
                        message.text = context.getString(R.string.friend_request, user.username)
                        profilePhoto.load(user.profilePhoto)
                        action.isEnabled = !item.isExpired && !item.isAccepted
                        action.setOnClickListener {
                            onAction(item)
                        }
                    }
                }

                Notification.TYPE_WORKGROUP_INVITATION -> {
                    (holder as WorkGroupViewHolder).apply {
                        message.text = context.getString(R.string.workgroup_request, user.username)
                        action.isEnabled = !item.isExpired && !item.isAccepted
                        action.setOnClickListener {
                            onAction(item)
                        }
                    }
                }

                Notification.TYPE_FRIEND_ACCEPTED -> {
                    (holder as FriendViewHolder).apply {
                        message.text = context.getString(R.string.friend_accepted, user.username)
                        profilePhoto.load(user.profilePhoto)
                    }
                }

                Notification.TYPE_WORKGROUP_ACCEPTED -> {
                    (holder as WorkGroupViewHolder).message.text = context.getString(
                        R.string.workgroup_accepted, user.username
                    )
                }
            }
        }
    }
}