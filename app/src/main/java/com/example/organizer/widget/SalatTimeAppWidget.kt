package com.example.organizer.widget

import android.annotation.SuppressLint
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


/**
 * Implementation of App Widget functionality.
 */
class SalatTimeAppWidget : AppWidgetProvider() {
    companion object {
        fun getRefreshWidgetPendingIntent(context: Context): PendingIntent? {
            val intent = Intent(context, SalatTimeAppWidget::class.java)
            intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            intent.data = Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME))
            return PendingIntent.getBroadcast(
                context,
                0,
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
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        when(intent?.action) {
            AppWidgetManager.ACTION_APPWIDGET_UPDATE -> onUpdate(
                context!!,
                AppWidgetManager.getInstance(context),
                AppWidgetManager.getInstance(context).getAppWidgetIds(
                    ComponentName(
                        context,
                        SalatTimeAppWidget::class.java
                    )
                )
            )
        }
        super.onReceive(context, intent)
    }
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    // Construct the RemoteViews object
    val views = RemoteViews(context.packageName, R.layout.salat_time_app_widget)
    val db = AppDatabase.getInstance(context)
    val salatService = SalatService(db.salatTimesDao(), db.salatSettingsDao(), context)
    val coroutineScope = CoroutineScope(Dispatchers.Main.immediate)
    coroutineScope.launch {
        val curNext = salatService.getCurrentAndNextEvent()
        val current = curNext.first.first
        val next = curNext.first.second
        if(current != null) {
            views.setTextViewText(R.id.current_event, context.getString(current.type.labelKey))
            views.setTextViewText(
                R.id.current_event_start,
                if (current.range.first == null) "" else DateUtils.salatDisplayTime(current.range.first!!)
            )
            views.setTextViewText(
                R.id.current_event_end,
                context.getString(R.string.end_at) + "  " + if (current.range.second == null) "" else DateUtils.salatDisplayTime(
                    current.range.second!!
                )
            )
        }
        if(next != null) {
            views.setTextViewText(R.id.next_event, context.getString(next.type.labelKey))
            views.setTextViewText(
                R.id.next_event_start,
                if (next.range.first == null) "" else DateUtils.salatDisplayTime(next.range.first!!)
            )
            views.setTextViewText(
                R.id.next_event_end,
                context.getString(R.string.end_at) + "  " + if (next.range.second == null) "" else DateUtils.salatDisplayTime(
                    next.range.second!!
                )
            )
        }
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
}