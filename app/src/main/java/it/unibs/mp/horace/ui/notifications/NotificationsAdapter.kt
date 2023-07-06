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
import it.unibs.mp.horace.backend.Invitation

class NotificationsAdapter(
    private val context: Context, private val dataset: List<Invitation>
) : RecyclerView.Adapter<NotificationsAdapter.ItemViewHolder>() {

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var message: TextView = view.findViewById(R.id.message)
        var icon: ImageView = view.findViewById(R.id.icon)
        var action: Button = view.findViewById(R.id.action)
    }

    // Called when RecyclerView needs a new ViewHolder of the given type.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout =
            LayoutInflater.from(parent.context).inflate(R.layout.notification, parent, false)

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

        if (item.type == Invitation.TYPE_FRIEND_INVITATION) {
            holder.icon.setImageResource(R.drawable.baseline_person_add_24)
            holder.message.text = context.getString(R.string.friend_request, item.sender.username)
            holder.action.text = context.getString(R.string.accept)
        } else {
            holder.icon.setImageResource(R.drawable.baseline_people_alt_24)
            holder.message.text =
                context.getString(R.string.workgroup_request, item.sender.username)
            holder.action.text = context.getString(R.string.join)
        }
    }
}