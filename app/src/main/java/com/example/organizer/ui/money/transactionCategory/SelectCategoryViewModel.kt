package com.example.organizer.ui.money.transactionCategory

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.organizer.database.entity.Category
import com.example.organizer.ui.money.common.CommonSelectViewModel

class SelectCategoryViewModel():CommonSelectViewModel<Category>() {
    override fun areSameRecord(a: Category, b: Category): Boolean {
        return a.id == b.id
    }
}