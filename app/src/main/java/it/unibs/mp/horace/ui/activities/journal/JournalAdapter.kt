package it.unibs.mp.horace.ui.activities.journal

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import it.unibs.mp.horace.R
import it.unibs.mp.horace.backend.journal.JournalDay

/**
 * RecyclerView adapter for the journals list.
 */
open class JournalAdapter(val dataset: List<JournalDay>) :
    RecyclerView.Adapter<JournalAdapter.DataViewHolder>() {

    inner class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val date: TextView = itemView.findViewById(R.id.date)
        val summary: TextView = itemView.findViewById(R.id.summary)
        val entries: RecyclerView = itemView.findViewById(R.id.journalsView)

        init {
            entries.isVisible = false
            itemView.setOnClickListener {
                entries.isVisible = !entries.isVisible
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): JournalAdapter.DataViewHolder {
        val adapterLayout =
            LayoutInflater.from(parent.context).inflate(R.layout.journal_day_item, parent, false)

        return DataViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        val item = dataset[position]

        holder.date.text = item.getDayString()
        holder.summary.text = holder.itemView.context.getString(
            R.string.journal_item_summary,
            item.totalTimeString(),
            item.totalPoints
        )

        holder.entries.adapter = EntryAdapter(item.entries)
    }

    override fun getItemCount(): Int = dataset.size
}
