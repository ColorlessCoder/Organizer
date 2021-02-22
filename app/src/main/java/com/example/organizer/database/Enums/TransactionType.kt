package com.example.organizer.database.Enums

import com.example.organizer.R

enum class TransactionType(val typeCode: Int, val color:Int) {
    EXPENSE(-1, R.color.ExpenseColor),
    TRANSFER(0, R.color.TransferColor),
    INCOME(1, R.color.IncomeColor);
    companion object {
        fun from(search: Int): TransactionType =  requireNotNull(values().find { it.typeCode == search }) { "No TaskAction with value $search" }
    }
}