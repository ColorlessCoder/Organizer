package com.example.organizer.ui.money.editDebt

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.organizer.R
import com.example.organizer.database.dao.TransactionDAO
import com.example.organizer.database.enums.TransactionType
import com.example.organizer.database.entity.Account
import com.example.organizer.database.entity.Debt
import com.example.organizer.database.entity.Transaction
import com.example.organizer.database.enums.DebtType
import com.example.organizer.database.relation.DebtDetails
import com.example.organizer.database.services.TransactionsService
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.*

class EditDebtViewModel : ViewModel() {
    private var debt: Debt? = null
    val debtType = MutableLiveData<DebtType>()
    val debtTypeText = MutableLiveData<String>()
    val amount = MutableLiveData<String>()
    val paidSoFar = MutableLiveData<String>()
    val details = MutableLiveData<String>()
    val fromAccount = MutableLiveData<Account>()
    val fromAccountName = MutableLiveData<String>()
    val toAccount = MutableLiveData<Account>()
    val toAccountName = MutableLiveData<String>()
    var backgroundColor = MutableLiveData<Int>()
    val showFromAccount = MutableLiveData<Boolean>()
    val showToAccount = MutableLiveData<Boolean>()
    lateinit var transactionDAO: TransactionDAO
    val selectedAccount = MutableLiveData<Account>()
    var initializedNotAllowed = false
    var dueDate: Date? = null
    val dueDateText = MutableLiveData<String>()
    val dueTimeText = MutableLiveData<String>()
    var fieldPendingToSetAfterNavigateBack: FIELDS = FIELDS.NONE
    val navigateBack = MutableLiveData<Boolean>()

    companion object {
        enum class FIELDS {
            NONE, FROM_ACCOUNT, TO_ACCOUNT, DEBT_TYPE
        }
    }

    init {
        selectDebtType(DebtType.BORROWED)
        navigateBack.value = false
    }

    fun setNavigateBack() {
        navigateBack.value = true
    }

    fun validation(): String {
        val value = if (amount.value != null) amount.value!!.toDouble() else 0.0
        val paidValue = if (paidSoFar.value != null) paidSoFar.value!!.toDouble() else 0.0
        if (value.compareTo(0.0) < 1) {
            return "Amount should be greater than zero"
        } else if (value.compareTo(paidValue) < 1) {
            return "Paid so far should be less than amount"
        } else if (details.value.isNullOrEmpty()) {
            return "Details is required"
        } else if (debtType.value!! == DebtType.BORROWED && toAccount.value == null) {
            return "Please select to account"
        } else if (debtType.value!! == DebtType.LENT && fromAccount.value == null) {
            return "Please select from account"
        }
        return ""
    }

    fun setDebtRecord(currentDebtDetails: DebtDetails) {
        val currentDebt = currentDebtDetails.debt
        debt = currentDebt
        amount.value = currentDebt.amount.toString()
        paidSoFar.value = currentDebt.paidSoFar.toString()
        fromAccountName.value = currentDebtDetails.fromAccountName
        toAccountName.value = currentDebtDetails.toAccountName
        details.value = currentDebt.details
        dueDate = currentDebt.scheduledAt.let { if (it == null) null else Date(it) }
        selectDebtType(DebtType.from(currentDebt.debtType))
    }

    fun saveDebt() {
        viewModelScope.launch {
            val debtRecord = Debt(
                debt?.id ?: UUID.randomUUID().toString(),
                debtType.value!!.typeCode,
                amount.value!!.toDouble(),
                paidSoFar.value!!.toDouble(),
                if (debt == null) fromAccount.value!!.id else debt!!.fromAccount,
                if (debt == null) toAccount.value!!.id else debt!!.toAccount,
                details.value!!,
                debt?.createdAt ?: Date().time,
                null,
                dueDate?.time
            )
            try {
                if (debt == null) {
                    transactionDAO.createDebt(debtRecord)
                } else {
                    transactionDAO.update(debtRecord)
                }
                setNavigateBack()
            } catch (ex: Exception) {
                println("Expection occured" + ex.message)
            }
        }
    }

    fun selectDebtType (type: DebtType) {
        debtType.value = type
        when (type) {
            DebtType.BORROWED -> {
                showFromAccount.value = false
                fromAccount.value = null
                showToAccount.value = true
            }
            DebtType.LENT -> {
                showFromAccount.value = true
                showToAccount.value = false
                toAccount.value = null
            }
            DebtType.INSTALLMENT -> {
                showFromAccount.value = false
                showToAccount.value = false
                fromAccount.value = null
                toAccount.value = null
            }
        }
    }
}