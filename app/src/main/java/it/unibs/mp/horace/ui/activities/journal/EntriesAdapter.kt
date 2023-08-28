package it.unibs.mp.horace.ui.activities.journal

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import it.unibs.mp.horace.R
import it.unibs.mp.horace.backend.DateTimeFormatter
import it.unibs.mp.horace.backend.firebase.models.TimeEntry

class EntriesAdapter(
    val context: Context,
    val dataset: List<TimeEntry>,
    private val showEntryOptions: (TimeEntry) -> Unit
) :
    RecyclerView.Adapter<EntriesAdapter.DataViewHolder>() {

    private val dateTimeFormatter = DateTimeFormatter(context)

    inner class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val activityName: TextView = itemView.findViewById(R.id.textview_name)
        val points: TextView = itemView.findViewById(R.id.textview_points)
        val duration: TextView = itemView.findViewById(R.id.textview_duration)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = DataViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.item_journal_entry, parent,
            false
        )
    )

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        val item = dataset[position]

        holder.activityName.text = item.activity?.name
        holder.points.text = context.getString(R.string.entry_points, item.points)
        holder.duration.text = context.getString(
            R.string.activity_duration,
            dateTimeFormatter.formatDuration(item.duration()),
            dateTimeFormatter.formatTime(item.startTime.toLocalTime()),
            dateTimeFormatter.formatTime(item.endTime.toLocalTime())
        )

        holder.itemView.setOnLongClickListener {
            showEntryOptions(item)
            true
        }
    }

    override fun getItemCount(): Int = dataset.size
}