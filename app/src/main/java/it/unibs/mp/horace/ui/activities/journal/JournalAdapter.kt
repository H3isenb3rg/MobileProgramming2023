package it.unibs.mp.horace.ui.activities.journal

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import it.unibs.mp.horace.R
import it.unibs.mp.horace.models.JournalDay
import it.unibs.mp.horace.ui.leaderboard.friends.FriendsAdapter

/**
 * RecyclerView adapter for the journals list.
 */
open class JournalAdapter() :
    RecyclerView.Adapter<JournalAdapter.DataViewHolder>() {

    var daysList: ArrayList<JournalDay> = ArrayList()

    inner class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val date_tv: TextView = itemView.findViewById(R.id.date)
        val summary_tv: TextView = itemView.findViewById(R.id.summary)
        val entries_rw: RecyclerView = itemView.findViewById(R.id.journalsView)

        init {
            entries_rw.visibility = View.GONE
            itemView.setOnClickListener {
                if (entries_rw.visibility == View.GONE) {
                    entries_rw.visibility = View.VISIBLE
                } else {
                    entries_rw.visibility = View.GONE
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JournalAdapter.DataViewHolder {
        val adapterLayout =
            LayoutInflater.from(parent.context).inflate(R.layout.journal_day_item, parent, false)

        return DataViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        val result = daysList[position]
        holder.date_tv.text = result.getDayString()
        holder.summary_tv.text = holder.itemView.context.getString(R.string.journal_item_summary, result.totalTime, result.totalPoints)
        val childMembersAdapter = EntryAdapter(result.entries)
        holder.entries_rw.adapter = childMembersAdapter
    }

    override fun getItemCount(): Int = daysList.size


    fun addData(list: List<JournalDay>) {
        // TODO: Sort by most recent
        daysList.addAll(list.sortedByDescending { it.day })
        notifyItemRangeInserted(0, list.size)
    }

}
