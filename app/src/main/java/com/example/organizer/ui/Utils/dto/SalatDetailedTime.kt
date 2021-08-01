package com.example.organizer.ui.Utils.dto

import android.content.res.Resources
import com.example.organizer.R
import java.util.Date

class SalatDetailedTime {
    var salatTimeRange = mutableMapOf<Type, Pair<Date, Date>>()
    companion object{
        private var eventOrder = mutableListOf<Type>(Type.SAHARI,
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
        private var forozPrayers = mutableListOf<Type>(Type.FAJR,
            Type.DHUHR,
            Type.ASR,
            Type.MAGHRIB,
            Type.ISHA,)
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