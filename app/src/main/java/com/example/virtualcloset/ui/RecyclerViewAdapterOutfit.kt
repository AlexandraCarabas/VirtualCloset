package com.example.virtualcloset.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.virtualcloset.R
import com.example.virtualcloset.models.Item
import com.example.virtualcloset.models.Outfit
import com.squareup.picasso.Picasso

class RecyclerViewAdapterOutfit (private val outfitsList: ArrayList<Outfit>): RecyclerView.Adapter<RecyclerViewAdapterOutfit.ViewHolder>() {

    //private val item :ArrayList<Item>
    private lateinit var mListener: onItemClickListener

    interface onItemClickListener{
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: onItemClickListener){
        mListener = listener
    }

    inner class ViewHolder (outfitsView: View, listener: onItemClickListener) : RecyclerView.ViewHolder(outfitsView){
        var outfitsContent : RecyclerView
        var outfitName : TextView

        init {
            outfitsContent = outfitsView.findViewById(R.id.recyclerViewOutfitItems)
            outfitName = outfitsView.findViewById(R.id.tvIOutfitName)

            outfitName.setOnClickListener{

                listener.onItemClick(adapterPosition)

            }
        }
       // val binding = ParentItemBinding.bind(outfitsView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val outfitView = LayoutInflater.from(parent.context)
            .inflate(R.layout.recyclerview_outfits,parent,false)
        return ViewHolder(outfitView, mListener)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val outfit: Outfit = outfitsList[position]
        holder.outfitName.text = outfit.name
        //holder.itemImage.setImageURI(item.image.toUri())
        val outfitsImages = OutfitRVAdapter(outfit.items)
        holder.outfitsContent.adapter = outfitsImages
    }

    override fun getItemCount(): Int {
        return outfitsList.size
    }
}