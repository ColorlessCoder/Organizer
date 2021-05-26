package com.example.organizer.ui.money.transactionChart

import android.view.View
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.example.organizer.database.dao.TransactionChartDAO
import com.example.organizer.database.entity.TransactionChart
import com.example.organizer.database.entity.TransactionChartPoint
import com.example.organizer.database.enums.ChartXType
import kotlinx.coroutines.launch
import java.util.*

class AddChartPointViewModel : ViewModel() {
    val fromTransactionId = MutableLiveData(0L)
    val toTransactionId = MutableLiveData(0L)
    val pointLabel = MutableLiveData("")
    lateinit var transactionChartDAO: TransactionChartDAO
    lateinit var chart: TransactionChart
    lateinit var view: View

    fun updateChartRelatedFields() {
        fromTransactionId.value = chart.startAfterTransactionId + 1
    }

    private fun validate(): String {
        if (chart.xType == ChartXType.LABEL.typeCode && pointLabel.value.isNullOrEmpty()) {
            return "Please enter a Label"
        }
        if (fromTransactionId.value!! <= chart.startAfterTransactionId) {
            return "From Transaction Id must be greater than ${chart.startAfterTransactionId}"
        }
        if (fromTransactionId.value!! > toTransactionId.value!!) {
            return "From Transaction Id cannot be greater than Up to transaction Id"
        }
        return ""
    }

    fun add() {
        if (fromTransactionId.value != null && toTransactionId.value != null) {
            val message = validate()
            if (message.isNotEmpty()) {
                Toast.makeText(view.context, message, Toast.LENGTH_SHORT).show()
            } else {
                viewModelScope.launch {
                    transactionChartDAO.addChartPoint(
                        TransactionChartPoint(
                            0,
                            chart.id,
                            pointLabel.value!!,
                            Date().time,
                            fromTransactionId.value!!,
                            toTransactionId.value!!
                        )
                    )
                    view.findNavController().popBackStack()
                }
            }
        }
    }
}