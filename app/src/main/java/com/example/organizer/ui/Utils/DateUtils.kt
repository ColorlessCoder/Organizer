package com.example.organizer.ui.Utils

import java.text.SimpleDateFormat
import java.util.*

class DateUtils {
    companion object {
        private const val salatDatePattern = "dd MMM yyyy"
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
        fun salatDisplayDate(dateStr: String): String {
            return salatDisplayDate(deserializeSalatDateString(dateStr))
        }
        fun salatDisplayDate(date: Date): String {
            return SimpleDateFormat("dd MMMM, yyyy", Locale.ENGLISH).format(date)
        }
        fun salatDisplayTime(date: Date): String {
            return SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(date)
        }
        fun createDateRange(start: Date?, end: Date?): Pair<Date?,Date?> {
            return createDateRange(start, end, reverse = false)
        }
        fun createDateRange(start: Date?, end: Date?, reverse: Boolean): Pair<Date?,Date?> {
            if(start != null && end != null && start.after(end)) {
                val cal = Calendar.getInstance()
                if (reverse) {
                    cal.time = start
                    cal.add(Calendar.DAY_OF_MONTH, -1)
                    return Pair(cal.time, end)
                } else {
                    cal.time = end
                    cal.add(Calendar.DAY_OF_MONTH, 1)
                    return Pair(start, cal.time)
                }
            }
            return Pair(start, end)
        }
        fun dateInRange(date: Date, range: Pair<Date?,Date?>?, includeRightRange: Boolean?): Boolean {
            if(range?.first == null || range.second == null) {
                return false
            }
            return (range.first!!.equals(date) || range.first!!.before(date))
                    && ((includeRightRange == true && range.second!!.equals(date)) || range.second!!.after(date))
        }

        fun getCurrentDate(): Date {
            return Date()
        }
    }
}