package it.unibs.mp.horace.ui.home.workgroup

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import it.unibs.mp.horace.R
import it.unibs.mp.horace.backend.firebase.models.User

class WorkGroupAdapter(
    private val dataset: List<User>,
    private val onRemove: (User) -> Unit
) :
    RecyclerView.Adapter<WorkGroupAdapter.ItemViewHolder>() {

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val profilePhoto: ImageView = view.findViewById(R.id.image_view_photo)
        val username: TextView = view.findViewById(R.id.textview_username)
        val remove: Button = view.findViewById(R.id.button_remove)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout =
            LayoutInflater.from(parent.context).inflate(R.layout.item_work_group, parent, false)

        return ItemViewHolder(adapterLayout)
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = dataset[position]

        holder.profilePhoto.load(item.profilePhoto)
        holder.username.text = item.username

        holder.remove.setOnClickListener {
            onRemove(item)
        }
    }
}