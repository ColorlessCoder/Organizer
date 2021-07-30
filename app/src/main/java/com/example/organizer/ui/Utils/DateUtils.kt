package com.example.organizer.ui.Utils

import java.text.SimpleDateFormat
import java.util.Date

class DateUtils {
    companion object {
        private const val salatDatePattern = "dd-MMM-yyyy"
        fun dateToString(date: Date): String {
            return SimpleDateFormat("dd/MM/yy hh:mm a").format(date)
        }
        fun getDateString(date: Date): String {
            return SimpleDateFormat("dd/MM/yyyy").format(date)
        }
        fun getDateStringWithMonth(date: Date): String {
            return SimpleDateFormat("dd-MMM-yyyy").format(date)
        }
        fun serializeSalatDate(date: Date): String {
            return SimpleDateFormat(salatDatePattern).format(date)
        }
        fun deserializeSalatDateString(dateStr: String): Date {
            return SimpleDateFormat(salatDatePattern).parse(dateStr)
        }
    }
}