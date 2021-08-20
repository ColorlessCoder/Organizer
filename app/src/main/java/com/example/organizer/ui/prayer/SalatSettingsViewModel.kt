package com.example.organizer.ui.prayer

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.organizer.database.services.SalatService
import com.example.organizer.utils.SalatAlarmUtils
import kotlinx.coroutines.launch

class SalatSettingsViewModel : ViewModel() {
    lateinit var salatService: SalatService
    lateinit var navController: NavController
    var settings: com.example.organizer.database.entity.SalatSettings? = null
    val address = MutableLiveData<String>()
    val fajrAlert = MutableLiveData("0")
    val dhuhrAlert = MutableLiveData("0")
    val asrAlert = MutableLiveData("0")
    val maghribAlert = MutableLiveData("0")
    val ishaAlert = MutableLiveData("0")
    val salatAlert = MutableLiveData(false)
    val fajrAlertActive = MutableLiveData(false)
    val dhuhrAlertActive = MutableLiveData(false)
    val asrAlertActive = MutableLiveData(false)
    val maghribAlertActive = MutableLiveData(false)
    val ishaAlertActive = MutableLiveData(false)

    fun populateSalatSettings(settings2: com.example.organizer.database.entity.SalatSettings) {
        settings = settings2
        address.value = settings2.address!!
        salatAlert.value = settings2.salatAlert == 1
        fajrAlertActive.value = settings2.fajrAlertActive == 1
        dhuhrAlertActive.value = settings2.dhuhrAlertActive == 1
        asrAlertActive.value = settings2.asrAlertActive == 1
        maghribAlertActive.value = settings2.maghribAlertActive == 1
        ishaAlertActive.value = settings2.ishaAlertActive == 1
        fajrAlert.value = settings2.fajrAlert.toString()
        dhuhrAlert.value = settings2.dhuhrAlert.toString()
        asrAlert.value = settings2.asrAlert.toString()
        maghribAlert.value = settings2.maghribAlert.toString()
        ishaAlert.value = settings2.ishaAlert.toString()
    }

    private fun convertStringToInt(str: String?): Int {
        return if (str.isNullOrEmpty()) 0 else str.toInt()
    }

    private fun convertBooleanToInt(value: Boolean?): Int {
        return if (value == true) 1 else 0
    }

    fun save() {
        if (!address.value.isNullOrEmpty()) {
            var downloadAndStartAlarm = false
            var insert = false
            if (settings == null) {
                settings = SalatService.defaultBdSalatSettings()
                insert = true
            } else {
                downloadAndStartAlarm = settings!!.address != address.value!!
            }

            settings!!.address = address.value!!
            settings!!.salatAlert = convertBooleanToInt(salatAlert.value)
            settings!!.fajrAlertActive = convertBooleanToInt(fajrAlertActive.value)
            settings!!.dhuhrAlertActive = convertBooleanToInt(dhuhrAlertActive.value)
            settings!!.asrAlertActive = convertBooleanToInt(asrAlertActive.value)
            settings!!.maghribAlertActive = convertBooleanToInt(maghribAlertActive.value)
            settings!!.ishaAlertActive = convertBooleanToInt(ishaAlertActive.value)
            settings!!.fajrAlert = convertStringToInt(fajrAlert.value)
            settings!!.dhuhrAlert = convertStringToInt(dhuhrAlert.value)
            settings!!.asrAlert = convertStringToInt(asrAlert.value)
            settings!!.maghribAlert = convertStringToInt(maghribAlert.value)
            settings!!.ishaAlert = convertStringToInt(ishaAlert.value)

            viewModelScope.launch {
                if (insert) {
                    salatService.salatSettingsDAO.insert(settings!!)
                } else {
                    salatService.salatSettingsDAO.update(settings!!)
                }
                if (downloadAndStartAlarm) {
                    if (salatService.downloadAndUploadSalatTimes()) {
                        SalatAlarmUtils.startAlarmScheduler(salatService.context, true)
                    }
                } else {
                    SalatAlarmUtils.startAlarmScheduler(salatService.context, true)
                }
                navController.popBackStack()
            }
        }
    }
}