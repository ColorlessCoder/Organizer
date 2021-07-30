package com.example.organizer.ui.money.viewTransaction

import androidx.lifecycle.ViewModel
import com.example.organizer.database.relation.TransactionDetails

class SimpleTransactionListViewModel : ViewModel() {
    var transactionList = mutableListOf<TransactionDetails>()
}