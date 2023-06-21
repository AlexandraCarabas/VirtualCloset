package com.example.virtualcloset.ui

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.virtualcloset.R
import com.example.virtualcloset.models.News
import java.time.LocalDate

class CalendarAdapter (private val days: ArrayList<LocalDate?>, selectedDate: LocalDate) : RecyclerView.Adapter<CalendarAdapter.ViewHolder>() {
    val selectedDate = selectedDate

    private lateinit var mListener: onItemClickListener

    interface onItemClickListener{
        fun onItemClick(position: Int,dayOfMonth : LocalDate)
    }

    fun setOnItemClickListener(listener: onItemClickListener){
        mListener = listener
    }

    inner class ViewHolder (itemView: View, listener: onItemClickListener) : RecyclerView.ViewHolder(itemView){
        var textView: TextView
        var parentView: View

        init {
            textView = itemView.findViewById<TextView>(R.id.tvCellDay)
            parentView = itemView.findViewById(R.id.parentView)


            itemView.setOnClickListener{

                listener.onItemClick(adapterPosition, days[adapterPosition]!!)

            }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarAdapter.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.calendar_cell,parent,false)
        var layoutParams = itemView.layoutParams
        if(days.size > 15)
            layoutParams.height = (parent.height * 0.1666666).toInt()
        else
            layoutParams.height = parent.height

        return ViewHolder(itemView, mListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        //selectedDate = LocalDate.now()
        val date: LocalDate? = days[position]
        if(date == null)
            holder.textView.text = ""
        else{
            holder.textView.text = date.dayOfMonth.toString()
            if (date.equals(selectedDate))
                holder.parentView.setBackgroundColor(Color.LTGRAY)
        }
    }

    override fun getItemCount(): Int {
        return days.size
    }

}