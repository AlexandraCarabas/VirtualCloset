package com.example.virtualcloset.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.virtualcloset.R
import com.example.virtualcloset.models.Item
import com.squareup.picasso.Picasso

class OutfitRVAdapter (private val itemList: ArrayList<Item>): RecyclerView.Adapter<OutfitRVAdapter.ViewHolder>() {


    inner class ViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView){
        var itemImage : ImageView
        var itemName : TextView

        init {
            itemImage = itemView.findViewById(R.id.ivItemImage)
            itemName = itemView.findViewById(R.id.tvItemName)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.recyclerview_model,parent,false)
        return ViewHolder(itemView)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item: Item = itemList[position]
        holder.itemName.text = item.name
        //holder.itemImage.setImageURI(item.image.toUri())
        if(item.image.isNotEmpty())
            Picasso.get().load(item.image).into(holder.itemImage)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}