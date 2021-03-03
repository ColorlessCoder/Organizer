package com.example.organizer.ui.money.viewTransaction

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.organizer.database.Enums.TransactionType
import com.example.organizer.database.entity.Account
import com.example.organizer.database.entity.Category
import com.google.android.material.bottomsheet.BottomSheetBehavior

class ViewTransactionViewModel : ViewModel() {
    val ALL = "<ALL>"
    val EMPTY = "<EMPTY>>"
    var filterAccountText :String = ALL
    var filterAccountValue = mutableListOf<Account>()
    var filterCategoryText :String = ALL
    var filterCategoryValue = mutableListOf<Category>()
    var filterTypeText :String = ALL
    var filterTypeValue = mutableListOf<TransactionType>()
    var filterDays: String = "30";
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
        filterAccountText = ALL;
        filterCategoryText = ALL;
        filterTypeText = ALL;
        filterDays = "30";
    }
}