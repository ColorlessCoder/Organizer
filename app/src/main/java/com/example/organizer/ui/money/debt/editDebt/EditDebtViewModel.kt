package com.example.organizer.ui.money.debt.editDebt

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.organizer.database.dao.TransactionDAO
import com.example.organizer.database.entity.Account
import com.example.organizer.database.entity.Debt
import com.example.organizer.database.enums.DebtType
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
    val account = MutableLiveData<Account?>()
    val accountName = MutableLiveData<String>()
    val showAccount = MutableLiveData<Boolean>()
    lateinit var transactionDAO: TransactionDAO
    val selectedAccount = MutableLiveData<Account>()
    var initializedNotAllowed = false
    var dueDate: Calendar? = null
    val dueDateText = MutableLiveData<String>()
    val dueTimeText = MutableLiveData<String>()
    var fieldPendingToSetAfterNavigateBack: FIELDS = FIELDS.NONE
    val navigateBack = MutableLiveData<Boolean>()

    companion object {
        enum class FIELDS {
            NONE, ACCOUNT, DEBT_TYPE
        }
    }

    init {
        showAccount.value = true
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
        } else if (debtType.value!! != DebtType.INSTALLMENT && account.value == null) {
            return "Please select to account"
        }
        return ""
    }

    fun setDebtRecord(currentDebtDetails: Debt) {
        debt = currentDebtDetails
        amount.value = currentDebtDetails.amount.toString()
        paidSoFar.value = currentDebtDetails.paidSoFar.toString()
        details.value = currentDebtDetails.details
        if(currentDebtDetails.scheduledAt != null) {
            dueDate = Calendar.getInstance()
            dueDate!!.time = Date(currentDebtDetails.scheduledAt!!)
        } else {
            dueDate = null
        }
        selectDebtType(DebtType.from(currentDebtDetails.debtType))
        showAccount.value = false
    }

    fun saveDebt() {
        viewModelScope.launch {
            val debtRecord = Debt(
                debt?.id ?: UUID.randomUUID().toString(),
                debtType.value!!.typeCode,
                amount.value!!.toDouble(),
                paidSoFar.value!!.toDouble(),
                details.value!!,
                debt?.createdAt ?: Date().time,
                null,
                dueDate?.timeInMillis
            )
            try {
                if (debt == null) {
                    transactionDAO.createDebt(debtRecord, account.value?.id)
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
        if(type != debtType.value) {
            account.value = null
        }
        debtType.value = type
        debtTypeText.value = type.name
        showAccount.value = debtType.value != DebtType.INSTALLMENT
    }
}