package com.example.organizer.database.enums

import java.util.*

enum class WeekDays(val typeCode: Int, val label: String, val calendarConst: Int) {
    SUNDAY(1, "Sunday", Calendar.SUNDAY),
    MONDAY(2, "Monday", Calendar.MONDAY),
    TUESDAY(3, "TuesDay", Calendar.TUESDAY),
    WEDNESDAY(4, "Wednesday", Calendar.WEDNESDAY),
    THURSDAY(5, "Thursday", Calendar.THURSDAY),
    FRIDAY(6, "Friday", Calendar.FRIDAY),
    SATURDAY(7, "Saturday", Calendar.SATURDAY);
    companion object {
        fun from(search: Int): WeekDays =  requireNotNull(values().find { it.typeCode == search }) { "No TaskAction with value $search" }
        fun fromLabel(search: String): WeekDays =  requireNotNull(values().find { it.label == search }) { "No TaskAction with value $search" }
        fun getAllDays(): List<String> =  values().map { it.label }
    }
}