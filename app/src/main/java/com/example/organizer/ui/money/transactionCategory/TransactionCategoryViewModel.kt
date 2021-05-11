package com.example.organizer.ui.money.transactionCategory

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.organizer.database.entity.Category
import com.example.organizer.database.enums.TransactionType

class TransactionCategoryViewModel : ViewModel() {
    val transactionTypes = MutableLiveData<List<TransactionType>>()
    var navigatedToSet = NAVIGATED_TO_SET.NONE
    val filterString = MutableLiveData<String>("")
    var allRecords = listOf<Category>()
    init {
        transactionTypes.value = mutableListOf(TransactionType.TRANSFER)
    }
    enum class NAVIGATED_TO_SET {
        NONE,
        TRANSACTION_TYPE
    }
}