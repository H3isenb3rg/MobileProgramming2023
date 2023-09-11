package it.unibs.mp.horace.ui.leaderboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import it.unibs.mp.horace.R
import it.unibs.mp.horace.backend.firebase.models.User

class SuggestedFriendsAdapter(
    private val dataset: MutableList<User>, private val sendFriendRequest: (User) -> Unit
) : RecyclerView.Adapter<SuggestedFriendsAdapter.ItemViewHolder>() {

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val profilePhoto: ImageView = view.findViewById(R.id.image_view_photo)
        val username: TextView = view.findViewById(R.id.textview_username)
        val email: TextView = view.findViewById(R.id.textview_email)
        val sendFriendRequest: Button = view.findViewById(R.id.button_send_friend_request)
    }

    // Called when RecyclerView needs a new ViewHolder of the given type.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_suggested_friend, parent, false)

        return ItemViewHolder(adapterLayout)
    }

    // Total number of items in the data set held by the adapter.
    override fun getItemCount(): Int {
        return dataset.size
    }

    // Called by RecyclerView to display the data at the specified position.
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = dataset[position]

        holder.profilePhoto.load(item.profilePhoto)
        holder.username.text = item.username
        holder.email.text = item.email

        holder.sendFriendRequest.setOnClickListener {
            holder.sendFriendRequest.isEnabled = false
            sendFriendRequest(item)
        }
    }
}