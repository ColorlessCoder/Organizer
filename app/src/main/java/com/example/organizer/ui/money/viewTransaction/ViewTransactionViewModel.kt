package com.example.organizer.ui.money.viewTransaction

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.organizer.database.enums.TransactionType
import com.example.organizer.database.entity.Account
import com.example.organizer.database.entity.Category
import com.google.android.material.bottomsheet.BottomSheetBehavior

class ViewTransactionViewModel : ViewModel() {
    val ALL = "<ALL>"
    val EMPTY = "<EMPTY>"
    val filterAccountText  = MutableLiveData<String>()
    var filterAccountValue = mutableListOf<Account>()
    val filterCategoryText  = MutableLiveData<String>()
    var filterCategoryValue = mutableListOf<Category>()
    val filterTypeText  = MutableLiveData<String>()
    var filterTypeValue = mutableListOf<TransactionType>()
    val filterDays = MutableLiveData<String>()
    var previousFilterDays: String = "30";
    var bottomSheetState: Int = BottomSheetBehavior.STATE_EXPANDED
    var fieldPendingToSetAfterNavigateBack: FIELDS = FIELDS.NONE

    init {
        clearFilter()
    }

    companion object {
        enum class FIELDS {
            ACCOUNT, CATEGORY, TYPE, NONE
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
        filterDays.value = "30";
        previousFilterDays = "30";
    }
}