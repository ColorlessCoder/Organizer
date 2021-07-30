package com.example.organizer.ui.money.viewTransaction

import androidx.core.util.Pair
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.organizer.database.enums.TransactionType
import com.example.organizer.database.entity.Account
import com.example.organizer.database.entity.Category
import com.example.organizer.database.relation.TransactionDetails
import com.example.organizer.ui.Utils.DateUtils
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.datepicker.MaterialDatePicker
import java.util.*

class ViewTransactionViewModel : ViewModel() {
    val ALL = "<ALL>"
    val EMPTY = "<EMPTY>"
    val filterAccountText  = MutableLiveData<String>()
    var filterAccountValue = mutableListOf<Account>()
    val filterCategoryText  = MutableLiveData<String>()
    var filterCategoryValue = mutableListOf<Category>()
    val filterTypeText  = MutableLiveData<String>()
    var filterTypeValue = mutableListOf<TransactionType>()
    var filterDateRange: Pair<Long, Long>? = Pair(MaterialDatePicker.thisMonthInUtcMilliseconds(), MaterialDatePicker.todayInUtcMilliseconds())
    val filterDateRangeText = MutableLiveData<String>()
    var bottomSheetState: Int = BottomSheetBehavior.STATE_EXPANDED
    var fieldPendingToSetAfterNavigateBack: FIELDS = FIELDS.NONE
    var transactionDetailsList = listOf<TransactionDetails>()

    init {
        clearFilter()
    }

    companion object {
        enum class FIELDS {
            ACCOUNT, CATEGORY, TYPE, NONE, DETAILS
        }
    }

    fun setDateRangeString() {
        if(filterDateRange == null) {
            filterDateRangeText.value = "<ALL>"
        } else {
            filterDateRangeText.value = DateUtils.getDateStringWithMonth(Date(filterDateRange!!.first!!)) + "  to  " + DateUtils.getDateStringWithMonth(Date(filterDateRange!!.second!!))
        }
    }

    fun clear() {
        bottomSheetState = BottomSheetBehavior.STATE_EXPANDED
        fieldPendingToSetAfterNavigateBack = FIELDS.NONE
        clearFilter()
    }

    fun clearFilter() {
        filterAccountText.value = ALL;
        filterCategoryText.value = ALL;
        filterTypeText.value = ALL;
        filterDateRange = Pair(MaterialDatePicker.thisMonthInUtcMilliseconds(), MaterialDatePicker.todayInUtcMilliseconds())
        setDateRangeString()
    }
}