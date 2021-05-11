package com.example.organizer.ui.money.transactionPlan

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.organizer.R
import com.example.organizer.database.enums.TransactionType
import com.example.organizer.database.dao.TemplateTransactionDAO
import com.example.organizer.database.entity.Account
import com.example.organizer.database.entity.Category
import com.example.organizer.database.entity.TemplateTransaction
import kotlinx.coroutines.launch
import java.util.*

class EditTemplateTransactionViewModel : ViewModel() {
    var transactionType = MutableLiveData<Int>()
    val amount = MutableLiveData<String>()
    val details = MutableLiveData<String>()
    val fromAccount = MutableLiveData<Account?>()
    val toAccount = MutableLiveData<Account?>()
    val category = MutableLiveData<Category?>()
    var backgroundColor = MutableLiveData<Int>()
    val showFromAccount = MutableLiveData<Boolean>()
    val showToAccount = MutableLiveData<Boolean>()
    val selectedAccount = MutableLiveData<Account>()
    var fieldPendingToSetAfterNavigateBack: FIELDS = FIELDS.NONE
    val navigateBack = MutableLiveData<Boolean>()
    var templateTransaction: TemplateTransaction? = null
    lateinit var templateTransactionDAO: TemplateTransactionDAO
    lateinit var transactionPlanId: String
    var order: Int = 0

    companion object {
        enum class FIELDS {
            FROM_ACCOUNT, TO_ACCOUNT, CATEGORY, NONE
        }
    }

    init {
        selectTransactionType(0)
        navigateBack.value = false
    }

    fun selectTransferType() {
        if(transactionType.value!! != TransactionType.TRANSFER.typeCode) {
            selectTransactionType(TransactionType.TRANSFER.typeCode)
        }
    }

    fun selectIncomeType() {
        if(transactionType.value!! != TransactionType.INCOME.typeCode) {
            selectTransactionType(TransactionType.INCOME.typeCode)
        }
    }

    fun selectExpenseType() {
        if(transactionType.value!! != TransactionType.EXPENSE.typeCode) {
            selectTransactionType(TransactionType.EXPENSE.typeCode)
        }
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

    fun saveTransaction() {
        if (amount.value != null && isTheAmountAllowed()) {
            viewModelScope.launch {
                if (templateTransaction == null) {
                    val transaction = TemplateTransaction(
                        UUID.randomUUID().toString(),
                        transactionPlanId,
                        transactionType.value!!,
                        amount.value!!.toDouble(),
                        if (fromAccount.value == null) null else fromAccount.value!!.id,
                        if (toAccount.value == null) null else toAccount.value!!.id,
                        if (category.value == null) null else category.value!!.id,
                        details.value!!.trim(),
                        order
                    )
                    try {
                        templateTransactionDAO.insert(transaction)
                        setNavigateBack()
                    } catch (ex: Exception) {
                        println("Expection occured" + ex.message)
                    }
                } else {
                    templateTransaction!!.fromAccount = if (fromAccount.value == null) null else fromAccount.value!!.id
                    templateTransaction!!.toAccount = if (toAccount.value == null) null else toAccount.value!!.id
                    templateTransaction!!.transactionCategoryId = if (category.value == null) null else category.value!!.id
                    templateTransaction!!.details = details.value!!.trim()
                    templateTransaction!!.transactionType = transactionType.value!!
                    templateTransaction!!.amount = amount.value!!.toDouble()
                    try {
                        templateTransactionDAO.update(templateTransaction!!)
                        setNavigateBack()
                    } catch (ex: Exception) {
                        println("Expection occured" + ex.message)
                    }
                }
            }
        }
    }

    fun selectTransactionType(type: Int) {
        println(type)
        transactionType.value = type
        category.value = null
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