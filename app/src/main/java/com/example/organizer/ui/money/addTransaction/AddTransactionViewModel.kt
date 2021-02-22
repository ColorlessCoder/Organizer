package com.example.organizer.ui.money.addTransaction

import android.graphics.Color
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.organizer.R
import com.example.organizer.database.AppDatabase
import com.example.organizer.database.Enums.TransactionType
import com.example.organizer.database.dao.TransactionDAO
import com.example.organizer.database.entity.Account
import com.example.organizer.database.entity.Transaction
import com.example.organizer.database.services.TransactionsService
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.*

class AddTransactionViewModel : ViewModel() {
    var transactionType = MutableLiveData<Int>()
    val amount = MutableLiveData<String>()
    val details = MutableLiveData<String>()
    val fromAccount = MutableLiveData<Account>()
    val toAccount = MutableLiveData<Account>()
    var backgroundColor = MutableLiveData<Int>()
    val showFromAccount = MutableLiveData<Boolean>()
    val showToAccount = MutableLiveData<Boolean>()
    lateinit var transactionService: TransactionsService
    val selectedAccount = MutableLiveData<Account>()
    var initializedNotAllowed = false
    var fieldPendingToSetAfterNavigateBack: FIELDS = FIELDS.NONE
    val navigateBack = MutableLiveData<Boolean>()

    companion object {
        enum class FIELDS {
            FROM_ACCOUNT, TO_ACCOUNT, NONE
        }
    }

    init {
        selectTransactionType(0)
        navigateBack.value = false
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

    fun setNavigateBack() {
        navigateBack.value = true
    }

    fun isTheAmountAllowed(): Boolean {
        val value = if (amount.value != null) amount.value!!.toDouble() else 0.0
        return value.compareTo(0.0) > 0 && (
                fromAccount.value == null
                        || fromAccount.value!!.balance.compareTo(value) >= 0
                )
    }

    fun createTransaction() {
        if (amount.value != null && isTheAmountAllowed()) {
            viewModelScope.launch {
                val transaction = Transaction(
                    UUID.randomUUID().toString(),
                    transactionType.value!!,
                    amount.value!!.toDouble(),
                    if (fromAccount.value == null) null else fromAccount.value!!.id,
                    if (toAccount.value == null) null else toAccount.value!!.id,
                    null,
                    null,
                    details.value,
                    Date().time
                )
                try {
                    transactionService.insert(transaction)
                    setNavigateBack()
                } catch (ex: Exception) {
                    println("Expection occured" + ex.message)
                }
            }
        }
    }

    fun selectTransactionType(type: Int) {
        println(type)
        transactionType.value = type
        if (TransactionType.TRANSFER.typeCode == type) {
            backgroundColor.value = R.color.TransferColor
            showFromAccount.value = true
            showToAccount.value = true
            fromAccount.value = selectedAccount.value
            toAccount.value = null
        } else if (TransactionType.INCOME.typeCode == type) {
            backgroundColor.value = R.color.IncomeColor
            showToAccount.value = true
            showFromAccount.value = false
            fromAccount.value = null
            toAccount.value = selectedAccount.value
        } else if (TransactionType.EXPENSE.typeCode == type) {
            backgroundColor.value = R.color.ExpenseColor
            showFromAccount.value = true
            showToAccount.value = false
            toAccount.value = null
            fromAccount.value = selectedAccount.value
        }
    }
}