package com.example.organizer.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.organizer.ui.Utils.DateUtils
import com.example.organizer.ui.Utils.dto.SalatDetailedTime
import com.example.organizer.widget.SalatTimeAppWidget
import java.util.*

class SalatAlertReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val extra = SalatAlertExtra.fromIntent(intent)
        PrefUtils.setAlarmSchedulerLastMessage(
            context,
            "${extra.type.name} Salat Alert received. Diff time: ${extra.remainTime}."
        )
        if(extra.type != SalatDetailedTime.Companion.Type.NONE) {
            NotificationUtils.showSalatAlert(context, extra)
            SalatTimeAppWidget.getRefreshWidgetPendingIntent(context, true)?.send()
        }
    }
}