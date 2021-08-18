package com.example.organizer.utils

import android.annotation.SuppressLint
import android.content.Context
import androidx.preference.PreferenceManager

class PrefUtils {
    companion object {
        private const val MSG_LIMIT = 10
        private const val BASE = "com.example.organizer."
        private const val ALARM_SCHEDULER_ACTIVE = BASE + "al.sh.ac"
        private const val ALARM_SCHEDULER_LAST_MESSAGE = BASE + "al.sh.l.msg"
        private const val ALARM_SCHEDULER_NEXT_INDEX = BASE + "al.sh.l.ix"
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

        private fun getString(context: Context, key: String, default: String): String {
            val value = PreferenceManager.getDefaultSharedPreferences(context)
                .getString(key, default)
            return value ?: default
        }

        private fun setString(context: Context, key: String, value: String) {
            PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(key, value)
                .apply()
        }

        private fun getInt(context: Context, key: String, default: Int): Int {
            return PreferenceManager.getDefaultSharedPreferences(context)
                .getInt(key, default)
        }

        private fun setInt(context: Context, key: String, value: Int) {
            PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putInt(key, value)
                .apply()
        }

        fun getAlarmSchedulerLastMessage(context: Context): List<String> {
            var index = getInt(context, ALARM_SCHEDULER_NEXT_INDEX, 0)
            val list = mutableListOf<String>()
            for (i in 1..10) {
                index--
                if (index == -1) {
                   index = 9
                }
                list.add(getString(context, ALARM_SCHEDULER_LAST_MESSAGE + index, "No Message"))
            }
            return list
        }

        fun setAlarmSchedulerLastMessage(context: Context, value: String) {
            val index = getInt(context, ALARM_SCHEDULER_NEXT_INDEX, 0)
            setString(context, ALARM_SCHEDULER_LAST_MESSAGE + index, value)
            setInt(context, ALARM_SCHEDULER_NEXT_INDEX, (index + 1) % 10)
        }
    }
}