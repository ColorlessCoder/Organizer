package com.example.organizer.ui.Utils

import java.text.SimpleDateFormat
import java.util.Date

class DateUtils {
    companion object {
        fun dateToString(date: Date): String {
            return SimpleDateFormat("dd/MM/yy hh:mm a").format(date)
        }
    }
}