package it.unibs.mp.horace.ui.manuallog

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView
import com.google.android.material.textview.MaterialTextView
import it.unibs.mp.horace.R
import it.unibs.mp.horace.models.Activity

/**
 * Adapter for the activities dropdown.
 */
class ActivitiesAdapter(
    context: Context,
    private val dataset: List<Activity>,
) : ArrayAdapter<Activity>(context, R.layout.select_activity_item, dataset) {

    // Contains only the items that match the search query.
    // The elements of the original dataset are copied.
    private val filteredDataset: ArrayList<Activity> =
        arrayListOf<Activity>().apply { addAll(dataset) }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createViewFromResource(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createViewFromResource(position, convertView, parent)
    }

    override fun getFilter(): Filter {  // FIXME: Scrivendo sbomba perchè sembra
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
                    @Suppress("UNCHECKED_CAST") filteredDataset.addAll(results.values as ArrayList<Activity>)

                    if (results.count > 0) {
                        // Tracking what changed in the dataset is too expensive,
                        // so we just notify the adapter that the whole dataset changed.
                        notifyDataSetChanged()
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
            .inflate(R.layout.select_activity_item, parent, false)

        // Get the views
        val name: TextView = layout.findViewById(R.id.name)
        val area: MaterialTextView = layout.findViewById(R.id.area)

        val item = filteredDataset[position]

        name.text = item.name

        // Hide the area chip if the activity has no area,
        // otherwise set the area name
        if (item.area == null) {
            area.visibility = View.GONE
        } else {
            area.text = item.area?.name
            area.visibility = View.VISIBLE
        }

        return layout
    }
}
