package it.unibs.mp.horace.ui.home.selectactivity

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import it.unibs.mp.horace.R
import it.unibs.mp.horace.backend.firebase.models.Activity

class SelectActivityAdapter(private val dataset: List<Activity>) :
    RecyclerView.Adapter<SelectActivityAdapter.ItemViewHolder>() {

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.name)
        val area: TextView = view.findViewById(R.id.area)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_select_activity, parent, false)

        return ItemViewHolder(adapterLayout)
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = dataset[position]

        holder.name.text = item.name
        if (item.area != null) {
            holder.area.text = item.area!!.name
        } else {
            holder.area.visibility = View.GONE
        }
    }
}