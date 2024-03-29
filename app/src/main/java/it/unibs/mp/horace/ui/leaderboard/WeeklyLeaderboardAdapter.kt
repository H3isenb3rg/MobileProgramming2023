package it.unibs.mp.horace.ui.leaderboard

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import it.unibs.mp.horace.R
import it.unibs.mp.horace.backend.LeaderboardItem

class WeeklyLeaderboardAdapter(
    private val dataset: List<LeaderboardItem>,
    private val context: Context
) : RecyclerView.Adapter<WeeklyLeaderboardAdapter.ItemViewHolder>() {

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val photo: ImageView = view.findViewById(R.id.imageview_photo)
        val position: TextView = view.findViewById(R.id.textview_position)
        val username: TextView = view.findViewById(R.id.textview_username)
        val points: TextView = view.findViewById(R.id.textview_points)
    }

    // Called when RecyclerView needs a new ViewHolder of the given type.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_leaderboard, parent, false)

        return ItemViewHolder(adapterLayout)
    }

    // Total number of items in the data set held by the adapter.
    override fun getItemCount(): Int {
        return dataset.size
    }

    // Called by RecyclerView to display the data at the specified position.
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = dataset[position]
        val leaderboardPosition = position + 1

        holder.photo.load(item.user.profilePhoto)
        holder.position.text = leaderboardPosition.toString()
        holder.username.text = item.user.username
        holder.points.text = context.getString(R.string.entry_points, item.points)
    }
}