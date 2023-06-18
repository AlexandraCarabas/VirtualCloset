package com.codingstuff.imageslider

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.virtualcloset.R
import com.example.virtualcloset.models.Item
import com.squareup.picasso.Picasso

class ImageAdapter(private val itemList: ArrayList<Item>, private val viewPager2: ViewPager2) :
    RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.image_container, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val item: Item = itemList[position]
        if(item.image.isNotEmpty())
            Picasso.get().load(item.image).into(holder.imageView)
        else
            holder.imageView.setImageURI(item.image.toUri())
//        if (position == itemList.size-1){
//            viewPager2.post(runnable)
//        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    private val runnable = Runnable {
        itemList.addAll(itemList)
        notifyDataSetChanged()
    }
}