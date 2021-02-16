package com.example.organizer.ui.money.addTransaction

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.organizer.R
import com.example.organizer.database.Enums.TransactionType
import com.example.organizer.database.entity.Account

class AddTransactionViewModel : ViewModel() {
    var transactionType = MutableLiveData<Int>()
    val amount = MutableLiveData<String>()
    val details = MutableLiveData<String>()
    val fromAccount = MutableLiveData<Account>()
    val toAccount = MutableLiveData<Account>()
    var backgroundColor = MutableLiveData<Int>()
    val showFromAccount = MutableLiveData<Boolean>()
    val showToAccount = MutableLiveData<Boolean>()
    var initializedNotAllowed = false
    var fieldPendingToSetAfterNavigateBack: FIELDS = FIELDS.NONE

    companion object {
        enum class FIELDS {
            FROM_ACCOUNT, TO_ACCOUNT, NONE
        }
    }

    init {
        selectTransactionType(0)
    }

    fun selectTransferType() {
        selectTransactionType(TransactionType.TRANSFER.typeCode)
    }
    fun selectIncomeType() {
        selectTransactionType(TransactionType.INCOME.typeCode)
    }
    fun selectExpenseType() {
        selectTransactionType(TransactionType.EXPENSE.typeCode)
    }

    fun selectFromAccount() {
        println("From account")
    }
    fun selectToAccount() {
        println("To account")
    }

    fun selectTransactionType(type: Int) {
        println(type)
        transactionType.value = type
        if(TransactionType.TRANSFER.typeCode == type) {
            backgroundColor.value = R.color.TransferColor
            showFromAccount.value = true
            showToAccount.value = true
        } else if(TransactionType.INCOME.typeCode == type) {
            backgroundColor.value = R.color.IncomeColor
            showToAccount.value = true
            showFromAccount.value = false
        } else if(TransactionType.EXPENSE.typeCode == type) {
            backgroundColor.value = R.color.ExpenseColor
            showFromAccount.value = true
            showToAccount.value = false
        }
    }
}