package com.example.organizer.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.organizer.widget.SalatTimeAppWidget

class AlarmSchedulerReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        SalatTimeAppWidget.getRefreshWidgetPendingIntent(context)?.send()
        AlarmUtils.setAlarmForNextSalat(context)
    }
}