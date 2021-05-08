package com.example.organizer.ui.Utils.dto

import com.example.organizer.ui.Utils.StringUtils
import java.lang.Exception

class HourMin() {
    var hour: Int = 0
    var min: Int = 0
    private var increment = 0

    companion object {
        fun valueOf(str: String): HourMin {
            if (validateString(str)) {
                val obj = HourMin()
                val hourString = str.substring(0, 2)
                obj.hour = hourString.toInt()
                val minString = str.substring(3, 5)
                obj.min = minString.toInt()
                if (obj.hour >= 24) {
                    throw Exception("Hour cannot be greater than 23")
                } else if (obj.min >= 60) {
                    throw Exception("Minute cannot be greater than 59")
                } else {
                    return obj
                }
            } else {
                throw Exception("Must be of patter hh:mm")
            }
        }

        private fun validateString(str: String): Boolean {
            return str.length >= 5 && StringUtils.isNumeric(str[0])
                    && StringUtils.isNumeric(str[1])
                    && str[2] == ':'
                    && StringUtils.isNumeric(str[3])
                    && StringUtils.isNumeric(str[4])
        }
    }

    fun addToSelf(minutes: Int): HourMin {
        min += minutes
        if(min >= 60 || min < 0) {
            hour += (min / 60) - if(min<0 && min%60 != 0) 1 else 0
            min %= 60
            if(min <0) {
                min += 60
            }
        }
        if(hour >= 24 || hour<0) {
            increment += hour/24 - if(hour<0 && hour%24 != 0) 1 else 0
            hour %= 24
            if(hour <0) {
                hour += 24
            }
        }
        return this
    }

    fun add(minutes: Int): HourMin {
        return this.clone().addToSelf(minutes)
    }

    fun clone(): HourMin {
        val obj = HourMin()
        obj.hour = hour
        obj.min = min
        return obj
    }

    override fun toString(): String {
        return (if(hour<10) "0" else "") + hour + ":" + (if(min<10) "0" else "") + min
    }

    fun getIncrement(): Int {
        return increment
    }

}