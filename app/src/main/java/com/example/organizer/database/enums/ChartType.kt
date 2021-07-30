package com.example.organizer.database.enums

enum class ChartType(val typeCode: Int) {
    LINE_CHART(1);
    companion object {
        fun from(search: Int): ChartType =  requireNotNull(values().find { it.typeCode == search }) { "No TaskAction with value $search" }
    }
}