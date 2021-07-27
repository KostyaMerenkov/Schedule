package ru.students.dvfu.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.students.dvfu.databinding.ItemUserBinding
import ru.students.dvfu.model.userdata.User
import ru.students.dvfu.model.userdata.entities.Role

class UsersRVAdapter() : RecyclerView.Adapter<UsersRVAdapter.RecyclerItemViewHolder>() {

    private var data: List<User> = arrayListOf()

    fun setData(data: List<User>) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        RecyclerItemViewHolder(ItemUserBinding.inflate(LayoutInflater.from(parent.context),
            parent, false))

    override fun onBindViewHolder(holder: RecyclerItemViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int = data.count()

    inner class RecyclerItemViewHolder(private val vb: ItemUserBinding) : RecyclerView.ViewHolder(vb.root) {
        fun bind(data: User) {
            vb.tvName.text = data.name
            vb.tvEmail.text = data.email
            vb.tvRole.text = Role.values()[data.role].name
            with(vb) {
                Glide.with(itemView).load(data.avatar).circleCrop().into(ivAvatar) }
        }
    }
}