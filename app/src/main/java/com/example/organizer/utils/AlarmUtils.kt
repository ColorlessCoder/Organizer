package com.example.organizer.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build

class AlarmUtils {
    companion object {
        private var alarmManager: AlarmManager? = null

        fun getInstance(context: Context): AlarmManager {
            if(alarmManager == null) {
                alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            }
            return alarmManager!!
        }

        fun setAlarm(context: Context, wakeUpAt: Long, pendingIntent: PendingIntent) {
            val alarmManager = getInstance(context)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    wakeUpAt,
                    pendingIntent
                )
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, wakeUpAt, pendingIntent);
            }
        }
    }
}