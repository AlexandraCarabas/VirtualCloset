package com.example.virtualcloset.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.virtualcloset.R
import com.example.virtualcloset.models.Item
import com.example.virtualcloset.models.News
import com.squareup.picasso.Picasso

class RecyclerViewAdapterNews (private val listener: OnNewsClick) : RecyclerView.Adapter<RecyclerViewAdapterNews.ViewHolder> () {

    private val newsList = ArrayList<News>()
    //private lateinit var mListener: onItemClickListener

//    interface onItemClickListener{
//        fun onItemClick(position: Int)
//    }
//
//    fun setOnItemClickListener(listener: onItemClickListener){
//        mListener = listener
//    }

    inner class ViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView){
        var imageView : ImageView
        var titleView : TextView

        init {
            imageView = itemView.findViewById(R.id.ivNewsImage)
            titleView = itemView.findViewById(R.id.tvNewsTitle)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.recyclerview_news,parent,false)
        val viewHolder = ViewHolder(itemView)
        itemView.setOnClickListener{
            listener.onClicked(newsList[viewHolder.adapterPosition])
        }
        return viewHolder
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val newsItem: News = newsList[position]
        holder.titleView.text = newsItem.title
        //holder.itemImage.setImageURI(item.image.toUri())
        if(newsItem.imageUrl.isNotEmpty())
            Picasso.get().load(newsItem.imageUrl).into(holder.imageView)
    }

    override fun getItemCount(): Int {
        return newsList.size
    }

    fun updateData(newData: ArrayList<News>){
        newsList.clear()
        newsList.addAll(newData)

        notifyDataSetChanged()
    }
    interface OnNewsClick{
        fun onClicked(news: News)
    }
}