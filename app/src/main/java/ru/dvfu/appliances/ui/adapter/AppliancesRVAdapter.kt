package ru.dvfu.appliances.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.dvfu.appliances.databinding.ItemApplianceBinding
import ru.dvfu.appliances.model.repository.entity.Appliance

class AppliancesRVAdapter() : RecyclerView.Adapter<AppliancesRVAdapter.RecyclerItemViewHolder>() {

    private var data: List<Appliance> = arrayListOf()

    fun setData(data: List<Appliance>) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        RecyclerItemViewHolder(
            ItemApplianceBinding.inflate(LayoutInflater.from(parent.context),
            parent, false))

    override fun onBindViewHolder(holder: RecyclerItemViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int = data.count()

    inner class RecyclerItemViewHolder(private val vb: ItemApplianceBinding) : RecyclerView.ViewHolder(vb.root) {
        fun bind(data: Appliance) {
            vb.tvName.text = data.name
//            with(vb) {
//                Glide.with(itemView).load(data.avatar).circleCrop().into(ivAvatar) }
        }
    }
}