package com.example.organizer.ui.Utils.dto

import android.content.res.Resources
import com.example.organizer.R
import com.example.organizer.database.entity.SalatSettings
import com.example.organizer.database.entity.SalatTime
import com.example.organizer.ui.Utils.DateUtils
import java.util.*

class SalatDetailedTime(
    val salatTime: SalatTime,
    salatSettings: SalatSettings,
    salatTimeForPreviousDay: SalatTime
) {
    var salatTimeRange = mutableMapOf<Type, Pair<Date?, Date?>>()
    var orderedEventList = mutableListOf<Event>()

    init {
        val date = DateUtils.deserializeSalatDateString(salatTime.date)

        salatTimeRange[Type.SAHARI] = Pair(null, makeDate(date, salatTime.imsak, 0))
        salatTimeRange[Type.FAJR] = DateUtils.createDateRange(
            makeDate(date, salatTime.fajrStart, salatSettings.fajrSafety),
            makeDate(date, salatTime.sunrise, 0)
        )
        salatTimeRange[Type.SUNRISE] = DateUtils.createDateRange(makeDate(date, salatTime.sunrise, 0), null)
        salatTimeRange[Type.FORBIDDEN_TIME_1] = DateUtils.createDateRange(
            makeDate(date, salatTime.sunrise, 0),
            makeDate(date, salatTime.sunrise, salatSettings.sunriseRedzone)
        )
        salatTimeRange[Type.ISHRAQ] = DateUtils.createDateRange(
            makeDate(salatTimeRange[Type.FORBIDDEN_TIME_1]?.first, 1),
            makeDate(date, salatTime.dhuhrStart, -salatSettings.middayRedzone)
        )
        salatTimeRange[Type.FORBIDDEN_TIME_2] = DateUtils.createDateRange(
            makeDate(date, salatTime.dhuhrStart, -salatSettings.middayRedzone),
            makeDate(date, salatTime.dhuhrStart, salatSettings.dhuhrSafety)
        )
        salatTimeRange[Type.DHUHR] = DateUtils.createDateRange(
            makeDate(date, salatTime.dhuhrStart, salatSettings.dhuhrSafety),
            makeDate(date, salatTime.asrStart, 0)
        )
        salatTimeRange[Type.ASR] = DateUtils.createDateRange(
            makeDate(date, salatTime.asrStart, salatSettings.asrSafety),
            makeDate(date, salatTime.sunset, -salatSettings.sunsetRedzone)
        )
        salatTimeRange[Type.FORBIDDEN_TIME_3] = DateUtils.createDateRange(
            makeDate(date, salatTime.sunset, -salatSettings.sunsetRedzone),
            makeDate(date, salatTime.sunset, salatSettings.maghribSafety)
        )
        salatTimeRange[Type.MAGHRIB] = DateUtils.createDateRange(
            makeDate(date, salatTime.sunset, salatSettings.maghribSafety),
            makeDate(date, salatTime.ishaStart, 0)
        )
        salatTimeRange[Type.ISHA] = DateUtils.createDateRange(
            makeDate(date, salatTime.ishaStart, salatSettings.ishaSafety),
            makeDate(date, salatTime.midnight, 0)
        )
        salatTimeRange[Type.PREVIOUS_ISHA] = DateUtils.createDateRange(
            makeDate(date, salatTimeForPreviousDay.ishaStart, salatSettings.ishaSafety),
            makeDate(date, salatTimeForPreviousDay.midnight, 0)
        )

        val midnight = salatTimeForPreviousDay.midnight
        salatTimeRange[Type.TAHAJJUD] = DateUtils.createDateRange(
            makeDate(date, midnight, 0),
            makeDate(date, salatTime.fajrStart, salatSettings.fajrSafety),
            true
        )
        orderedEventList = getAllEvents()
    }

    private fun makeDate(date: Date, hourMinStr: String?, offset: Int): Date? {
        var newDate: Date? = null
        if (hourMinStr != null) {
            val hourMin = HourMin.valueOf(hourMinStr)
            val calendar = Calendar.getInstance()
            calendar.time = date
            calendar.set(Calendar.HOUR, hourMin.hour)
            calendar.set(Calendar.MINUTE, hourMin.min)
            calendar.add(Calendar.MINUTE, offset)
            newDate = calendar.time
        }
        return newDate
    }

    private fun makeDate(date: Date?, offset: Int): Date? {
        var newDate: Date? = null
        if (date != null) {
            val calendar = Calendar.getInstance()
            calendar.time = date
            calendar.add(Calendar.MINUTE, offset)
            newDate = calendar.time
        }
        return newDate
    }

    companion object {
        private var eventOrder = mutableListOf(
            Type.PREVIOUS_ISHA,
            Type.TAHAJJUD,
            Type.SAHARI,
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
        )

        public val redZones = mutableListOf(
            Type.FORBIDDEN_TIME_1,
            Type.FORBIDDEN_TIME_2,
            Type.FORBIDDEN_TIME_3,
        )
        private var fardPrayers = mutableListOf(
            Type.FAJR,
            Type.DHUHR,
            Type.ASR,
            Type.MAGHRIB,
            Type.ISHA
        )

        enum class Type(val labelKey: Int) {
            PREVIOUS_ISHA(R.string.isha_previous_day),
            SAHARI(R.string.sahari_end),
            FAJR(R.string.fajr),
            SUNRISE(R.string.sunrise),
            FORBIDDEN_TIME_1(R.string.forbidden_time_1),
            ISHRAQ(R.string.ishraq),
            FORBIDDEN_TIME_2(R.string.forbidden_time_2),
            DHUHR(R.string.dhuhr),
            ASR(R.string.asr),
            FORBIDDEN_TIME_3(R.string.forbidden_time_3),
            SUNSET(R.string.sunset),
            MAGHRIB(R.string.maghrib),
            ISHA(R.string.isha),
            TAHAJJUD(R.string.tahajjud)
        }

        enum class Status(val labelKey: Int) {
            ONGOING(R.string.ongoing),
            IGNORE(0),
            NEXT(R.string.up_next);
        }

        fun getSpecialMessage(type: Type): Int? {
            return when (type) {
                Type.FORBIDDEN_TIME_3 -> R.string.forbidden_time_3_message
                Type.TAHAJJUD -> R.string.tahajjud_message
                else -> null
            }
        }

        fun getNextEvent(salatDetailedTime: SalatDetailedTime, event: Event): Event? {
            var index = eventOrder.indexOf(event.type)
            if (index == eventOrder.size - 1) return null
            return salatDetailedTime.orderedEventList[index + 1]
        }

        fun getNextPrayer(type: Type): Type {
            var index = fardPrayers.indexOf(type)
            if (index == fardPrayers.size - 1) index = -1
            return fardPrayers[index + 1]
        }

        data class Event(val date: String, val type: Type, val range: Pair<Date?, Date?>, var status: Status?)

        fun getCurrentEventAndPopulate(salatDetailedTime: SalatDetailedTime?, curNext: Pair<Event?, Event?>): Pair<Event?, Event?> {
            return getCurrentEventAndPopulate(salatDetailedTime, curNext, false)
        }
        fun getCurrentEventAndPopulate(salatDetailedTime: SalatDetailedTime?, curNext: Pair<Event?, Event?>, includePreviousDay: Boolean): Pair<Event?, Event?> {
            if(salatDetailedTime == null) {
                return curNext
            }
            val now = Date()
            var current: Event? = curNext.first
            var next: Event? = curNext.second
            salatDetailedTime.orderedEventList.forEach {
                if(includePreviousDay || it.type != Type.PREVIOUS_ISHA) {
                    if (current != null && next == null) {
                        next = it
                        it.status = Status.NEXT
                    } else if (current == null && DateUtils.dateInRange(now, it.range, false)) {
                        current = it
                        it.status = Status.ONGOING
                    }
                }
            }
            return Pair(current, next)
        }

        fun getCurrentEventAndPopulate(map: Map<String, SalatDetailedTime>): Pair<Event?, Event?> {
            val cal = Calendar.getInstance()
            val nowDateString = DateUtils.serializeSalatDate(cal.time)
            var curNext = getCurrentEventAndPopulate(map[nowDateString], Pair(null, null), true)
            if(curNext.second == null) {
                cal.add(Calendar.DAY_OF_MONTH, 1)
                val tomorrowDateString = DateUtils.serializeSalatDate(cal.time)
                curNext = getCurrentEventAndPopulate(map[tomorrowDateString], curNext)
            }
            return curNext
        }
    }

    fun getAllEvents(): MutableList<Event> {
        val eventList = mutableListOf<Event>()
        eventOrder.forEach {
            if(salatTimeRange[it] != null) {
                val range: Pair<Date?, Date?> = salatTimeRange[it]!!
                eventList.add(Event(salatTime.date, it, range, if(it == Type.PREVIOUS_ISHA) Status.IGNORE else null))
            }
        }

        return eventList
    }

}