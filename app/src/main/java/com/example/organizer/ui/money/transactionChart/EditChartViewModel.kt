package com.example.organizer.ui.money.transactionChart

import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.example.organizer.R
import com.example.organizer.database.dao.TransactionChartDAO
import com.example.organizer.database.entity.Category
import com.example.organizer.database.entity.TransactionChart
import com.example.organizer.database.enums.*
import com.example.organizer.ui.Utils.DateUtils
import com.example.organizer.ui.money.viewTransaction.ViewTransactionViewModel
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch
import java.util.*
import kotlin.properties.Delegates

class EditChartViewModel : ViewModel() {
    val chartName = MutableLiveData<String>("")

    var xTypeList = listOf(ChartXType.LABEL.name, ChartXType.DATE.name)
    var weekDays = WeekDays.getAllDays()
    var scheduleTypeList = ScheduleIntervalType.getAll()

    val startAfterTransactionId = MutableLiveData<Long>(0)
    val xType = MutableLiveData("")
    val showExtraOnePoint = MutableLiveData(false)
    val filterCategories = MutableLiveData(false)
    val groupCategories = MutableLiveData(true)
    val savePoint = MutableLiveData(true)
    val groupTransactionType = MutableLiveData(true)
    val extraPointLabel = MutableLiveData<String?>(null)
    var fieldPendingToSetAfterNavigateBack: FIELDS = FIELDS.NONE
    val scheduleIntervalType = MutableLiveData<String>()
    val generateAtTime = MutableLiveData("")
    val generateAtDate = MutableLiveData("")
    val generateAtDay = MutableLiveData("")
    var generateAtCalendar: Calendar? = null
    var filterCategoryIds = mutableListOf<String>()
    var clone: Boolean = false
    var insert: Boolean = true
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
        if(!clone) {
            chartName.value = transactionChartArg.chartName
        }
        startAfterTransactionId.value = transactionChartArg.startAfterTransactionId
        setXTypeInView(ChartXType.from(transactionChartArg.xType).name)
        showExtraOnePoint.value = transactionChartArg.showExtraOnePoint == 1
        filterCategories.value = transactionChartArg.filterCategories == 1
        groupCategories.value = transactionChartArg.groupCategories == 1
        groupTransactionType.value = transactionChartArg.groupTransactionType == 1
        extraPointLabel.value = transactionChartArg.extraPointLabel
        savePoint.value = transactionChartArg.savePoint == 1
        setScheduleTypeInView(ScheduleIntervalType.from(transactionChartArg.scheduleIntervalType).label)
        if(transactionChartArg.generateAt != null) {
            val cal = Calendar.getInstance()
            cal.time = Date(transactionChartArg.generateAt!!.toLong())
            setGenerateDate(cal)
            setGenerateTime(cal.get(Calendar.HOUR), cal.get(Calendar.MINUTE))
        }
    }

    fun save() {
        val msg = validate()
        if (msg.isNullOrEmpty()) {
            populatedGenerateAt()
            val cloneId = if(clone && transactionChart != null) transactionChart!!.id else null
            viewModelScope.launch {
                if (insert) {
                    transactionChart =
                        TransactionChart(
                            id = UUID.randomUUID().toString(),
                            chartName = chartName.value!!.trim(),
                            chartType = ChartType.LINE_CHART.typeCode,
                            chartOrder = chartOrder,
                            chartEntity = ChartEntityType.CATEGORY.typeCode,
                            xType = ChartXType.fromName(xType.value!!).typeCode,
                            startAfterTransactionId = if(transactionChart == null ) 0 else transactionChart!!.startAfterTransactionId,
                            showExtraOnePoint = if(showExtraOnePoint.value == true) 1 else 0,
                            extraPointLabel = if(showExtraOnePoint.value == true) extraPointLabel.value else null,
                            filterCategories = if(filterCategories.value == true) 1 else 0,
                            groupCategories = if(groupCategories.value == true) 1 else 0,
                            groupTransactionType = if(groupTransactionType.value == true) 1 else 0,
                            scheduleIntervalType = ScheduleIntervalType.fromLabel(scheduleIntervalType.value!!).typeCode,
                            savePoint = if(savePoint.value == true) 1 else 0,
                            generateAt = generateAtCalendar?.timeInMillis,
                            lastGeneratedAt = null
                        )
                } else {
                    transactionChart!!.chartName = chartName.value!!
                    transactionChart!!.showExtraOnePoint = if(showExtraOnePoint.value == true) 1 else 0
                    transactionChart!!.filterCategories = if(filterCategories.value == true) 1 else 0
                    transactionChart!!.groupCategories = if(groupCategories.value == true) 1 else 0
                    transactionChart!!.groupTransactionType = if(groupTransactionType.value == true) 1 else 0
                    transactionChart!!.extraPointLabel = if(showExtraOnePoint.value == true) extraPointLabel.value else null
                }
                transactionChartDAO.saveTransactionChart(transactionChart!!, insert, cloneId, filterCategoryIds.toSet())
                view.findNavController().popBackStack()
            }
        } else {
            Toast.makeText(view.context, msg, Toast.LENGTH_SHORT).show()
        }
    }

    private fun validate(): String? {
        if(chartName.value.isNullOrEmpty()) {
            return "Please insert a chart name."
        } else if (scheduleIntervalType.value == ScheduleIntervalType.WEEKLY.label && generateAtDay.value.isNullOrEmpty()) {
            return "Please select a Day"
        } else if (scheduleIntervalType.value == ScheduleIntervalType.MONTHLY.label && generateAtDate.value.isNullOrEmpty()) {
            return "Please select a date"
        } else if (scheduleIntervalType.value != ScheduleIntervalType.NONE.label && generateAtTime.value.isNullOrEmpty()) {
            return "Please select a time"
        }
        return null
    }

    private fun populatedGenerateAt() {
        when(scheduleIntervalType.value) {
            ScheduleIntervalType.WEEKLY.label -> {
                val cal = Calendar.getInstance()
                cal.set(Calendar.DAY_OF_WEEK, WeekDays.fromLabel(generateAtDay.value!!).calendarConst)
                setGenerateDate(cal)
            }
            else -> {}
        }
    }

    fun setGenerateDate(cal: Calendar) {
        if(generateAtCalendar == null) {
            generateAtCalendar = cal
        } else {
            generateAtCalendar!!.set(Calendar.DATE, cal.get(Calendar.DATE))
            generateAtCalendar!!.set(Calendar.MONTH, cal.get(Calendar.MONTH))
            generateAtCalendar!!.set(Calendar.YEAR, cal.get(Calendar.YEAR))
        }
        generateAtDay.value = WeekDays.from(cal.get(Calendar.DAY_OF_WEEK)).label
        setDayInView(generateAtDay.value!!)
        generateAtDate.value = DateUtils.getDateStringWithMonth(cal.time)
    }

    fun setGenerateTime(hour: Int, min: Int) {
        if(generateAtCalendar == null) {
            generateAtCalendar = Calendar.getInstance()
        }
        generateAtCalendar!!.set(Calendar.HOUR, hour)
        generateAtCalendar!!.set(Calendar.MINUTE, min)
        generateAtTime.value = "$hour : $min"
    }

    fun setXTypeInView(label: String) {
        println(label)
        xType.value = label
        val textField = view.findViewById<TextInputLayout>(R.id.show_count_by)
        (textField.editText as? AutoCompleteTextView)?.setText(label, false)
    }

    fun setDayInView(label: String) {
        generateAtDay.value = label
        val textField = view.findViewById<TextInputLayout>(R.id.generated_day)
        (textField.editText as? AutoCompleteTextView)?.setText(label, false)
    }

    fun setScheduleTypeInView(label: String) {
        scheduleIntervalType.value = label
        val autoGenerate = view.findViewById<TextInputLayout>(R.id.auto_generate)
        (autoGenerate.editText as? AutoCompleteTextView)?.setText(label, false)
    }
}