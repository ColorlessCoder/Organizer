package com.example.organizer.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.organizer.ui.Utils.DateUtils
import com.example.organizer.widget.SalatTimeAppWidget
import java.util.*


class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        PrefUtils.setAlarmSchedulerLastMessage(context, "Received: ${DateUtils.dateToString(Date())}")
        SalatTimeAppWidget.getRefreshWidgetPendingIntent(context)?.send()
        SalatAlarmUtils.setAlarmForNextSalat(context)
        if (intent.action === Intent.ACTION_BOOT_COMPLETED) {
            SalatAlarmUtils.startSchedulerAfterBoot(context)
        }
    }
}