package com.example.organizer.database.enums

import com.example.organizer.R

enum class TransactionType(val typeCode: Int, val color:Int, val label: String) {
    EXPENSE(-1, R.color.ExpenseColor, "Expense"),
    TRANSFER(0, R.color.TransferColor, "Transfer"),
    INCOME(1, R.color.IncomeColor, "Income"),
    INITIAL(2, R.color.GrayColor, "Initial");
    companion object {
        fun from(search: Int): TransactionType =  requireNotNull(values().find { it.typeCode == search }) { "No TaskAction with value $search" }
    }
}