package it.unibs.mp.horace.ui.leaderboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import it.unibs.mp.horace.R
import it.unibs.mp.horace.backend.LeaderboardItem

class WeeklyLeaderboardAdapter(
    private val dataset: List<LeaderboardItem>
) : RecyclerView.Adapter<WeeklyLeaderboardAdapter.ItemViewHolder>() {

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val position: TextView = view.findViewById(R.id.position)
        val username: TextView = view.findViewById(R.id.username)
        val points: TextView = view.findViewById(R.id.points)
    }

    // Called when RecyclerView needs a new ViewHolder of the given type.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.suggested_friend_item, parent, false)

        return ItemViewHolder(adapterLayout)
    }

    // Total number of items in the data set held by the adapter.
    override fun getItemCount(): Int {
        return dataset.size
    }

    // Called by RecyclerView to display the data at the specified position.
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = dataset[position]

        holder.position.text = position.toString()
        holder.username.text = item.user.username
        holder.points.text = item.points.toString()
    }
}