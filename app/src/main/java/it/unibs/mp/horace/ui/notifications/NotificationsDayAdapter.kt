package it.unibs.mp.horace.ui.notifications

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import it.unibs.mp.horace.R
import it.unibs.mp.horace.backend.DateTimeFormatter
import it.unibs.mp.horace.backend.firebase.NotificationDay
import it.unibs.mp.horace.backend.firebase.models.Notification

class NotificationsDayAdapter(
    private val context: Context,
    private val dataset: List<NotificationDay>,
    private val onAction: (Notification) -> Unit
) : RecyclerView.Adapter<NotificationsDayAdapter.ItemViewHolder>() {

    private val dateTimeFormatter = DateTimeFormatter(context)

    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var date: TextView = view.findViewById(R.id.textview_date)
        var notifications: RecyclerView = view.findViewById(R.id.recyclerview_notifications)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val layout = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notification_day, parent, false)
        return ItemViewHolder(layout)
    }

    // Total number of items in the data set held by the adapter.
    override fun getItemCount(): Int {
        return dataset.size
    }

    // Called by RecyclerView to display the data at the specified position.
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = dataset[position]

        holder.date.text = dateTimeFormatter.formatDate(item.date)
        holder.notifications.adapter = NotificationsAdapter(context, item.notifications, onAction)
    }
}