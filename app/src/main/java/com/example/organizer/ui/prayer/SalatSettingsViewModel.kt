package com.example.organizer.ui.prayer

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.organizer.database.dao.SalatSettingsDAO
import com.example.organizer.database.services.SalatService
import com.example.organizer.utils.AlarmUtils
import kotlinx.coroutines.launch

class SalatSettingsViewModel : ViewModel() {
    val address = MutableLiveData<String>()
    lateinit var salatService: SalatService
    lateinit var navController: NavController
    var settings: com.example.organizer.database.entity.SalatSettings? = null

    fun populateSalatSettings(settings2: com.example.organizer.database.entity.SalatSettings) {
        settings = settings2
        address.value = settings!!.address!!
    }

    fun save() {
        if(!address.value.isNullOrEmpty()) {
            var downloadAndStartAlarm = false
            var insert = false
            if(settings == null) {
                settings = SalatService.defaultBdSalatSettings()
                insert = true
            } else {
                downloadAndStartAlarm = settings!!.address != address.value!!
            }
            settings!!.address = address.value!!
            viewModelScope.launch {
                if(insert) {
                    salatService.salatSettingsDAO.insert(settings!!)
                } else {
                    salatService.salatSettingsDAO.update(settings!!)
                }
                if (downloadAndStartAlarm) {
                    if(salatService.downloadAndUploadSalatTimes()) {
                        AlarmUtils.startAlarmScheduler(salatService.context, true)
                    }
                }
                navController.popBackStack()
            }
        }
    }
}