package com.example.organizer.database.enums

import com.example.organizer.R

enum class DebtType(val typeCode: Int, val color:Int, val relatedTransactionType: TransactionType, val relatedPaymentTransactionType: TransactionType) {
    BORROWED(1, R.color.ExpenseColor, TransactionType.INCOME, TransactionType.EXPENSE),
    LENT(2, R.color.IncomeColor, TransactionType.EXPENSE, TransactionType.INCOME ),
    INSTALLMENT(3, R.color.TransferColor, TransactionType.INITIAL, TransactionType.EXPENSE);
    companion object {
        fun from(search: Int): DebtType =  requireNotNull(values().find { it.typeCode == search }) { "No Debt Type with value $search" }
    }
}