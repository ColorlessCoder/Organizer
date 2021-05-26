package com.example.organizer.database.enums

enum class ChartEntityType(val typeCode: Int) {
    CATEGORY(1);
    companion object {
        fun from(search: Int): ChartEntityType =  requireNotNull(values().find { it.typeCode == search }) { "No TaskAction with value $search" }
    }
}