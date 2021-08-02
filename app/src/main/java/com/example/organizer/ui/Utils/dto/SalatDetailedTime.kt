package com.example.organizer.ui.Utils.dto

import android.content.res.Resources
import com.example.organizer.R
import com.example.organizer.database.entity.SalatSettings
import com.example.organizer.database.entity.SalatTime
import com.example.organizer.ui.Utils.DateUtils
import java.util.*

class SalatDetailedTime(salatTime: SalatTime, salatSettings: SalatSettings) {
    var salatTimeRange = mutableMapOf<Type, Pair<Date?, Date?>>()

    init {
        val date = DateUtils.deserializeSalatDateString(salatTime.date)

        salatTimeRange[Type.SAHARI] = Pair(null, makeDate(date, salatTime.imsak, 0))
        salatTimeRange[Type.FAJR] = Pair(makeDate(date, salatTime.fajrStart, salatSettings.fajrSafety), makeDate(date, salatTime.sunrise, -1))
        salatTimeRange[Type.SUNRISE] = Pair(makeDate(date, salatTime.sunrise, 0), null)
        salatTimeRange[Type.FORBIDDEN_TIME_1] = Pair(makeDate(date, salatTime.sunrise, 0), makeDate(date, salatTime.sunrise, salatSettings.sunriseRedzone))
        salatTimeRange[Type.ISHRAQ] = Pair(makeDate(salatTimeRange[Type.FORBIDDEN_TIME_1]?.first, 1), makeDate(date, salatTime.dhuhrStart, -salatSettings.middayRedzone))
        salatTimeRange[Type.FORBIDDEN_TIME_2] = Pair(makeDate(date, salatTime.dhuhrStart, -salatSettings.middayRedzone), makeDate(date, salatTime.dhuhrStart, salatSettings.dhuhrSafety))
        salatTimeRange[Type.DHUHR] = Pair(makeDate(date, salatTime.dhuhrStart, salatSettings.dhuhrSafety), makeDate(date, salatTime.asrStart, 0))
        salatTimeRange[Type.ASR] = Pair( makeDate(date, salatTime.asrStart, salatSettings.asrSafety), makeDate(date, salatTime.sunset, -salatSettings.sunsetRedzone))
        salatTimeRange[Type.FORBIDDEN_TIME_3] = Pair( makeDate(date, salatTime.sunset, -salatSettings.sunsetRedzone), makeDate(date, salatTime.sunset, salatSettings.maghribSafety))
        salatTimeRange[Type.MAGHRIB] = Pair( makeDate(date, salatTime.sunset, salatSettings.maghribSafety), makeDate(date, salatTime.ishaStart, 0))
        salatTimeRange[Type.ISHA] = Pair( makeDate(date, salatTime.ishaStart, salatSettings.ishaSafety), makeDate(date, salatTime.midnight, 0))
        salatTimeRange[Type.TAHAJJUD] = Pair( makeDate(date, salatTime.midnight, 0), null)
    }

    private fun makeDate(date: Date, hourMinStr: String?, offset: Int): Date? {
        var newDate: Date? = null
        if(hourMinStr != null) {
            val hourMin = HourMin.valueOf(hourMinStr)
            val calendar = Calendar.getInstance()
            calendar.time = date
            calendar.set(Calendar.HOUR, hourMin.hour)
            calendar.set(Calendar.MINUTE, hourMin.hour)
            calendar.add(Calendar.MINUTE, offset)
            newDate = calendar.time
        }
        return newDate
    }
    private fun makeDate(date: Date?, offset: Int): Date? {
        var newDate: Date? = null
        if(date != null) {
            val calendar = Calendar.getInstance()
            calendar.time = date
            calendar.add(Calendar.MINUTE, offset)
            newDate = calendar.time
        }
        return newDate
    }

    companion object{
        private var eventOrder = mutableListOf(Type.SAHARI,
            Type.FAJR,
            Type.SUNRISE,
            Type.FORBIDDEN_TIME_1,
            Type.ISHRAQ,
            Type.FORBIDDEN_TIME_2,
            Type.DHUHR,
            Type.ASR,
            Type.FORBIDDEN_TIME_3,
            Type.SUNSET,
            Type.MAGHRIB,
            Type.ISHA,
            Type.TAHAJJUD)
        private var forozPrayers = mutableListOf(Type.FAJR,
            Type.DHUHR,
            Type.ASR,
            Type.MAGHRIB,
            Type.ISHA)
        enum class Type{
            SAHARI,
            FAJR,
            SUNRISE,
            FORBIDDEN_TIME_1,
            ISHRAQ,
            FORBIDDEN_TIME_2,
            DHUHR,
            ASR,
            FORBIDDEN_TIME_3,
            SUNSET,
            MAGHRIB,
            ISHA,
            TAHAJJUD
        }

        fun getSpecialMessage(type: Type): String {
            return when(type) {
                Type.FORBIDDEN_TIME_3 -> Resources.getSystem().getString(R.string.forbidden_time_3_message)
                Type.TAHAJJUD -> Resources.getSystem().getString(R.string.tahajjud_message)
                else -> ""
            }
        }

        fun getNextEvent(type: Type): Type {
            var index = eventOrder.indexOf(type)
            if(index == eventOrder.size - 1) index = -1
            return eventOrder[index + 1]
        }

        fun getNextPrayer(type: Type): Type {
            var index = forozPrayers.indexOf(type)
            if(index == forozPrayers.size - 1) index = -1
            return forozPrayers[index + 1]
        }
    }

}