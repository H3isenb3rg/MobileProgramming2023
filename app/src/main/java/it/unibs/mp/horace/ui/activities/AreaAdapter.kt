package it.unibs.mp.horace.ui.activities

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView
import it.unibs.mp.horace.R
import it.unibs.mp.horace.backend.firebase.models.Area

class AreaAdapter(
    context: Context,
    private val dataset: List<Area>,
) : ArrayAdapter<Area>(context, R.layout.area_item, dataset) {

    // Contains only the items that match the search query.
    // The elements of the original dataset are copied.
    private val filteredDataset: ArrayList<Area> =
        arrayListOf<Area>().apply { addAll(dataset) }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createViewFromResource(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createViewFromResource(position, convertView, parent)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResults = when {
                    // If the search query is empty, return the original dataset.
                    constraint.isNullOrEmpty() -> dataset

                    // Otherwise, check if there are items that match the search query.
                    // If there are no matches, search the database for users with
                    // the given email or username.
                    else -> dataset.filter { it.fitsSearch(constraint.toString()) }
                }
                return FilterResults().apply {
                    values = filterResults
                    count = filterResults.size
                }
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                if (results?.values is ArrayList<*>) {
                    filteredDataset.clear()

                    // Here we are sure that the values are of type ArrayList<Activity>.
                    // So the cast is safe.
                    @Suppress("UNCHECKED_CAST") filteredDataset.addAll(results.values as ArrayList<Area>)

                    if (results.count > 0) {
                        // Tracking what changed in the dataset is too expensive,
                        // so we just notify the adapter that the whole dataset changed.
                        @Suppress("NotifyDataSetChanged") notifyDataSetChanged()
                    } else {
                        notifyDataSetInvalidated()
                    }
                }
            }
        }
    }

    private fun createViewFromResource(
        position: Int, convertView: View?, parent: ViewGroup?
    ): View {
        // If there is no view to reuse, inflate a new one
        val layout = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.area_item, parent, false)

        // Get the views
        val area: TextView = layout.findViewById(R.id.area)

        val item = filteredDataset[position]

        area.text = item.name

        return layout
    }
}