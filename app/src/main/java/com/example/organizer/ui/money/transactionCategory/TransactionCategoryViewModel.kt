package com.example.organizer.ui.money.transactionCategory

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.organizer.database.Enums.TransactionType

class TransactionCategoryViewModel : ViewModel() {
    val transactionTypes = MutableLiveData<List<TransactionType>>()
    var navigatedToSet = NAVIGATED_TO_SET.NONE
    init {
        transactionTypes.value = mutableListOf(TransactionType.TRANSFER)
    }
    enum class NAVIGATED_TO_SET {
        NONE,
        TRANSACTION_TYPE
    }
}