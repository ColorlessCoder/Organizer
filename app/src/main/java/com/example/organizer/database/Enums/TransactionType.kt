package com.example.organizer.database.Enums

enum class TransactionType(val typeCode: Int) {
    EXPENSE(-1),
    TRANSFER(0),
    INCOME(1);
    companion object {
        fun from(search: Int): TransactionType =  requireNotNull(values().find { it.typeCode == search }) { "No TaskAction with value $search" }
    }
}