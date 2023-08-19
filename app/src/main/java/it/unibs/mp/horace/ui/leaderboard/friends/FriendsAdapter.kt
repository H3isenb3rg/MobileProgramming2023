package it.unibs.mp.horace.ui.leaderboard.friends

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import it.unibs.mp.horace.R
import it.unibs.mp.horace.backend.firebase.models.User

/**
 * RecyclerView adapter for the friends list.
 * Implementing the Filterable interface allows us to filter the dataset.
 */
class FriendsAdapter(private val dataset: ArrayList<User>) :
    RecyclerView.Adapter<FriendsAdapter.ItemViewHolder>(), Filterable {

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val profilePhoto: ImageView = view.findViewById(R.id.profilePhoto)
        val username: TextView = view.findViewById(R.id.username)
        val email: TextView = view.findViewById(R.id.email)
    }

    // Contains only the items that match the search query.
    // The elements of the original dataset are copied.
    private val filteredDataset: ArrayList<User> = arrayListOf<User>().apply { addAll(dataset) }

    // Called when RecyclerView needs a new ViewHolder of the given type.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout =
            LayoutInflater.from(parent.context).inflate(R.layout.item_friend, parent, false)

        return ItemViewHolder(adapterLayout)
    }

    // Total number of items in the data set held by the adapter.
    override fun getItemCount(): Int {
        return filteredDataset.size
    }

    // Called by RecyclerView to display the data at the specified position.
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = filteredDataset[position]

        holder.profilePhoto.load(item.photoUrl ?: R.drawable.ic_default_profile_photo)
        holder.username.text = item.username
        holder.email.text = item.email
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
                    @Suppress("UNCHECKED_CAST") filteredDataset.addAll(results.values as ArrayList<User>)

                    // Tracking what changed in the dataset is too expensive,
                    // so we just notify the adapter that the whole dataset changed.
                    @Suppress("NotifyDataSetChanged") notifyDataSetChanged()
                }
            }
        }
    }
}
