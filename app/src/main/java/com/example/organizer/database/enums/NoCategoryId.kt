package com.example.organizer.database.enums

import com.example.organizer.R
import com.example.organizer.database.entity.Category
import com.example.organizer.ui.money.viewTransaction.ViewTransactionGraphViewModel

enum class NoCategoryId(val typeCode: Int, val color:Int, val label: String) {
    EXPENSE(-1, R.color.ExpenseColor, "Expense: No Category"),
    TRANSFER(0, R.color.TransferColor, "Transfer: No Category"),
    INCOME(1, R.color.IncomeColor, "Income: No Category"),
    INITIAL(2, R.color.GrayColor, "Initial: No Category");
    companion object {
        fun from(search: Int): NoCategoryId =  requireNotNull(values().find { it.typeCode == search }) { "No TaskAction with value $search" }
        fun fromLabel(search: String?): NoCategoryId =  requireNotNull(values().find { it.label == search }) { "No TaskAction with value $search" }
    }
    fun toCategory(): Category {
        return Category(label, ViewTransactionGraphViewModel.NO_CATEGORY, typeCode, 0, 0)
    }
}