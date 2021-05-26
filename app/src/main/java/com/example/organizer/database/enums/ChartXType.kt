package com.example.organizer.database.enums

enum class ChartXType(val typeCode: Int) {
    LABEL(1),
    DATE(2);
    companion object {
        fun from(search: Int): ChartXType =  requireNotNull(values().find { it.typeCode == search }) { "No TaskAction with value $search" }
        fun fromName(search: String): ChartXType =  requireNotNull(values().find { it.name == search }) { "No TaskAction with value $search" }
    }
}