package com.example.organizer.ui.money.transactionChart

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.example.organizer.database.dao.TransactionChartDAO
import com.example.organizer.database.entity.Category
import com.example.organizer.database.entity.TransactionChart
import com.example.organizer.database.enums.ChartEntityType
import com.example.organizer.database.enums.ChartType
import com.example.organizer.database.enums.ChartXType
import com.example.organizer.ui.money.viewTransaction.ViewTransactionViewModel
import kotlinx.coroutines.launch
import java.util.*
import kotlin.properties.Delegates

class EditChartViewModel : ViewModel() {
    val chartName = MutableLiveData<String>("")
    val startAfterTransactionId = MutableLiveData<Long>(0)
    val xType = MutableLiveData(ChartXType.LABEL.name)
    val showExtraOnePoint = MutableLiveData(false)
    val filterCategories = MutableLiveData(false)
    val groupCategories = MutableLiveData(true)
    val groupTransactionType = MutableLiveData(true)
    val extraPointLabel = MutableLiveData<String?>(null)
    var fieldPendingToSetAfterNavigateBack: FIELDS = FIELDS.NONE
    var filterCategoryIds = mutableListOf<String>()
    private var transactionChart: TransactionChart? = null
    lateinit var transactionChartDAO: TransactionChartDAO
    lateinit var view: View
    var chartOrder by Delegates.notNull<Int>()

    companion object {
        enum class FIELDS {
            CATEGORY, NONE
        }
    }
    fun setChart(transactionChartArg: TransactionChart) {
        this.transactionChart = transactionChartArg
        chartName.value = transactionChartArg.chartName
        startAfterTransactionId.value = transactionChartArg.startAfterTransactionId
        xType.value = ChartXType.from(transactionChartArg.xType).name
        showExtraOnePoint.value = transactionChartArg.showExtraOnePoint == 1
        filterCategories.value = transactionChartArg.filterCategories == 1
        groupCategories.value = transactionChartArg.groupCategories == 1
        groupTransactionType.value = transactionChartArg.groupTransactionType == 1
        extraPointLabel.value = transactionChartArg.extraPointLabel
    }

    fun save() {
        if (!chartName.value.isNullOrEmpty()) {
            viewModelScope.launch {
                val insert = transactionChart == null
                if (insert) {
                    transactionChart =
                        TransactionChart(
                            id = UUID.randomUUID().toString(),
                            chartName = chartName.value!!.trim(),
                            chartType = ChartType.LINE_CHART.typeCode,
                            chartOrder = chartOrder,
                            chartEntity = ChartEntityType.CATEGORY.typeCode,
                            xType = ChartXType.fromName(xType.value!!).typeCode,
                            startAfterTransactionId = 0,
                            showExtraOnePoint = if(showExtraOnePoint.value == true) 1 else 0,
                            extraPointLabel = if(showExtraOnePoint.value == true) extraPointLabel.value else null,
                            filterCategories = if(filterCategories.value == true) 1 else 0,
                            groupCategories = if(groupCategories.value == true) 1 else 0,
                            groupTransactionType = if(groupTransactionType.value == true) 1 else 0
                        )
                } else {
                    transactionChart!!.chartName = chartName.value!!
                    transactionChart!!.showExtraOnePoint = if(showExtraOnePoint.value == true) 1 else 0
                    transactionChart!!.extraPointLabel = if(showExtraOnePoint.value == true) extraPointLabel.value else null
                }
                transactionChartDAO.saveTransactionChart(transactionChart!!, insert, filterCategoryIds.toSet())
                view.findNavController().popBackStack()
            }
        }
    }
}