package it.unibs.mp.horace.ui.home.workgroup

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import it.unibs.mp.horace.R
import it.unibs.mp.horace.backend.User

class InviteFriendsAdapter(
    private val dataset: List<User>,
    private val onSelect: (User, Boolean) -> Unit
) :
    RecyclerView.Adapter<InviteFriendsAdapter.ItemViewHolder>() {

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val profilePhoto: ImageView = view.findViewById(R.id.profilePhoto)
        val username: TextView = view.findViewById(R.id.username)
        val select: CheckBox = view.findViewById(R.id.select)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout =
            LayoutInflater.from(parent.context).inflate(R.layout.work_group_item, parent, false)

        return ItemViewHolder(adapterLayout)
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = dataset[position]
        holder.profilePhoto.load(item.photoUrl)
        holder.select.setOnCheckedChangeListener { _, isChecked ->
            onSelect(item, isChecked)
        }
    }
}