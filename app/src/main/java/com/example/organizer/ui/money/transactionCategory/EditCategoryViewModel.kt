package com.example.organizer.ui.money.transactionCategory

import android.graphics.Color
import android.view.View
import android.widget.Toast
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
    val subCategoryGroup1 = MutableLiveData<String>()
    val subCategoryGroup2 = MutableLiveData<String>()
    val categoryName = MutableLiveData<String>()
    val transactionType = MutableLiveData<TransactionType>()
    val isCreating = MutableLiveData<Boolean>(true)
    var category: Category? = null
    val showDelete = MutableLiveData<Boolean>()
    val showClone = MutableLiveData<Boolean>(false)

    lateinit var categoryDAO: CategoryDAO;
    lateinit var view: View

    companion object {
        fun findCategoryGroup(name: String): String {
            var group = "";
            val firstGroupKeyWordIndex = name.indexOfFirst { c -> c == ':' }
            if (firstGroupKeyWordIndex != -1) {
                group = name.substring(0, firstGroupKeyWordIndex).trim()
            }
            return group
        }

        fun findCategoryName(name: String): String {
            var catName = name.trim()
            val firstGroupKeyWordIndex = name.indexOfFirst { c -> c == ':' }
            if (firstGroupKeyWordIndex != -1) {
                catName = name.substring(firstGroupKeyWordIndex + 1).trim()
            }
            return catName
        }
    }

    init {
        showDelete.value = false
    }

    fun delete() {
        if (category != null) {
            viewModelScope.launch {
                categoryDAO.delete(category!!)
                view.findNavController().popBackStack()
            }
        }
    }

    fun clone() {
        category = null
        showClone.value = false
        categoryName.value = ""
        isCreating.value = true
        showDelete.value = false
    }

    fun setFullCategoryName(name: String) {
        categoryGroup.value = findCategoryGroup(name)
        var value = findCategoryName(name)
        subCategoryGroup1.value = findCategoryGroup(value)
        value = findCategoryName(value)
        subCategoryGroup2.value = findCategoryGroup(value)
        categoryName.value = findCategoryName(value)
    }

    private fun formatCategoryGroupValue(group: String?): String {
        if (group.isNullOrEmpty()) return ""
        return group.trim() + ": "
    }

    private fun validateField(value: String?): Boolean {
        val check = value ?: ""
        return !check.contains(":") && !check.contains(" ")
    }

    private fun validateForm(): Boolean {
        return validateField(categoryGroup.value) && validateField(subCategoryGroup1.value) && validateField(
            subCategoryGroup2.value
        ) && validateField(categoryName.value)
                && !categoryGroup.value.isNullOrEmpty() && !categoryName.value.isNullOrEmpty()
    }

    private fun getFullCategoryName(): String {
        return formatCategoryGroupValue(categoryGroup.value) + formatCategoryGroupValue(
            subCategoryGroup1.value
        ) + formatCategoryGroupValue(subCategoryGroup2.value) + categoryName.value!!.trim()
    }

    fun save() {
        if (validateForm()) {
            val fullName = getFullCategoryName()
            viewModelScope.launch {
                val existingCategories = categoryDAO.getCategoriesLikeGroup("$fullName:%")
                if (existingCategories.isNotEmpty()) {
                    Toast.makeText(view.context, "$fullName is already a group", Toast.LENGTH_LONG)
                        .show()
                } else {
                    if (category == null) {
                        categoryDAO.insert(
                            Category(
                                UUID.randomUUID().toString(),
                                fullName,
                                transactionType.value!!.typeCode,
                                ContextCompat.getColor(view.context, R.color.TealBCWhiteTC),
                                Color.WHITE
                            )
                        )
                    } else {
                        category!!.categoryName = fullName
                        categoryDAO.update(category!!)
                    }
                    view.findNavController().popBackStack()
                }
            }
        }
    }
}