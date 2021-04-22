package ru.students.dvfu.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.students.dvfu.mvp.presenter.list.IUserListPresenter
import ru.students.dvfu.mvp.view.list.UserItemView
import ru.students.dvfu.databinding.ItemUserBinding

class UsersRVAdapter(val presenter: IUserListPresenter,
                     //val imageLoader: IImageLoader<ImageView>
                     ) : RecyclerView.Adapter<UsersRVAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)).apply {
            itemView.setOnClickListener { presenter.itemClickListener?.invoke(this) }
        }

    override fun getItemCount() = presenter.getCount()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = presenter.bindView(holder.apply { pos = position })

    inner class ViewHolder(private val vb: ItemUserBinding) : RecyclerView.ViewHolder(vb.root),
        UserItemView {
        override var pos = -1

//        override fun setLogin(text: String) = with(vb) {
//            tvLogin.text = text
//        }

        override fun setName(name: String) {
            vb.tvName.text = name
        }

        override fun setEmail(email: String) {
            vb.tvEmail.text = email
        }

        override fun setRole(role: String) {
            vb.tvRole.text = role
        }

        override fun loadAvatar(url: String): Unit = with(vb) {
            Glide.with(itemView).load(url).circleCrop().into(ivAvatar)
        }
    }
}