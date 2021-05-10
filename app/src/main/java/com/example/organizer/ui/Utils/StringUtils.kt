package com.example.organizer.ui.Utils

import kotlin.math.ceil
import kotlin.math.floor

class StringUtils {
    companion object {
        fun doubleToString(value: Double?):String {
            if(value == null) return ""
            return if (ceil(value) == floor(value)) value.toInt().toString() else value.toString()
        }
    }
}