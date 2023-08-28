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
import it.unibs.mp.horace.backend.firebase.models.User

class InviteFriendsAdapter(
    private val dataset: List<User>,
    private val onSelect: (User, Boolean) -> Unit
) :
    RecyclerView.Adapter<InviteFriendsAdapter.ItemViewHolder>() {

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val profilePhoto: ImageView = view.findViewById(R.id.image_view_photo)
        val username: TextView = view.findViewById(R.id.textview_username)
        val email: TextView = view.findViewById(R.id.textview_email)
        val select: CheckBox = view.findViewById(R.id.checkbox_select)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout =
            LayoutInflater.from(parent.context).inflate(R.layout.item_invite_friend, parent, false)

        return ItemViewHolder(adapterLayout)
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = dataset[position]
        holder.profilePhoto.load(item.profilePhoto)
        holder.username.text = item.username
        holder.email.text = item.email
        holder.itemView.setOnClickListener {
            holder.select.isChecked = !holder.select.isChecked
        }
        holder.select.setOnCheckedChangeListener { _, isChecked ->
            onSelect(item, isChecked)
        }
    }
}