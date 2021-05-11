package com.example.organizer.ui.money.editAccount

import android.graphics.Color
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.organizer.database.dao.AccountDAO
import com.example.organizer.database.entity.Account
import com.example.organizer.ui.Utils.ColorUtil
import com.example.organizer.ui.money.ColorSpinnerAdapter
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.*

class EditAccountViewModel() : ViewModel() {
    val accountName = MutableLiveData<String>()
    val amount = MutableLiveData<String>()
    val backgroundColorIndex = MutableLiveData<Int>()
    val id = MutableLiveData<String>()
    private val colors = ColorUtil.getAllWhiteTextBackgroundColors()
    val navigateBack = MutableLiveData<Boolean>()
    var isCreating = MutableLiveData<Boolean>()
    var navigateToAddTransaction = MutableLiveData<Boolean>()
    var navigateToViewTransaction = MutableLiveData<Boolean>()
    lateinit var adapter: ColorSpinnerAdapter;
    lateinit var accountDAO: AccountDAO
    init {
        backgroundColorIndex.value = 0
        id.value = UUID.randomUUID().toString()
        navigateBack.value = false
        isCreating.value = true
    }

    fun historyClicked() {
        navigateToViewTransaction.value = true
    }

    fun delete() {
        viewModelScope.launch {
            try {
                accountDAO.deleteById(id.value!!)
            } catch (e:Exception) {
                println(e.message)
            }
            navigateBack()
        }
    }

    fun addTransaction() {
        navigateToAddTransaction.value = true
    }

    private fun navigateBack() {
        navigateBack.value = true
    }

    fun save() {
        println(accountName.value)
        println(amount.value)
        println(backgroundColorIndex.value)
        if(accountName.value != null && amount.value != null) {
            viewModelScope.launch {
                val account = Account(
                       id.value!!,
                        accountName.value!!.trim(),
                        amount.value!!.toDouble(),
                        colors.get(backgroundColorIndex.value!!),
                        Color.WHITE,
                        "BDT"
                    );
                try {
                    if (isCreating.value!!) {
                        accountDAO.insert(account)
                    } else {
                        accountDAO.update(account)
                    }
                    navigateBack()
                } catch (ex: Exception) {
                    if(ex.localizedMessage.contains("UNIQUE")) {
                        println("Similar Account Name exists")
                    } else {
                        println("Expection occured" + ex.message)
                    }
                }
            }
        }
    }
}
