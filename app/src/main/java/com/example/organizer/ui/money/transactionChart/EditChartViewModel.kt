package com.example.organizer.ui.money.transactionChart

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.example.organizer.database.dao.TransactionChartDAO
import com.example.organizer.database.entity.TransactionChart
import com.example.organizer.database.enums.ChartEntityType
import com.example.organizer.database.enums.ChartType
import com.example.organizer.database.enums.ChartXType
import kotlinx.coroutines.launch
import java.util.*
import kotlin.properties.Delegates

class EditChartViewModel : ViewModel() {
    val chartName = MutableLiveData<String>("")
    val startAfterTransactionId = MutableLiveData<Long>(0)
    val xType = MutableLiveData(ChartXType.LABEL.name)
    val showExtraOnePoint = MutableLiveData(false)
    val extraPointLabel = MutableLiveData<String?>(null)
    private var transactionChart: TransactionChart? = null
    lateinit var transactionChartDAO: TransactionChartDAO
    lateinit var view: View
    var chartOrder by Delegates.notNull<Int>()

    fun setChart(transactionChartArg: TransactionChart) {
        this.transactionChart = transactionChartArg
        chartName.value = transactionChartArg.chartName
        startAfterTransactionId.value = transactionChartArg.startAfterTransactionId
        xType.value = ChartXType.from(transactionChartArg.xType).name
        showExtraOnePoint.value = transactionChartArg.showExtraOnePoint == 1
        extraPointLabel.value = transactionChartArg.extraPointLabel
    }

    fun save() {
        if (!chartName.value.isNullOrEmpty()) {
            viewModelScope.launch {
                if (transactionChart == null) {
                    transactionChartDAO.insert(
                        TransactionChart(
                            id = UUID.randomUUID().toString(),
                            chartName = chartName.value!!.trim(),
                            chartType = ChartType.LINE_CHART.typeCode,
                            chartOrder = chartOrder,
                            chartEntity = ChartEntityType.CATEGORY.typeCode,
                            xType = ChartXType.fromName(xType.value!!).typeCode,
                            startAfterTransactionId = 0,
                            showExtraOnePoint = if(showExtraOnePoint.value == true) 1 else 0,
                            extraPointLabel = if(showExtraOnePoint.value == true) extraPointLabel.value else null
                        )
                    )
                } else {
                    transactionChart!!.chartName = chartName.value!!
                    transactionChart!!.showExtraOnePoint = if(showExtraOnePoint.value == true) 1 else 0
                    transactionChart!!.extraPointLabel = if(showExtraOnePoint.value == true) extraPointLabel.value else null
                    transactionChartDAO.update(transactionChart!!)
                }
                view.findNavController().popBackStack()
            }
        }
    }
}