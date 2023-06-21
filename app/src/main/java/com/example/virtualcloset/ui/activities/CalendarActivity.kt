package com.example.virtualcloset.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.example.virtualcloset.R
import com.example.virtualcloset.ui.CalendarAdapter
import com.example.virtualcloset.utils.CalendarUtils
import java.time.LocalDate
import java.time.LocalDate.from
import java.time.YearMonth
import java.time.format.DateTimeFormatter

//import com.example.virtualcloset.utils.CalendarUtils.daysInMonthArray
//import com.example.virtualcloset.utils.CalendarUtils.monthYearFromDate

class CalendarActivity : AppCompatActivity() {

    private lateinit var monthYearText: TextView
    private lateinit var calendarRecyclerView : RecyclerView
    //private lateinit var calendarAdapter : CalendarAdapter
    //private lateinit var daysInMonth: ArrayList<LocalDate?>
    private lateinit var selectedDate: LocalDate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)

        initWidgets()
        selectedDate = LocalDate.now()
        setMonthView()


    }

    private fun initWidgets() {
        calendarRecyclerView = findViewById(R.id.rvCalendarRecyclerView)
        monthYearText = findViewById(R.id.tvMonthYear)
    }

    private fun setMonthView() {
        monthYearText.text = monthYearFromDate(selectedDate)
        val daysInMonth: ArrayList<LocalDate?> = daysInMonthArray(selectedDate)!!
        val calendarAdapter = CalendarAdapter(daysInMonth, selectedDate)
        val layoutManager: LayoutManager = GridLayoutManager(applicationContext, 7)
        calendarRecyclerView.layoutManager = layoutManager
        calendarRecyclerView.adapter = calendarAdapter

        calendarAdapter.setOnItemClickListener(object: CalendarAdapter.onItemClickListener{
            override fun onItemClick(position: Int, date: LocalDate) {
                if (date != null) {
                    selectedDate = date
                    setMonthView()
                }
            }

        })

    }

    fun daysInMonthArray(date: LocalDate?): ArrayList<LocalDate?>? {
        val daysInMonthArray = ArrayList<LocalDate?>()
        val yearMonth = YearMonth.from(date)
        val daysInMonth = yearMonth.lengthOfMonth()
        val firstOfMonth: LocalDate = selectedDate.withDayOfMonth(1)
        val dayOfWeek = firstOfMonth.dayOfWeek.value
        for (i in 1..42) {
            if (i <= dayOfWeek || i > daysInMonth + dayOfWeek) daysInMonthArray.add(null) else daysInMonthArray.add(
                LocalDate.of(selectedDate.year, selectedDate.month, i - dayOfWeek)
            )
        }
        return daysInMonthArray
    }

    fun monthYearFromDate(date: LocalDate): String? {
        val formatter = DateTimeFormatter.ofPattern("MMMM yyyy")
        return date.format(formatter)
    }

    fun previousMonthAction(view: View) {
        selectedDate = selectedDate.minusMonths(1)
        setMonthView()
    }

    fun nextMonthAction(view: View) {
        selectedDate = selectedDate.plusMonths(1)
        setMonthView()
    }

    fun weeklyAction (view: View){
        startActivity(Intent(this, WeekViewActivity::class.java ))
    }


}