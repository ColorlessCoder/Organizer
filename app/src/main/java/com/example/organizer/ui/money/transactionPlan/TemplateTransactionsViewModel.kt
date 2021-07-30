package com.example.organizer.ui.money.transactionPlan

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.organizer.database.entity.TemplateTransaction
import com.example.organizer.database.relation.TemplateTransactionDetails

class TemplateTransactionsViewModel : ViewModel() {
    val dragStarted = MutableLiveData<Boolean>()
    var templates: List<TemplateTransactionDetails> = mutableListOf()
    lateinit var transactionPlanId: String
    init {
        dragStarted.value = false;
    }
}