package it.unibs.mp.horace.ui.activities.journal

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import it.unibs.mp.horace.R
import it.unibs.mp.horace.backend.firebase.models.TimeEntry
import java.time.LocalDateTime

class EntryAdapter(memberData: List<TimeEntry>) :
    RecyclerView.Adapter<EntryAdapter.DataViewHolder>() {

    class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val activityName: TextView = itemView.findViewById(R.id.activity_name)
        val points: TextView = itemView.findViewById(R.id.points)
        val activityDetails: TextView = itemView.findViewById(R.id.activity_details)
    }

    private var membersList: List<TimeEntry> = memberData

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = DataViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.journal_entry_item, parent,
            false
        )
    )

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        val item = membersList[position]

        holder.activityName.text = item.activity?.name
        holder.points.text = holder.itemView.context.getString(R.string.entry_points, item.points)
        holder.activityDetails.text = holder.itemView.context.getString(
            R.string.activity_details,
            item.durationInHours(),
            formatTime(item.startTime),
            formatTime(item.endTime)
        )
    }

    override fun getItemCount(): Int = membersList.size

    private fun formatTime(time: LocalDateTime): String {
        val minutes = time.minute.toString()
        val hours = time.hour.toString()
        return hours + ":" + if (minutes.length < 2) "0${minutes}" else minutes
    }
}