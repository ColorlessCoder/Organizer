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
    val categoryGroup = MutableLiveData<String>()
    val categoryName = MutableLiveData<String>()
    val transactionType = MutableLiveData<TransactionType>()
    var category:Category? = null
    val showDelete = MutableLiveData<Boolean>()

    lateinit var categoryDAO: CategoryDAO;
    lateinit var view: View;

    companion object {
        fun findCategoryGroup(name: String): String {
            var group = "";
            val firstGroupKeyWordIndex = name.indexOfFirst { c -> c == ':' }
            if(firstGroupKeyWordIndex != -1) {
                group = name.substring(0, firstGroupKeyWordIndex).trim()
            }
            return group
        }

        fun findCategoryName(name: String): String {
            var catName = name.trim();
            val firstGroupKeyWordIndex = name.indexOfFirst { c -> c == ':' }
            if(firstGroupKeyWordIndex != -1) {
                catName = name.substring(firstGroupKeyWordIndex + 1).trim()
            }
            return catName
        }
    }

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

    fun setFullCategoryName(name: String) {
        categoryGroup.value = findCategoryGroup(name);
        categoryName.value = findCategoryName(name);
    }

    private fun getFullCategoryName(): String {
        return categoryGroup.value!!.trim() +": " + categoryName.value!!.trim()
    }

    fun save() {
        if(!categoryName.value.isNullOrEmpty() && !categoryGroup.value.isNullOrEmpty()) {
            val fullName = getFullCategoryName()
            viewModelScope.launch {
                if(category == null) {
                    categoryDAO.insert(
                        Category(
                            UUID.randomUUID().toString(),
                            fullName,
                            transactionType.value!!.typeCode,
                            ContextCompat.getColor(view.context, R.color.TealBCWhiteTC),
                            Color.WHITE
                        )
                    );
                } else {
                    category!!.categoryName = fullName
                    categoryDAO.update(category!!)
                }
                view.findNavController().popBackStack()
            }
        }
    }
}