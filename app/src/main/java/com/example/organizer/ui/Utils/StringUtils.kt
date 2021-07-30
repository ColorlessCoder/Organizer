package com.example.organizer.ui.Utils

import java.text.DecimalFormat
import java.text.NumberFormat
import kotlin.math.ceil
import kotlin.math.floor

class StringUtils {
    companion object {
        fun doubleToString(value: Double?):String {
            if(value == null) return ""
            val format = DecimalFormat("#,##,###.###")
            return format.format(value)
        }
        fun isNumeric(a: Char): Boolean {
            return a in '0'..'9'
        }
    }
}