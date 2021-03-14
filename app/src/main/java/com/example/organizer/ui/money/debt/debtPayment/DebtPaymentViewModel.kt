package com.example.organizer.ui.money.debt.debtPayment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.organizer.database.entity.Account
import com.example.organizer.database.entity.Debt

class DebtPaymentViewModel : ViewModel() {
    lateinit var debt: Debt
    val amount = MutableLiveData<String>()
    val account = MutableLiveData<Account>()
    val details = MutableLiveData<String>()
    var navigatedToSet: NavigatedToSet = NavigatedToSet.NONE
    companion object {
        enum class NavigatedToSet{
            NONE, ACCOUNT
        }
    }
}