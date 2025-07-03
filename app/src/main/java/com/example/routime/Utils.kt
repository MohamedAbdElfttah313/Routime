package com.example.routime

import java.text.ChoiceFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Utils {

    companion object{
        fun formatTimeForDataBase(date: Date, format: String = "yyyy-MM-dd HH:mm:ss") : String{
            return SimpleDateFormat(format, Locale.getDefault())
                .format(date)
        }

        fun parseTimeReturnedFromDataBase(date: String, format: String = "yyyy-MM-dd HH:mm:ss") : Date{
            return SimpleDateFormat(format, Locale.getDefault()).parse(date)?: Date()
        }
    }
}