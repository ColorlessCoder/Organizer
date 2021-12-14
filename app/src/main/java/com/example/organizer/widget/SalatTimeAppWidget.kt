package com.example.organizer.widget

import android.app.AlertDialog
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.organizer.R
import com.example.organizer.database.AppDatabase
import com.example.organizer.database.services.SalatService
import com.example.organizer.ui.Utils.DateUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import android.app.PendingIntent
import android.net.Uri
import android.view.View
import android.widget.Toast
import com.example.organizer.database.dto.OrganizerException
import com.example.organizer.utils.IntentRequestCode
import com.example.organizer.utils.SalatAlarmUtils


/**
 * Implementation of App Widget functionality.
 */
class SalatTimeAppWidget : AppWidgetProvider() {
    companion object {
        fun getRefreshWidgetPendingIntent(
            context: Context,
            skipNotification: Boolean = false
        ): PendingIntent? {
            val intent = Intent(context, SalatTimeAppWidget::class.java)
            intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            intent.data = Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME))
            if (skipNotification) {
//                SalatAlarmUtils.cancelScheduledAlert(context)
                intent.putExtra("skip-notification", skipNotification)
            }
            return PendingIntent.getBroadcast(
                context,
                if (skipNotification) IntentRequestCode.WidgetSKipNotificationIntent.requestCode else IntentRequestCode.WidgetRefreshIntent.requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        onUpdate(context, appWidgetManager, appWidgetIds, false)
    }

    private fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
        skipNotification: Boolean
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, skipNotification)
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            AppWidgetManager.ACTION_APPWIDGET_UPDATE -> onUpdate(
                context!!,
                AppWidgetManager.getInstance(context),
                AppWidgetManager.getInstance(context).getAppWidgetIds(
                    ComponentName(
                        context,
                        SalatTimeAppWidget::class.java
                    )
                ),
                intent.getBooleanExtra("skip-notification", false)
            )
        }
        super.onReceive(context, intent)
    }
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int,
    skipNotification: Boolean = false
) {
    // Construct the RemoteViews object
    val views = RemoteViews(context.packageName, R.layout.salat_time_app_widget)
    val db = AppDatabase.getInstance(context)
    val salatService = SalatService(db.salatTimesDao(), db.salatSettingsDao(), context)
    val coroutineScope = CoroutineScope(Dispatchers.Main.immediate)
    coroutineScope.launch {
        try {
            val curNext = salatService.getCurrentAndNextEvent()
            val current = curNext.first.first
            val next = curNext.first.second
            val alertCal = Calendar.getInstance()
            if (current != null) {
                views.setTextViewText(R.id.current_event, context.getString(current.type.labelKey))
                views.setTextViewText(
                    R.id.current_event_start,
                    if (current.range.first == null) "" else DateUtils.salatDisplayTime(current.range.first!!)
                )
                views.setTextViewText(
                    R.id.current_event_end,
                    context.getString(R.string.end_col) + "  " + if (current.range.second == null) "" else DateUtils.salatDisplayTime(
                        current.range.second!!
                    )
                )
            }
            if (next != null) {
                views.setTextViewText(R.id.next_event, context.getString(next.type.labelKey))
                views.setTextViewText(
                    R.id.next_event_start,
                    if (next.range.first == null) "" else DateUtils.salatDisplayTime(next.range.first!!)
                )
                views.setTextViewText(
                    R.id.next_event_end,
                    context.getString(R.string.end_col) + "  " + if (next.range.second == null) "" else DateUtils.salatDisplayTime(
                        next.range.second!!
                    )
                )
            }
            var skipNotificationButton: Int? = null
            if (!skipNotification && current?.range?.first != null && current.range.second != null) {
                val salatSettings = salatService.getActiveSalatSettings()
                val diff = SalatService.getSalatAlertTime(salatSettings, current.type)
                if (diff > 0) {
                    views.setTextViewText(R.id.skip_notification_start_min, "+$diff m")
                    skipNotificationButton = R.id.skip_notification_start
                    alertCal.time = current.range.first!!
                    alertCal.add(Calendar.MINUTE, diff)
                } else if (diff < 0) {
                    views.setTextViewText(R.id.skip_notification_end_min, "$diff m")
                    skipNotificationButton = R.id.skip_notification_end
                    alertCal.time = current.range.second!!
                    alertCal.add(Calendar.MINUTE, diff)
                }
            }

            val before = alertCal.time.before(Date())

            views.setViewVisibility(
                R.id.skip_notification_end_section,
                if (skipNotificationButton != R.id.skip_notification_end || before) View.GONE else View.VISIBLE
            )
            views.setViewVisibility(
                R.id.skip_notification_start_section,
                if (skipNotificationButton != R.id.skip_notification_start || before) View.GONE else View.VISIBLE
            )

            if (skipNotificationButton != null && !before) {
                views.setOnClickPendingIntent(
                    skipNotificationButton,
                    SalatTimeAppWidget.getRefreshWidgetPendingIntent(context, true)
                )
            }
            views.setOnClickPendingIntent(
                R.id.reload_button,
                SalatTimeAppWidget.getRefreshWidgetPendingIntent(context, skipNotification)
            )
            appWidgetManager.updateAppWidget(appWidgetId, views)
        } catch (ex: OrganizerException) {
            //TODO: WAIT and restart
        }
    }
}

