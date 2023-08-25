package it.unibs.mp.horace.ui.activities.journal

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import it.unibs.mp.horace.R
import it.unibs.mp.horace.backend.firebase.models.TimeEntry
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class EntryAdapter(
    val context: Context,
    val dataset: List<TimeEntry>,
    private val showEntryOptions: (TimeEntry) -> Unit
) :
    RecyclerView.Adapter<EntryAdapter.DataViewHolder>() {

    class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
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
            formatDuration(item.duration() / 3600, (item.duration() % 3600) / 60),
            formatTime(item.startTime.toLocalTime()),
            formatTime(item.endTime.toLocalTime())
        )

        holder.itemView.setOnLongClickListener {
            showEntryOptions(item)
            true
        }
    }

    override fun getItemCount(): Int = dataset.size

    private fun formatTime(time: LocalTime) = time.format(DateTimeFormatter.ofPattern("HH:mm"))

    private fun formatDuration(hours: Int, minutes: Int): String {
        val minutesString = context.resources.getQuantityString(R.plurals.minutes, minutes, minutes)
        val hoursString = context.resources.getQuantityString(R.plurals.hours, hours, hours)

        return if (hours == 0) {
            minutesString
        } else if (minutes == 0) {
            hoursString
        } else {
            "$hoursString $minutesString"
        }
    }
}