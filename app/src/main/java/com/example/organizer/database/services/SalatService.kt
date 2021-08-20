package com.example.organizer.database.services

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.organizer.database.dao.SalatSettingsDAO
import com.example.organizer.database.dao.SalatTimesDAO
import com.example.organizer.database.entity.SalatSettings
import com.example.organizer.database.entity.SalatTime
import com.example.organizer.database.enums.DbBoolValue
import com.example.organizer.ui.Utils.DateUtils
import com.example.organizer.ui.Utils.dto.HourMin
import com.example.organizer.ui.Utils.dto.SalatDetailedTime
import com.google.gson.Gson
import com.google.gson.internal.LinkedTreeMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class SalatService(
    private val salatTimeDao: SalatTimesDAO,
    val salatSettingsDAO: SalatSettingsDAO,
    val context: Context
) {
    companion object {
        val visitedSet = mutableSetOf<String>()
        fun defaultBdSalatSettings(): SalatSettings {
            return SalatSettings(
                id = UUID.randomUUID().toString(),
                settingsName = SalatSettingsDAO.DefaultSettingsName,
                address = "Khilgoan, Dhaka, Bangladesh",
                active = DbBoolValue.TRUE.value,
                salatAlert = DbBoolValue.FALSE.value,
                fajrAlert = DbBoolValue.FALSE.value,
                dhuhrAlert = DbBoolValue.FALSE.value,
                asrAlert = DbBoolValue.FALSE.value,
                maghribAlert = DbBoolValue.FALSE.value,
                ishaAlert = DbBoolValue.FALSE.value,
                fajrSafety = 3,
                dhuhrSafety = 3,
                asrSafety = 0,
                maghribSafety = 3,
                ishaSafety = 0,
                sunriseRedzone = 24,
                middayRedzone = 3,
                sunsetRedzone = 24
            )
        }

        fun getSalatAlertTime(setting: SalatSettings, type: SalatDetailedTime.Companion.Type): Int {
            if(setting.salatAlert == 1) {
                return when(type) {
                    SalatDetailedTime.Companion.Type.ISHA -> if (setting.ishaAlertActive == 1) setting.ishaAlert else 0
                    SalatDetailedTime.Companion.Type.PREVIOUS_ISHA -> if (setting.ishaAlertActive == 1) setting.ishaAlert else 0
                    SalatDetailedTime.Companion.Type.FAJR -> if (setting.fajrAlertActive == 1) setting.fajrAlert else 0
                    SalatDetailedTime.Companion.Type.DHUHR -> if (setting.dhuhrAlertActive == 1) setting.dhuhrAlert else 0
                    SalatDetailedTime.Companion.Type.ASR -> if (setting.asrAlertActive == 1) setting.asrAlert else 0
                    SalatDetailedTime.Companion.Type.MAGHRIB -> if (setting.maghribAlertActive == 1) setting.maghribAlert else 0
                    else -> 0
                }
            }
            return 0
        }
    }

    suspend fun getCurrentAndNextEvent(validNext: Boolean = false): Pair<Pair<SalatDetailedTime.Companion.Event?, SalatDetailedTime.Companion.Event?>, String> {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_MONTH, -1)
        val setting = salatSettingsDAO.getActiveSalatSettings()
        if(setting.address != null) {
            val address = setting.address!!
            val yesterday = salatTimeDao.getByDateAddress(
                DateUtils.serializeSalatDate(cal.time),
                address
            )
            cal.add(Calendar.DAY_OF_MONTH, 1)
            val today = salatTimeDao.getByDateAddress(
                DateUtils.serializeSalatDate(cal.time),
                address
            )
            if (yesterday != null && today != null) {
                val todayDetailedTime = SalatDetailedTime(today, setting, yesterday)
                var curNext = SalatDetailedTime.getCurrentEventAndPopulate(todayDetailedTime, Pair(null, null), true, validNext)
                if(curNext.first == null || curNext.second == null) {
                    cal.add(Calendar.DAY_OF_MONTH, 1)
                    val tomorrow = salatTimeDao.getByDateAddress(
                        DateUtils.serializeSalatDate(cal.time),
                        address
                    )
                    if(tomorrow != null) {
                        val tomorrowDetailedTime = SalatDetailedTime(tomorrow, setting, today)
                        curNext = SalatDetailedTime.getCurrentEventAndPopulate(
                            tomorrowDetailedTime,
                            curNext,
                            false,
                            validNext
                        )
                    } else {
                        throw Exception("Please consider download salat times data first. Go to Backup menu.")
                    }
                }
                return Pair(curNext, address)
            } else {
                throw Exception("Please consider download salat times data first. Go to Backup menu.")
            }
        } else {
            throw Exception("Please set an address in Prayer Settings")
        }
    }

    suspend fun getConsecutiveSalatTimes(
        startDate: Date,
        numberOfDays: Int
    ): List<SalatDetailedTime>? {
        val salatSettings: SalatSettings = salatSettingsDAO.getActiveSalatSettings()
        val cal: Calendar = Calendar.getInstance()
        cal.time = startDate
        cal.add(Calendar.DAY_OF_MONTH, -1)
        val salatTimes = mutableListOf<SalatTime>()
        var failed = false
        val response = mutableListOf<SalatDetailedTime>()
        for (i in 0..numberOfDays) {
            val salatTime = getSalatTimeForDate(cal, salatSettings)
            if (salatTime == null) {
                failed = true
                break
            }
            salatTimes.add(salatTime)
            if(i > 0) {
                response.add(SalatDetailedTime(salatTime, salatSettings, salatTimes[i - 1]))
            }
            cal.add(Calendar.DAY_OF_MONTH, 1)
        }

        if (failed) {
            return null
        }
        return response
    }

    suspend fun getSalatTimeForDate(
        cal: Calendar,
        salatSettings: SalatSettings
    ): SalatTime? {
        var response: SalatTime? = null
        if (!salatSettings.address.isNullOrEmpty()) {
            val address = salatSettings.address!!
            val salatTime: SalatTime? = salatTimeDao.getByDateAddress(
                DateUtils.serializeSalatDate(cal.time),
                address
            )
            if (salatTime == null) {
                throw Exception("Sorry, no data found")
            } else {
                response = salatTime
            }
        }
        return response
    }

    suspend fun downloadAndUploadSalatTimes(): Boolean {
        val cal = Calendar.getInstance()
        var result = true
        try {
            val salatSettings: SalatSettings = salatSettingsDAO.getActiveSalatSettings()
            cal.add(Calendar.DAY_OF_MONTH, -1)
            downloadAndUploadTimeForMonthIfNotExists(cal, salatSettings.address!!)
            cal.add(Calendar.DAY_OF_MONTH, 1)
            downloadAndUploadTimeForMonthIfNotExists(cal, salatSettings.address!!)
            cal.add(Calendar.MONTH, 1)
            downloadAndUploadTimeForMonthIfNotExists(cal, salatSettings.address!!)
        } catch (ex: java.lang.Exception) {
            result = false
        }
        return result
    }

    private suspend fun downloadAndUploadTimeForMonthIfNotExists(cal: Calendar, address: String, ) {
        val salatTime = salatTimeDao.getByDateAddress(DateUtils.serializeSalatDate(cal.time), address)
        if(salatTime == null) {
            val salatTimes = downloadSalatTimes(
                cal.get(Calendar.MONTH),
                cal.get(Calendar.YEAR),
                address
            )
            uploadAllSalatTime(salatTimes, address)
        }
    }

    suspend fun uploadAllSalatTime(salatTimes: List<SalatTime>, address: String) {
        salatTimes.forEach {
            val key = it.date + '#' +  it.address
            if(!visitedSet.contains(key)) {
                val salatTime = salatTimeDao.getByDateAddress(it.date, it.address)
                if (salatTime == null) {
                    println(it.date + it.address + this)
                    salatTimeDao.insert(it)
                } else {
                    it.id = salatTime.id
                    salatTimeDao.update(it)
                }
                visitedSet.add(key)
            }
        }
    }

    suspend fun downloadSalatTimes(
        month: Int,
        year: Int,
        address: String
    ) = suspendCoroutine<List<SalatTime>> { cod ->
        val queue = Volley.newRequestQueue(context)
        val url =
            "https://api.aladhan.com/v1/calendarByAddress?address=$address&method=1&month=${month + 1}&year=$year&school=1"
        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
                val salatTimes = convertSalatTimesApiResponseToSalatTimes(response, address)
                cod.resume(salatTimes)
            },
            {
                it.printStackTrace()
                cod.resumeWithException(Exception("Error occurred while sending request."))
            })

        queue.add(stringRequest)
    }

    private fun convertSalatTimesApiResponseToSalatTimes(
        response: String,
        address: String
    ): List<SalatTime> {
        val salatTimes = mutableListOf<SalatTime>()
        var map: Map<String, Any> = HashMap()
        map = Gson()
            .fromJson(
                response,
                map.javaClass
            ) as Map<String, Any>
        val data: List<LinkedTreeMap<String, Any>> =
            map["data"] as ArrayList<LinkedTreeMap<String, Any>>
        data.forEach {
            val day: LinkedTreeMap<String, Any> = it
            val date: LinkedTreeMap<String, Any> =
                day["date"] as LinkedTreeMap<String, Any>
            val dateString = date["readable"].toString()
            val timings: LinkedTreeMap<String, Any> =
                day["timings"] as LinkedTreeMap<String, Any>
            val fajr = HourMin.valueOf(timings["Fajr"].toString())
            val sunrise = HourMin.valueOf(timings["Sunrise"].toString())
            val dhuhr = HourMin.valueOf(timings["Dhuhr"].toString())
            val asr = HourMin.valueOf(timings["Asr"].toString())
            val sunset = HourMin.valueOf(timings["Sunset"].toString())
            val maghrib = HourMin.valueOf(timings["Maghrib"].toString())
            val isha = HourMin.valueOf(timings["Isha"].toString())
            val imsak = HourMin.valueOf(timings["Imsak"].toString())
            val midnight = HourMin.valueOf(timings["Midnight"].toString())
            val salatTime = SalatTime(
                UUID.randomUUID().toString(),
                date = dateString,
                address = address,
                fajrStart = fajr.toString(),
                sunrise = sunrise.toString(),
                dhuhrStart = dhuhr.toString(),
                asrStart = asr.toString(),
                sunset = sunset.toString(),
                maghribStart = maghrib.toString(),
                ishaStart = isha.toString(),
                imsak = imsak.toString(),
                midnight = midnight.toString()
            )
            salatTimes.add(salatTime)
        }
        return salatTimes
    }
}