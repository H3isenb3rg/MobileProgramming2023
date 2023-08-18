package it.unibs.mp.horace.ui.activities.journal

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import it.unibs.mp.horace.R
import it.unibs.mp.horace.backend.journal.JournalDay
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * RecyclerView adapter for the journals list.
 */
open class JournalAdapter(val context: Context, val dataset: List<JournalDay>) :
    RecyclerView.Adapter<JournalAdapter.DataViewHolder>() {

    inner class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val date: TextView = itemView.findViewById(R.id.date)
        val summary: TextView = itemView.findViewById(R.id.summary)
        val entries: RecyclerView = itemView.findViewById(R.id.journalsView)

        private val expand: ImageView = itemView.findViewById(R.id.expand)

        init {
            entries.isVisible = false

            itemView.setOnClickListener {
                entries.isVisible = !entries.isVisible
                expand.setImageResource(
                    if (entries.isVisible) {
                        R.drawable.baseline_expand_less_24
                    } else {
                        R.drawable.baseline_expand_more_24
                    }
                )
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): JournalAdapter.DataViewHolder {
        val adapterLayout =
            LayoutInflater.from(parent.context).inflate(R.layout.journal_day_item, parent, false)

        return DataViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        val item = dataset[position]

        holder.date.text = getDayString(item)
        holder.summary.text = holder.itemView.context.getString(
            R.string.journal_item_summary, getTotalTimeString(item), item.totalPoints
        )

        holder.entries.adapter = EntryAdapter(context, item.timeEntries)
    }

    override fun getItemCount(): Int = dataset.size

    private fun getDayString(journalDay: JournalDay): String {
        return when (journalDay.date) {
            LocalDate.now() -> {
                context.getString(R.string.today)
            }

            LocalDate.now().minusDays(1) -> {
                context.getString(R.string.yesterday)
            }

            else -> journalDay.date.format(DateTimeFormatter.ofPattern("E, d MMM yyyy"))
        }
    }

    private fun getTotalTimeString(journalDay: JournalDay): String {
        val hours = journalDay.totalTime / 3600
        val minutes = (journalDay.totalTime % 3600) / 60

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
