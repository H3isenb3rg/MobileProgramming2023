package it.unibs.mp.horace.ui.friends

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import coil.load
import it.unibs.mp.horace.R
import it.unibs.mp.horace.backend.LoggedUser
import it.unibs.mp.horace.backend.User

/**
 * RecyclerView adapter for the friends list.
 */
class FriendsAdapter(private val dataset: ArrayList<User>) :
    RecyclerView.Adapter<FriendsAdapter.ItemViewHolder>(), Filterable {

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val profilePhoto: ImageView = view.findViewById(R.id.profilePhoto)
        val username: TextView = view.findViewById(R.id.username)
        val addFriend: Button = view.findViewById(R.id.addFriend)
    }

    // Contains only the items that match the search query.
    private val filteredDataset: ArrayList<User> = arrayListOf<User>().apply { addAll(dataset) }

    // Called when RecyclerView needs a new ViewHolder of the given type.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout =
            LayoutInflater.from(parent.context).inflate(R.layout.friend_item, parent, false)

        return ItemViewHolder(adapterLayout)
    }

    // Total number of items in the data set held by the adapter.
    override fun getItemCount(): Int {
        return filteredDataset.size
    }

    // Called by RecyclerView to display the data at the specified position.
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = filteredDataset[position]

        val currentUser = LoggedUser()

        holder.profilePhoto.load(item.photoUrl ?: R.drawable.default_profile_photo)
        holder.username.text = item.username
        holder.addFriend.isVisible = !currentUser.friends.contains(item)
    }

    // Returns a filter that can be used to search the dataset.
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                return FilterResults().apply {
                    values = when {
                        // If the search query is empty, return the original dataset.
                        constraint.isNullOrEmpty() -> dataset

                        // Otherwise, check if there are items that match the search query.
                        // If there are no matches, search the database for users with
                        // the given email or username.
                        else -> dataset.filter { it.fitsSearch(constraint.toString()) }
                    }
                }
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                if (results?.values is ArrayList<*>) {
                    filteredDataset.clear()

                    // Here we are sure that the values are of type ArrayList<User>.
                    // So the cast is safe.
                    @Suppress("UNCHECKED_CAST")
                    filteredDataset.addAll(results.values as ArrayList<User>)

                    // Tracking what changed in the dataset is too expensive,
                    // so we just notify the adapter that the whole dataset changed.
                    @Suppress("NotifyDataSetChanged")
                    notifyDataSetChanged()
                }
            }
        }
    }
}
