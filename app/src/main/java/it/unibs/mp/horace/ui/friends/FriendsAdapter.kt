package it.unibs.mp.horace.ui.friends

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
import it.unibs.mp.horace.backend.User

class FriendsAdapter(private val dataset: List<User>) :
    RecyclerView.Adapter<FriendsAdapter.ItemViewHolder>(), Filterable {

    private val filteredDataset: ArrayList<User> = arrayListOf<User>().apply { addAll(dataset) }

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val profilePhoto: ImageView = view.findViewById(R.id.profilePhoto)
        val username: TextView = view.findViewById(R.id.username)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout =
            LayoutInflater.from(parent.context).inflate(R.layout.friend_item, parent, false)

        return ItemViewHolder(adapterLayout)
    }

    override fun getItemCount(): Int {
        return filteredDataset.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = filteredDataset[position]
        holder.profilePhoto.load(item.photoUrl ?: R.drawable.default_profile_photo)
        holder.username.text = item.username
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                return FilterResults().apply {
                    values = when {
                        constraint.isNullOrEmpty() -> dataset
                        else -> dataset.filter { it.fitsSearch(constraint.toString()) }
                    }
                }
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                if (results?.values is ArrayList<*>) {
                    filteredDataset.clear()

                    @Suppress("UNCHECKED_CAST")
                    filteredDataset.addAll(results.values as ArrayList<User>)

                    @Suppress("NotifyDataSetChanged")
                    notifyDataSetChanged()
                }
            }
        }
    }
}
