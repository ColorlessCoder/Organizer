package com.example.organizer.ui.money

import android.graphics.Color
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.organizer.database.entity.TransactionPlan

class MoneyFragmentViewModel : ViewModel(){
    val applyTransactionPlanId = MutableLiveData<String>()
}