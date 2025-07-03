package com.example.routime.Presentation.UiAdapters

import android.content.res.ColorStateList
import java.util.Calendar
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.routime.R
import com.example.routime.databinding.SingleWeekDayLayoutBinding

class SevenDaysRecyclerViewAdapter(val calendar: Calendar,val getDateDeeds : (Long)->(Unit)) : RecyclerView.Adapter<SevenDaysRecyclerViewAdapter.SingleDayViewHolder>() {
    private val lettersOfWeekDays = arrayOf("s","m","t","w","t","f","s")
    private var selectedCardView : CardView? = null

    init {
        calendar.add(Calendar.DAY_OF_MONTH,-6)
    }

    class SingleDayViewHolder(val xmlView : SingleWeekDayLayoutBinding) : ViewHolder(xmlView.root) {
        val letterOfDayTextView = xmlView.DayNameOfWeekTextView
        val dateOfSingleDayTextView = xmlView.DateOfSingleDayTextView
        val dateOfSingleDateCardView = xmlView.DateOfSingleDayCardView
        var day = 0
        var month = 0
        var year = 0

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SingleDayViewHolder {
        val binding = SingleWeekDayLayoutBinding
            .inflate(LayoutInflater.from(parent.context),parent,false)
        return SingleDayViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SingleDayViewHolder, position: Int) {

        holder.apply {
            day = calendar.get(Calendar.DAY_OF_MONTH)
            month = calendar.get(Calendar.MONTH)
            year = calendar.get(Calendar.YEAR)
        }

        holder.letterOfDayTextView.text = lettersOfWeekDays.get(calendar.get(Calendar.DAY_OF_WEEK)-1)
        holder.dateOfSingleDayTextView.text = calendar.get(Calendar.DAY_OF_MONTH).toString()
        calendar.add(Calendar.DAY_OF_MONTH,1)

        if (position==6){
            holder.dateOfSingleDateCardView.setCardBackgroundColor("#4C9292".toColorInt())
            selectedCardView = holder.dateOfSingleDateCardView
        }

        holder.dateOfSingleDateCardView.setOnClickListener {
            selectedCardView!!.setCardBackgroundColor("#00000000".toColorInt())
            selectedCardView = it as CardView
            it.setCardBackgroundColor("#4C9292".toColorInt())

            val dateOfDay = calendar.run {
                set(Calendar.DAY_OF_MONTH,holder.day)
                set(Calendar.MONTH,holder.month)
                set(Calendar.YEAR,holder.year)
                timeInMillis
            }
            getDateDeeds(dateOfDay)
            println("SevenDaysRecyclerViewAdapter.Calendar.DayOfMonth : ${holder.day}")
        }

    }

    override fun getItemCount() = 7
}