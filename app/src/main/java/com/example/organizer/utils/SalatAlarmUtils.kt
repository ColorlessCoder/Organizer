package com.example.organizer.utils

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.organizer.database.AppDatabase
import com.example.organizer.database.dto.OrganizerException
import com.example.organizer.database.entity.SalatSettings
import com.example.organizer.database.services.SalatService
import com.example.organizer.ui.Utils.DateUtils
import com.example.organizer.ui.Utils.dto.SalatDetailedTime
import com.example.organizer.widget.SalatTimeAppWidget
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class SalatAlarmUtils {
    companion object {
        fun startSchedulerAfterBoot(context: Context) {
            if (PrefUtils.isAlarmSchedulerActive(context)) {
                PrefUtils.setAlarmSchedulerLastMessage(
                    context,
                    "Starting scheduler after boot."
                )
                startAlarmScheduler(context, true)
            }
        }

        fun startAlarmScheduler(context: Context, force: Boolean = false) {
            if (force || !PrefUtils.isAlarmSchedulerActive(context)) {
                if (force) {
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
            setAlarmForScheduler(context, nowMilliSeconds + delayMs)
        }

        fun setAlarmForScheduler(context: Context, wakeUpAt: Long) {
            AlarmUtils.setAlarm(context, wakeUpAt, getPendingIntentForRefresh(context))
        }

        fun getPendingIntentForRefresh(context: Context): PendingIntent {
            val intent = Intent(context, AlarmSchedulerReceiver::class.java)
            return PendingIntent.getBroadcast(
                context,
                IntentRequestCode.AlarmSchedulerIntent.requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }

        fun getPendingIntentForAlert(
            context: Context,
            extra: SalatAlertExtra? = null
        ): PendingIntent {
            val intent = Intent(context, SalatAlertReceiver::class.java)
            extra?.populateIntent(intent)
            return PendingIntent.getBroadcast(
                context,
                IntentRequestCode.AlarmSchedulerIntent.requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }

        fun cancelAlarmForScheduler(context: Context) {
            val alarmManager = AlarmUtils.getInstance(context)
            alarmManager.cancel(getPendingIntentForRefresh(context))
            cancelScheduledAlert(context)
        }

        fun cancelScheduledAlert(context: Context) {
            val alarmManager = AlarmUtils.getInstance(context)
            alarmManager.cancel(getPendingIntentForAlert(context))
            PrefUtils.setAlarmSchedulerLastMessage(
                context,
                "Salat Alert Cancelled."
            )
        }

        fun resetAlertForCurrentSalat(
            context: Context,
            current: SalatDetailedTime.Companion.Event,
            salatSettings: SalatSettings
        ) {
            cancelScheduledAlert(context)
            setAlertForCurrentSalat(context, current, salatSettings)
        }

        fun setAlertForCurrentSalat(
            context: Context,
            current: SalatDetailedTime.Companion.Event,
            salatSettings: SalatSettings
        ) {
            if (current.range.first != null && current.range.second != null) {
                val diff = SalatService.getSalatAlertTime(salatSettings, current.type)
                val time: Calendar = Calendar.getInstance()
                if (diff != 0) {
                    if (diff > 0) {
                        time.time = current.range.first!!
                    } else {
                        time.time = current.range.second!!
                    }
                    time.add(Calendar.MINUTE, diff)
                    if (time.time.after(Date())) {
                        AlarmUtils.setAlarm(
                            context, time.timeInMillis,
                            getPendingIntentForAlert(context, SalatAlertExtra(current.type, diff))
                        )
                        PrefUtils.setAlarmSchedulerLastMessage(
                            context,
                            "${current.type.name} Salat Alert set for ${DateUtils.dateToString(time.time)}."
                        )
                    }
                }
            }
        }

        fun setAlarmForNextSalat(context: Context) {
            val db = AppDatabase.getInstance(context)
            val salatService = SalatService(db.salatTimesDao(), db.salatSettingsDao(), context)
            val coroutineScope = CoroutineScope(Dispatchers.Main.immediate)
            coroutineScope.launch {
                val now = Date()
                val nowMs = now.time
                try {
                    val curNext = salatService.getCurrentAndNextEvent(true)
                    val next = curNext.first.second
                    var message =
                        "Now: ${DateUtils.dateToStringSafe(now)}, Current Type: ${curNext.first.first.toString()}, Next Type: ${curNext.first.second.toString()}"
                    if (next?.range?.first != null && nowMs < next.range.first!!.time) {
                        val delay = next.range.first!!.time - nowMs
                        PrefUtils.setAlarmSchedulerLastMessage(
                            context,
                            "Scheduler is running. Next time will be ${
                                DateUtils.dateToStringSafe(
                                    next.range.first
                                )
                            } after. $message"
                        )
                        setAlarmForScheduler(context, nowMs, delay)
                        if (curNext.first.first != null) {
                            val settings = salatService.getActiveSalatSettings()
                            resetAlertForCurrentSalat(context, curNext.first.first!!, settings)
                        }
                    } else {
                        if (next?.range?.first != null) {
                            if (nowMs >= next.range.first!!.time) {
                                message = "(nowMs >= next.range.first!!.time) is true. $message"
                            }
                        }
                        PrefUtils.setAlarmSchedulerLastMessage(
                            context,
                            "Scheduler is stopped. $message"
                        )
                        stopAlarmScheduler(context)
                    }
                } catch (ex: OrganizerException) {
                    // TODO: wait and restart
                }
            }
        }
    }
}