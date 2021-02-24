package com.example.organizer.ui.money.transactionCategory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.organizer.database.Enums.TransactionType
import com.example.organizer.database.entity.Category

class TransactionCategoryViewModel : ViewModel() {
    val transactionType = MutableLiveData<TransactionType>()
    var navigatedToSet = NAVIGATED_TO_SET.NONE
    init {
        transactionType.value = TransactionType.TRANSFER
    }
    enum class NAVIGATED_TO_SET {
        NONE,
        TRANSACTION_TYPE
    }
}