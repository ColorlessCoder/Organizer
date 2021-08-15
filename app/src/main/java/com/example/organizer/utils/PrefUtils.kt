package com.example.organizer.utils

import android.annotation.SuppressLint
import android.content.Context
import androidx.preference.PreferenceManager

class PrefUtils {
    companion object {
        private const val BASE = "com.example.organizer."
        private const val ALARM_SCHEDULER_ACTIVE = BASE + "al.sh.ac"
        fun isAlarmSchedulerActive(context: Context): Boolean {
            return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(ALARM_SCHEDULER_ACTIVE, false)
        }

        fun setAlarmSchedulerActive(context: Context, value: Boolean) {
            PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(ALARM_SCHEDULER_ACTIVE, value)
                .apply()
        }

        @SuppressLint("ApplySharedPref")
        fun setAlarmSchedulerActiveSync(context: Context, value: Boolean) {
            PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(ALARM_SCHEDULER_ACTIVE, value)
                .commit()
        }
    }
}