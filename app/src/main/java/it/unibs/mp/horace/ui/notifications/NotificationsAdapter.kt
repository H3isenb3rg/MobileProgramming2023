package it.unibs.mp.horace.ui.notifications

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import it.unibs.mp.horace.R
import it.unibs.mp.horace.backend.Notification

class NotificationsAdapter(
    private val context: Context, private val dataset: List<Notification>
) : RecyclerView.Adapter<NotificationsAdapter.ItemViewHolder>() {

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var message: TextView = view.findViewById(R.id.message)
        var icon: ImageView = view.findViewById(R.id.icon)
        var action: Button = view.findViewById(R.id.action)
    }

    override fun getItemViewType(position: Int): Int {
        return dataset[position].type
    }

    // Called when RecyclerView needs a new ViewHolder of the given type.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val layout = when (viewType) {
            Notification.TYPE_FRIEND_INVITATION -> R.layout.notification_friend_request
            Notification.TYPE_WORKGROUP_INVITATION -> R.layout.notification_workgroup_request
            Notification.TYPE_FRIEND_ACCEPTED -> R.layout.notification_friend_accepted
            Notification.TYPE_WORKGROUP_ACCEPTED -> R.layout.notification_workgroup_accepted
            else -> R.layout.notification_friend_request
        }

        val adapterLayout = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return ItemViewHolder(adapterLayout)
    }

    // Total number of items in the data set held by the adapter.
    override fun getItemCount(): Int {
        return dataset.size
    }

    // Called by RecyclerView to display the data at the specified position.
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = dataset[position]

        if (item.isExpired) {
            holder.icon.visibility = View.INVISIBLE
            holder.action.visibility = View.INVISIBLE
        }

        holder.message.text = when (item.type) {
            Notification.TYPE_FRIEND_INVITATION -> context.getString(
                R.string.friend_request, item.sender.username
            )

            Notification.TYPE_WORKGROUP_INVITATION -> context.getString(
                R.string.workgroup_request, item.sender.username
            )

            Notification.TYPE_FRIEND_ACCEPTED -> context.getString(
                R.string.friend_accepted, item.sender.username
            )

            Notification.TYPE_WORKGROUP_ACCEPTED -> context.getString(
                R.string.workgroup_accepted, item.sender.username
            )

            else -> context.getString(
                R.string.text_goes_here
            )
        }
    }
}