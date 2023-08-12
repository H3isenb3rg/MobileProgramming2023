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

    // Will only contain friends to which a friend request has not been sent yet
    private val suggestedFriends: MutableList<User> =
        mutableListOf<User>().apply { addAll(dataset) }

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val profilePhoto: ImageView = view.findViewById(R.id.profilePhoto)
        val username: TextView = view.findViewById(R.id.username)
        val email: TextView = view.findViewById(R.id.email)
        val sendFriendRequest: Button = view.findViewById(R.id.sendFriendRequest)
    }

    // Called when RecyclerView needs a new ViewHolder of the given type.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.suggested_friend_item, parent, false)

        return ItemViewHolder(adapterLayout)
    }

    // Total number of items in the data set held by the adapter.
    override fun getItemCount(): Int {
        return suggestedFriends.size
    }

    // Called by RecyclerView to display the data at the specified position.
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = suggestedFriends[position]

        holder.profilePhoto.load(item.profilePhoto)
        holder.username.text = item.username
        holder.email.text = item.email

        holder.sendFriendRequest.setOnClickListener {
            holder.sendFriendRequest.isEnabled = false

            suggestedFriends.removeAt(position)
            notifyItemRemoved(position)

            sendFriendRequest(item)
        }
    }
}