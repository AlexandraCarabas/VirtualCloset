package com.example.virtualcloset.ui.activities

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.example.virtualcloset.R
import com.example.virtualcloset.ui.CalendarAdapter
import com.example.virtualcloset.utils.CalendarUtils
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class WeekViewActivity : AppCompatActivity() {

    private lateinit var  monthYearText: TextView
    private lateinit var calendarRecyclerView: RecyclerView
    private lateinit var selectedDate: LocalDate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_week_view)

        selectedDate = LocalDate.now()
        initWidgets()
        setWeekView()
    }

    private fun initWidgets() {
        calendarRecyclerView = findViewById(R.id.rvCalendarRecyclerView)
        monthYearText = findViewById(R.id.tvMonthYear)
    }

    private fun setWeekView() {
        monthYearText.setText(monthYearFromDate(selectedDate))
        val days: ArrayList<LocalDate?> = daysInWeekArray(selectedDate)!!
        val calendarAdapter = CalendarAdapter(days, selectedDate)
        val layoutManager: LayoutManager = GridLayoutManager(applicationContext, 7)
        calendarRecyclerView.layoutManager = layoutManager
        calendarRecyclerView.adapter = calendarAdapter

        calendarAdapter.setOnItemClickListener(object: CalendarAdapter.onItemClickListener{
            override fun onItemClick(position: Int, date: LocalDate) {
                if (date != null) {
                    selectedDate = date
                    setWeekView()
                }
            }

        })
        //setEventAdpater()
    }

    fun daysInWeekArray(selectedDate: LocalDate?): ArrayList<LocalDate?>? {
        val days = ArrayList<LocalDate?>()
        var current = sundayForDate(selectedDate!!)
        val endDate = current!!.plusWeeks(1)
        while (current!!.isBefore(endDate)) {
            days.add(current)
            current = current.plusDays(1)
        }
        return days
    }

    private fun sundayForDate(current: LocalDate): LocalDate? {
        var current = current
        val oneWeekAgo = current.minusWeeks(1)
        while (current.isAfter(oneWeekAgo)) {
            if (current.dayOfWeek == DayOfWeek.SUNDAY) return current
            current = current.minusDays(1)
        }
        return null
    }

    fun monthYearFromDate(date: LocalDate): String? {
        val formatter = DateTimeFormatter.ofPattern("MMMM yyyy")
        return date.format(formatter)
    }

    fun previousWeekAction(view: View) {
        selectedDate = selectedDate.minusMonths(1)
        setWeekView()
    }

    fun nextWeekAction(view: View) {
        selectedDate = selectedDate.plusMonths(1)
        setWeekView()
    }

}