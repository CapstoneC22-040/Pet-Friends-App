package com.example.petfriends.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.petfriends.R
import com.example.petfriends.data.local.model.ItemList
import com.example.petfriends.databinding.ItemRowActivityBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.ktx.Firebase

class ListItemAdapter(
    private val itemList: ArrayList<ItemList>,
) : RecyclerView.Adapter<ListItemAdapter.MyViewHolder> () {


    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: ItemList)
    }

    class MyViewHolder(private var binding: ItemRowActivityBinding)
        : RecyclerView.ViewHolder(binding.root) {
        fun bind(itemList: ItemList) {
            binding.apply {
                tvCategoriesInformation.text = itemList.categoryName
                tvFoodName.text = itemList.foodName
                tvFoodWeight.text = itemList.foodWeight
                tvFoodDate.text = itemList.date
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemRowActivityBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(itemList[position])
        holder.itemView.setOnClickListener { onItemClickCallback.onItemClicked(itemList[holder.adapterPosition]) }
    }

    override fun getItemCount(): Int = itemList.size
}