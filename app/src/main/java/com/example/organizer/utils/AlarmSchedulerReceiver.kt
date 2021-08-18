package com.example.organizer.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.organizer.ui.Utils.DateUtils
import com.example.organizer.widget.SalatTimeAppWidget
import java.util.*

class AlarmSchedulerReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        PrefUtils.setAlarmSchedulerLastMessage(context, "Received: ${DateUtils.dateToString(Date())}")
        SalatTimeAppWidget.getRefreshWidgetPendingIntent(context)?.send()
        AlarmUtils.setAlarmForNextSalat(context)
    }
}