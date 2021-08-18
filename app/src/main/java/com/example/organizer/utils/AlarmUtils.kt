package com.example.organizer.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.organizer.database.AppDatabase
import com.example.organizer.database.services.SalatService
import com.example.organizer.ui.Utils.DateUtils
import com.example.organizer.widget.SalatTimeAppWidget
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class AlarmUtils {
    companion object {
        fun startAlarmScheduler(context: Context, force: Boolean = false) {
            if (force || !PrefUtils.isAlarmSchedulerActive(context)) {
                if(force) {
                    stopAlarmScheduler(context, force)
                }
                PrefUtils.setAlarmSchedulerActive(context, true)
                setAlarmForNextSalat(context)
                SalatTimeAppWidget.getRefreshWidgetPendingIntent(context)?.send()
            }
        }

        fun stopAlarmScheduler(context: Context, force: Boolean = false) {
            if (force || PrefUtils.isAlarmSchedulerActive(context)) {
                cancelAlarmForScheduler(context)
                PrefUtils.setAlarmSchedulerActive(context, false)
            }
        }

        fun setAlarmForScheduler(context: Context, nowMilliSeconds: Long, delayMs: Long) {
            val wakeUpAt = nowMilliSeconds + delayMs
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, AlarmSchedulerReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                IntentRequestCode.AlarmSchedulerIntent.requestCode,
                intent,
                0
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, wakeUpAt, pendingIntent)
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, wakeUpAt, pendingIntent);
            }
        }

        fun cancelAlarmForScheduler(context: Context) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, AlarmSchedulerReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                IntentRequestCode.AlarmSchedulerIntent.requestCode,
                intent,
                0
            )
            alarmManager.cancel(pendingIntent)
        }

        fun setAlarmForNextSalat(context: Context) {
            val db = AppDatabase.getInstance(context)
            val salatService = SalatService(db.salatTimesDao(), db.salatSettingsDao(), context)
            val coroutineScope = CoroutineScope(Dispatchers.Main.immediate)
            coroutineScope.launch {
                val now = Date()
                val nowMs = now.time
                val curNext = salatService.getCurrentAndNextEvent(true)
                val next = curNext.first.second
                var message = "Now: ${DateUtils.dateToStringSafe(now)}, Current Type: ${curNext.first.first.toString()}, Next Type: ${curNext.first.second.toString()}"
                if (next?.range?.first != null && nowMs < next.range.first!!.time) {
                    val delay = next.range.first!!.time - nowMs
                    PrefUtils.setAlarmSchedulerLastMessage(context,
                        "Scheduler is running. Next time will be ${DateUtils.dateToStringSafe(next.range.first)} after. $message"
                    )
                    setAlarmForScheduler(context, nowMs, delay)
                } else {
                    if(next?.range?.first != null) {
                        if (nowMs >= next.range.first!!.time) {
                           message = "(nowMs >= next.range.first!!.time) is true. $message"
                        }
                    }
                    PrefUtils.setAlarmSchedulerLastMessage(context,
                        "Scheduler is stopped. $message"
                    )
                    stopAlarmScheduler(context)
                }
            }
        }
    }
}