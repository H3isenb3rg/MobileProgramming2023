package it.unibs.mp.horace.ui.activities.journal

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import it.unibs.mp.horace.R
import it.unibs.mp.horace.models.TimeEntry
import java.time.Duration

class EntryAdapter(memberData: List<TimeEntry>) :
    RecyclerView.Adapter<EntryAdapter.DataViewHolder>() {

    private var membersList: List<TimeEntry> = memberData

    var onItemClick: ((String) -> Unit)? = null

    inner class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(membersList[adapterPosition].description)
            }
        }

        fun bind(result: TimeEntry) {
            itemView.findViewById<TextView>(R.id.activity_name).text = result.activity?.name
            itemView.findViewById<TextView>(R.id.points).text = itemView.context.getString(R.string.entry_points, result.points)
            itemView.findViewById<TextView>(R.id.activity_details).text = itemView.context.getString(R.string.activity_details, result.timeDiffFloat(), result.startTimeString(), result.endTimeString())
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = DataViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.journal_entry_item, parent,
            false
        )
    )

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        holder.bind(membersList[position])
    }

    override fun getItemCount(): Int = membersList.size


}