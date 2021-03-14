package com.example.organizer.ui.money.debt.debtlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.organizer.database.dao.DebtDAO
import com.example.organizer.database.entity.Debt
import kotlinx.coroutines.launch
import java.util.*

class DebtListViewModel : ViewModel() {
    lateinit var debtDAO: DebtDAO
    fun deleteDebt(debtId: String) {
        viewModelScope.launch {
            debtDAO.deleteDebtById(debtId)
        }
    }
    fun completeDebt(debt: Debt) {
        debt.completedAt = Date().time
        viewModelScope.launch {
            debtDAO.update(debt)
        }
    }
}