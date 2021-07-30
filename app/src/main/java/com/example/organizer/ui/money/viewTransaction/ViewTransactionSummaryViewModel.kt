package com.example.organizer.ui.money.viewTransaction

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.organizer.database.relation.TransactionDetails

class ViewTransactionSummaryViewModel : ViewModel() {
    private var transactions = mutableListOf<TransactionDetails>()
    var accountSummaryList = mutableListOf<AccountSummaryRowData>()
    var categorySummaryList = mutableListOf<ViewTransactionSummary.CategoryGroupTreeNode>()
    var debSummaryList = mutableListOf<DebtSummaryRowData>()
    var focusedRow: View? = null
    val layoutLoaded = MutableLiveData<Int>(0)
    fun getTransactions(): MutableList<TransactionDetails> {
        return transactions
    }

    fun setTransactions(list: MutableList<TransactionDetails>) {
        transactions = list
        accountSummaryList.clear()
        categorySummaryList.clear()
        debSummaryList.clear()
        focusedRow = null
    }

    fun getAllTransactionsUnderCategory(
        node: ViewTransactionSummary.CategoryGroupTreeNode,
        transactionList: MutableList<TransactionDetails>
    ) {
        transactionList.addAll(node.value.transactions)
        if (node.value.name == NO_CATEGORY) {
            return
        }
        node.children.entries.forEach { getAllTransactionsUnderCategory(it.value, transactionList) }
    }

    fun getAllTransactionsForAccount(accountName: String): MutableList<TransactionDetails> {
        return transactions.filter { it.fromAccountName == accountName || it.toAccountName == accountName }.toMutableList()
    }
    fun getAllTransactionsForDebt(debtId: String): MutableList<TransactionDetails> {
        return transactions.filter { it.debt != null && it.debt.id == debtId }.toMutableList()
    }

    companion object {
        const val NO_CATEGORY = "No Category"
        const val NUMBER_OF_LAYOUT = 3
    }
}