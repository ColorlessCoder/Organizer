package com.example.organizer.ui.money.viewTransaction

import androidx.lifecycle.ViewModel
import com.example.organizer.database.relation.TransactionDetails

class ViewTransactionGraphViewModel : ViewModel() {
    private var transactions = mutableListOf<TransactionDetails>()
    var categoryTrace = mutableListOf<ViewTransactionSummary.CategoryGroupTreeNode>()
    var pieSettings = ChartSettings(true,
        showValue = false,
        navigateDeep = true,
        navigateToList = false
    )
    var barSettings = ChartSettings(true,
        showValue = true,
        navigateDeep = false,
        navigateToList = false
    )
    fun getTransactions(): MutableList<TransactionDetails> {
        return transactions
    }

    fun setTransactions(list: MutableList<TransactionDetails>) {
        transactions = list
        categoryTrace.clear()
    }

    fun getAllTransactionsUnderCategory(
        node: ViewTransactionSummary.CategoryGroupTreeNode,
        transactionList: MutableList<TransactionDetails>
    ) {
        transactionList.addAll(node.value.transactions)
        node.children.entries.forEach { getAllTransactionsUnderCategory(it.value, transactionList) }
    }

    companion object {
        const val NO_CATEGORY = "No Category"
        const val INCOME = "Income"
        const val EXPENSE = "Expense"
        const val TRANSFER = "Transfer"
        data class ChartSettings(var showLabel: Boolean, var showValue: Boolean, var navigateDeep: Boolean, var navigateToList: Boolean)
    }
}