package com.example.organizer.database.services

import android.content.Context
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.organizer.database.dao.SalatTimesDAO
import com.example.organizer.database.entity.SalatTime
import com.example.organizer.ui.Utils.dto.HourMin
import com.google.gson.Gson
import com.google.gson.internal.LinkedTreeMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class SalatService(val salatTimeDao: SalatTimesDAO) {

    fun downloadSalatTimes(
        month: Int,
        year: Int,
        city: String,
        country: String,
        context: Context,
        lifeCycleScope: CoroutineScope,
        overwrite: Boolean
    ) {
        val queue = Volley.newRequestQueue(context)
        val url =
            "https://api.aladhan.com/v1/calendarByCity?city=$city&country=$country&method=1&month=$month&year=$year&school=1"
        val stringRequest = StringRequest(
            Request.Method.GET, url,
            Response.Listener<String> { response ->
                val salatTimes = convertSalatTimesApiResponseToSalatTimes(response, city, country)
                lifeCycleScope.launch {
                    salatTimes.forEach {
                        val salatTime = salatTimeDao.getByDateCountryCity(it.date, country, city)
                        if (salatTime == null) {
                            salatTimeDao.insert(it)
                        } else if (overwrite) {
                            it.id = salatTime.id
                            salatTimeDao.update(it)
                        }
                    }
                }
            },
            Response.ErrorListener {
                it.printStackTrace()
                throw Exception("Error occurred while sending request.")
            })

        queue.add(stringRequest)
    }

    private fun convertSalatTimesApiResponseToSalatTimes(
        response: String,
        city: String,
        country: String
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
                city = city,
                country = country,
                tahajjudStart = midnight.add(1).toString(),
                tahajjudEnd = fajr.toString(),
                fajrStart = fajr.toString(),
                fajrEnd = sunrise.add(-5).toString(),
                sunrise = sunrise.toString(),
                firstRestrictionStart = sunrise.toString(),
                firstRestrictionEnd = sunrise.add(20).toString(),
                ishraqStart = sunrise.add(20).toString(),
                ishraqEnd = sunrise.add(140).toString(),
                midday = dhuhr.add(-15).toString(),
                secondRestrictionStart = dhuhr.add(-15).toString(),
                secondRestrictionEnd = dhuhr.toString(),
                dhuhrStart = dhuhr.toString(),
                dhuhrEnd = asr.toString(),
                asrStart = asr.toString(),
                asrEnd = sunset.add(-15).toString(),
                sunset = sunset.toString(),
                thirdRestrictionStart = sunset.add(-15).toString(),
                thirdRestrictionEnd = sunset.toString(),
                maghribStart = maghrib.toString(),
                maghribEnd = maghrib.add(20).toString(),
                ishaStart = isha.toString(),
                ishaEnd = midnight.toString(),
                imsak = imsak.toString()
            )
            salatTimes.add(salatTime)
        }
        return salatTimes
    }
}