package com.example.organizer.ui.money.transactionCategory

import android.graphics.Color
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.example.organizer.R
import com.example.organizer.database.enums.TransactionType
import com.example.organizer.database.dao.CategoryDAO
import com.example.organizer.database.entity.Category
import kotlinx.coroutines.launch
import java.util.UUID

class EditCategoryViewModel : ViewModel() {
    val categoryName = MutableLiveData<String>()
    val transactionType = MutableLiveData<TransactionType>()
    var category:Category? = null
    val showDelete = MutableLiveData<Boolean>()

    lateinit var categoryDAO: CategoryDAO;
    lateinit var view: View;

    init {
        showDelete.value = false
    }

    fun delete() {
        if(category != null) {
            viewModelScope.launch {
                categoryDAO.delete(category!!)
                view.findNavController().popBackStack()
            }
        }
    }

    fun save() {
        if(!categoryName.value.isNullOrEmpty()) {
            viewModelScope.launch {
                if(category == null) {
                    categoryDAO.insert(
                        Category(
                            UUID.randomUUID().toString(),
                            categoryName.value!!,
                            transactionType.value!!.typeCode,
                            ContextCompat.getColor(view.context, R.color.TealBCWhiteTC),
                            Color.WHITE
                        )
                    );
                } else {
                    category!!.categoryName = categoryName.value!!
                    categoryDAO.update(category!!)
                }
                view.findNavController().popBackStack()
            }
        }
    }
}