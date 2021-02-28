package com.example.organizer.ui.money.transactionPlan

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.example.organizer.database.dao.TransactionPlanDAO
import com.example.organizer.database.entity.TransactionPlan
import com.example.organizer.ui.Utils.ColorUtil
import com.example.organizer.ui.money.ColorSpinnerAdapter
import kotlinx.coroutines.launch
import java.util.*

class EditTransactionPlanViewModel : ViewModel() {
    val name = MutableLiveData<String>()
    val colorIndex = MutableLiveData<Int>()
    val colors = ColorUtil.getAllWhiteTextBackgroundColors()
    var transactionPlan: TransactionPlan? = null
    lateinit var adapter: ColorSpinnerAdapter
    lateinit var transactionPlanDAO: TransactionPlanDAO
    lateinit var view: View

    init {
        colorIndex.value = 0
    }

    fun save() {
        if (!name.value.isNullOrEmpty()) {
            val color = colors[colorIndex.value!!]
            viewModelScope.launch {
                if (transactionPlan != null) {
                    transactionPlan!!.name = name.value!!
                    transactionPlan!!.color = color
                    transactionPlanDAO.update(transactionPlan!!)
                } else {
                    transactionPlanDAO.insert(
                        TransactionPlan(
                            UUID.randomUUID().toString(),
                            name.value!!,
                            0,
                            color
                        )
                    )
                }
                view.findNavController().popBackStack()
            }
        }
    }

    fun delete() {
        if (transactionPlan != null) {
            viewModelScope.launch {
                transactionPlanDAO.deleteById(transactionPlan!!.id)
                view.findNavController().popBackStack()
            }
        }
    }
}