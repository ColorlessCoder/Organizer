package com.example.organizer.utils

import android.content.Intent
import com.example.organizer.ui.Utils.dto.SalatDetailedTime

data class SalatAlertExtra(var type: SalatDetailedTime.Companion.Type, var remainTime: Int) {
    companion object {
        private const val typeString = "salatType"
        private const val remainTimeString = "remainTime"
        fun fromIntent(intent: Intent): SalatAlertExtra {
            val type = intent.getStringExtra(typeString)
            val remainTime = intent.getIntExtra(remainTimeString, 0)
            return SalatAlertExtra(
                type = if (type == null) SalatDetailedTime.Companion.Type.NONE else SalatDetailedTime.Companion.Type.fromName(type),
                remainTime = remainTime
            )
        }
    }
    fun populateIntent(intent: Intent) {
        intent.putExtra(typeString, type.name)
        intent.putExtra(remainTimeString, remainTime)
    }
}