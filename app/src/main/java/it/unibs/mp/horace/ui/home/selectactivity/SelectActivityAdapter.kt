package it.unibs.mp.horace.ui.home.selectactivity

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import it.unibs.mp.horace.R
import it.unibs.mp.horace.backend.firebase.models.Activity

class SelectActivityAdapter(
    private val dataset: List<Activity>,
    private val onItemClick: (Activity) -> Unit
) :
    RecyclerView.Adapter<SelectActivityAdapter.ItemViewHolder>(), Filterable {

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.textview_name)
        val area: TextView = view.findViewById(R.id.textview_area)
    }

    // Contains only the items that match the search query.
    // The elements of the original dataset are copied.
    private val filteredDataset: ArrayList<Activity> =
        arrayListOf<Activity>().apply { addAll(dataset) }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_select_activity, parent, false)

        return ItemViewHolder(adapterLayout)
    }

    override fun getItemCount(): Int {
        return filteredDataset.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = filteredDataset[position]

        holder.name.text = item.name
        if (item.area != null) {
            holder.area.text = item.area!!.name
        } else {
            holder.area.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            onItemClick(item)
        }
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
                    @Suppress("UNCHECKED_CAST") filteredDataset.addAll(results.values as ArrayList<Activity>)

                    // Tracking what changed in the dataset is too expensive,
                    // so we just notify the adapter that the whole dataset changed.
                    @Suppress("NotifyDataSetChanged") notifyDataSetChanged()
                }
            }
        }
    }
}