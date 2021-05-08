package com.example.organizer.ui.Utils

import kotlin.math.ceil
import kotlin.math.floor

class StringUtils {
    companion object {
        fun doubleToString(value: Double):String {
            return if (ceil(value) == floor(value)) value.toInt().toString() else value.toString()
        }
        fun isNumeric(a: Char): Boolean {
            return a in '0'..'9'
        }
    }
}