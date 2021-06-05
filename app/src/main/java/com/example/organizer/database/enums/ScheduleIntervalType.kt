package com.example.organizer.database.enums

enum class ScheduleIntervalType(val typeCode: Int, val label: String) {
    NONE(0, "None"),
    DAILY(1, "Daily"),
    WEEKLY(2, "Weekly"),
    MONTHLY(3, "Monthly");
    companion object {
        fun from(search: Int): ScheduleIntervalType =  requireNotNull(values().find { it.typeCode == search }) { "No TaskAction with value $search" }
    }
}