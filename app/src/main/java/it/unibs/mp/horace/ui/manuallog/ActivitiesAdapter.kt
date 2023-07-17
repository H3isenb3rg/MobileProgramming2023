package it.unibs.mp.horace.ui.manuallog

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.google.android.material.chip.Chip
import it.unibs.mp.horace.R
import it.unibs.mp.horace.models.Activity

/**
 * Adapter for the activities dropdown.
 */
class ActivitiesAdapter(
    context: Context,
    private val dataset: MutableList<Activity>,
    private val onSelect: (Activity) -> Unit
) : ArrayAdapter<Activity>(context, R.layout.activity_item, dataset) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createViewFromResource(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createViewFromResource(position, convertView, parent)
    }

    private fun createViewFromResource(
        position: Int, convertView: View?, parent: ViewGroup?
    ): View {
        // If there is no view to reuse, inflate a new one
        val layout = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.activity_item, parent, false)

        // Get the views
        val activity: TextView = layout.findViewById(R.id.activity)
        val area: Chip = layout.findViewById(R.id.area)

        val item = dataset[position]

        activity.text = item.name

        // Hide the area chip if the activity has no area,
        // otherwise set the area name
        if (item.area == null) {
            area.visibility = View.GONE
        } else {
            area.text = item.area?.name
        }

        // Set the click listener
        layout.setOnClickListener {
            onSelect(item)
        }

        return layout
    }
}
