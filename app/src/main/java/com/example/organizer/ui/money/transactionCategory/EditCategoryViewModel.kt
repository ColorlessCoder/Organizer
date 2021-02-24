package com.example.organizer.ui.money.transactionCategory

import android.graphics.Color
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.example.organizer.database.Enums.TransactionType
import com.example.organizer.database.dao.CategoryDAO
import com.example.organizer.database.entity.Category
import kotlinx.coroutines.launch
import java.util.UUID

class EditCategoryViewModel : ViewModel() {
    val categoryName = MutableLiveData<String>()
    val transactionType = MutableLiveData<TransactionType>()

    lateinit var categoryDAO: CategoryDAO;
    lateinit var view: View;

    fun save() {
        if(!categoryName.value.isNullOrEmpty()) {
            viewModelScope.launch {
                categoryDAO.insert(
                    Category(
                        UUID.randomUUID().toString(),
                        categoryName.value!!,
                        transactionType.value!!.typeCode,
                        Color.BLUE,
                        Color.WHITE
                    )
                );
                view.findNavController().popBackStack()
            }
        }
    }
}