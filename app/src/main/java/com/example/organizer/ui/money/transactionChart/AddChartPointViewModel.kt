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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import java.util.*

class AddChartPointViewModel : ViewModel() {
    val fromTransactionId = MutableLiveData("0")
    val toTransactionId = MutableLiveData("0")
    val pointLabel = MutableLiveData("")
    val insert = MutableLiveData(true)
    var transactionChartPoint: TransactionChartPoint? = null
    var deleteEnabled = MutableLiveData<Boolean>(false)
    lateinit var transactionChartDAO: TransactionChartDAO
    lateinit var chart: TransactionChart
    lateinit var view: View

    fun updateChartRelatedFields() {
        fromTransactionId.value = (chart.startAfterTransactionId + 1).toString()
    }

    fun setPoint(point: TransactionChartPoint) {
        transactionChartPoint = point
        pointLabel.value = point.label
        fromTransactionId.value = point.fromTransactionId.toString()
        toTransactionId.value = point.toTransactionId.toString()
    }

    private fun validate(): String {
        val from = (fromTransactionId.value?:"0").toLong()
        val to = (toTransactionId.value?:"0").toLong()
        if (chart.xType == ChartXType.LABEL.typeCode && pointLabel.value.isNullOrEmpty()) {
            return "Please enter a Label"
        }
        if (from <= chart.startAfterTransactionId) {
            return "From Transaction Id must be greater than ${chart.startAfterTransactionId}"
        }
        if (from > to) {
            return "From Transaction Id cannot be greater than Up to transaction Id"
        }
        return ""
    }

    fun regenerate() {
        MaterialAlertDialogBuilder(view.context)
            .setTitle("Regenerate")
            .setMessage("Are you sure? All values of this point will be deleted and generated again.")
            .setPositiveButton("Yes") { _, _ ->
                viewModelScope.launch {
                    transactionChartDAO.regenerateValues(transactionChartPoint!!.id)
                    view.findNavController().popBackStack()
                }
            }
            .setNegativeButton("No"){_,_ -> }
            .show()
    }

    fun delete() {
        MaterialAlertDialogBuilder(view.context)
            .setTitle("Delete")
            .setMessage("Are you sure? All values of this point will be deleted.")
            .setPositiveButton("Yes") { _, _ ->
                viewModelScope.launch {
                    transactionChartDAO.deletePoint(transactionChartPoint!!.id, transactionChartPoint!!.chartId)
                    view.findNavController().popBackStack()
                }
            }
            .setNegativeButton("No"){_,_ -> }
            .show()
    }

    fun add() {
        if (fromTransactionId.value != null && toTransactionId.value != null) {
            val message = validate()
            if (message.isNotEmpty()) {
                Toast.makeText(view.context, message, Toast.LENGTH_SHORT).show()
            } else {
                viewModelScope.launch {
                    if (transactionChartPoint == null) {
                        transactionChartDAO.addChartPoint(
                            TransactionChartPoint(
                                0,
                                chart.id,
                                pointLabel.value!!.trim(),
                                Date().time,
                                (fromTransactionId.value?:"0").toLong(),
                                (toTransactionId.value?:"0").toLong()
                            ), true
                        )
                    } else {
                        transactionChartPoint!!.label = pointLabel.value!!.trim()
                        transactionChartDAO.update(transactionChartPoint!!)
                    }
                    view.findNavController().popBackStack()
                }
            }
        }
    }
}