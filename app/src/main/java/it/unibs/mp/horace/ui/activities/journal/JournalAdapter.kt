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
import it.unibs.mp.horace.backend.DateTimeFormatter
import it.unibs.mp.horace.backend.firebase.models.TimeEntry
import it.unibs.mp.horace.backend.journal.JournalDay

/**
 * RecyclerView adapter for the journals list.
 */
open class JournalAdapter(
    val context: Context,
    val dataset: List<JournalDay>,
    private val showEntryOptions: (TimeEntry) -> Unit
) :
    RecyclerView.Adapter<JournalAdapter.DataViewHolder>() {

    private val dateTimeFormatter = DateTimeFormatter(context)

    inner class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val date: TextView = itemView.findViewById(R.id.textview_date)
        val summary: TextView = itemView.findViewById(R.id.textview_summary)
        val entries: RecyclerView = itemView.findViewById(R.id.recyclerview_journal)

        private val expand: ImageView = itemView.findViewById(R.id.imageview_expand)

        init {
            entries.isVisible = false

            itemView.setOnClickListener {
                entries.isVisible = !entries.isVisible
                expand.setImageResource(
                    if (entries.isVisible) {
                        R.drawable.ic_expand_less
                    } else {
                        R.drawable.ic_expand_more
                    }
                )
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): JournalAdapter.DataViewHolder {
        val adapterLayout =
            LayoutInflater.from(parent.context).inflate(R.layout.item_journal_day, parent, false)

        return DataViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        val item = dataset[position]

        holder.date.text = dateTimeFormatter.formatDate(item.date)
        holder.summary.text = holder.itemView.context.getString(
            R.string.journal_item_summary,
            dateTimeFormatter.formatDuration(item.totalTime),
            item.totalPoints
        )

        holder.entries.adapter = EntriesAdapter(context, item.timeEntries, showEntryOptions)
    }

    override fun getItemCount(): Int = dataset.size
}
