package com.example.organizer.utils

import android.annotation.TargetApi
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import com.example.organizer.R
import com.example.organizer.ui.Utils.DateUtils

class NotificationUtils {
    companion object {
        private var notificationManager: NotificationManager? = null

        private const val CHANNEL_ID_SALAT_ALERT = "salat_alert"
        private const val CHANNEL_NAME_SALAT_ALERT = "Salat Alert"

        private const val TIMER_ID = 0

        fun getInstance(context: Context): NotificationManager {
            if (notificationManager == null) {
                notificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            }
            return notificationManager!!
        }

        fun showSalatAlert(context: Context, extra: SalatAlertExtra) {
            val nBuilder = getBasicNotificationBuilder(context, CHANNEL_ID_SALAT_ALERT, true)
            val eventName = context.getString(extra.type.labelKey)
            val message =
                if (extra.remainTime > 0) "Waqt for $eventName started ${extra.remainTime} minutes ago."
                else "Waqt for $eventName will end soon. Only ${extra.remainTime} minutes remain."
            nBuilder.setContentTitle("Salat Alert!")
                .setContentText(message)

            val nManager = getInstance(context)
            nManager.createNotificationChannel(
                CHANNEL_ID_SALAT_ALERT,
                CHANNEL_NAME_SALAT_ALERT,
                true
            )
            nManager.notify(TIMER_ID, nBuilder.build())
        }

        private fun getBasicNotificationBuilder(
            context: Context,
            channelId: String,
            playSound: Boolean
        )
                : NotificationCompat.Builder {
            val notificationSound: Uri =
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val nBuilder = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_muslim_man_praying)
                .setAutoCancel(true)
                .setDefaults(0)
            if (playSound) nBuilder.setSound(notificationSound)
            return nBuilder
        }

        private fun <T> getPendingIntentWithStack(
            context: Context,
            javaClass: Class<T>
        ): PendingIntent? {
            val resultIntent = Intent(context, javaClass)
            resultIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP

            val stackBuilder = TaskStackBuilder.create(context)
            stackBuilder.addParentStack(javaClass)
            stackBuilder.addNextIntent(resultIntent)

            return stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        @TargetApi(26)
        private fun NotificationManager.createNotificationChannel(
            channelID: String,
            channelName: String,
            playSound: Boolean
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channelImportance = if (playSound) NotificationManager.IMPORTANCE_DEFAULT
                else NotificationManager.IMPORTANCE_LOW
                val nChannel = NotificationChannel(channelID, channelName, channelImportance)
                nChannel.enableLights(true)
                nChannel.lightColor = Color.BLUE
                this.createNotificationChannel(nChannel)
            }
        }
    }
}