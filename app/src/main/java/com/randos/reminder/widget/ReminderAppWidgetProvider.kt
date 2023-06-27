package com.randos.reminder.widget

import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import android.widget.RemoteViews
import androidx.core.net.toUri
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.randos.reminder.MainActivity
import com.randos.reminder.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


/**
 * Implementation of App Widget functionality.
 */
private const val TAG = "ReminderWidget"

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class ReminderAppWidgetProvider : AppWidgetProvider() {
    override fun peekService(myContext: Context?, service: Intent?): IBinder {
        Log.d(TAG, "peekService: ")
        return super.peekService(myContext, service)
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        Log.d(TAG, "onUpdate: ")
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "onReceive: ")
        if (AppWidgetManager.ACTION_APPWIDGET_UPDATE == intent.action) {
            val widgetIds = intent.extras?.getIntArray(AppWidgetManager.EXTRA_APPWIDGET_IDS)
            if (widgetIds?.isNotEmpty() != null) {
                onUpdate(context, AppWidgetManager.getInstance(context), widgetIds)
            } else {
                Log.d(
                    TAG,
                    "onUpdate, input: no widgetIds, discovered here:" + listOf(getWidgetIds(context)) + ", context:" + context
                )
            }
        }
        super.onReceive(context, intent)
    }

    private fun getWidgetIds(context: Context): IntArray? {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        return if (appWidgetManager == null) intArrayOf() else appWidgetManager.getAppWidgetIds(
            ComponentName(
                context,
                AppWidgetProvider::class.java
            )
        )
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
        CoroutineScope(Dispatchers.Main).launch { toggleIsFirstTime(context) }
        Log.d(TAG, "onEnabled: ")
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
        Log.d(TAG, "onDisabled: ")
    }
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    // Construct the RemoteViews object
    val intent = Intent(context, ReminderWidgetService::class.java)
    intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
    val remoteViews = RemoteViews(context.packageName, R.layout.reminder_widget)
    remoteViews.setRemoteAdapter(R.id.recyclerView, intent)

    // Set on click listener for "All reminder" text
    remoteViews.setOnClickPendingIntent(
        R.id.textViewAllReminder,
        PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )
    )

    // Set on click listener for add button
    val addTaskScreenIntent = Intent(
        Intent.ACTION_VIEW,
        "reminder://add".toUri(),
        context,
        MainActivity::class.java
    )
    // This makes the deep link work
    val addTaskScreenPendingIntent: PendingIntent? = TaskStackBuilder.create(context).run {
        addNextIntentWithParentStack(addTaskScreenIntent)
        getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE)
    }

    remoteViews.setOnClickPendingIntent(R.id.imageViewAdd, addTaskScreenPendingIntent)

    // Template to handle the click listener for each item
    val templateIntent = Intent(
        Intent.ACTION_VIEW,
        "reminder://all".toUri(),
        context,
        MainActivity::class.java
    )
    val templateIntentPendingIntent: PendingIntent? = TaskStackBuilder.create(context).run {
        addNextIntentWithParentStack(templateIntent)
        getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE)
    }

    remoteViews.setPendingIntentTemplate(R.id.recyclerView, templateIntentPendingIntent)

    // Set padding
    remoteViews.setViewPadding(R.id.container, 0,0,0,0)

    // Instruct the widget manager to update the widget
    appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.recyclerView)

    CoroutineScope(Dispatchers.Main).launch {
        isFirstTime(context).collect { isFirstTime ->
            if (isFirstTime) {
                Log.d(TAG, "updateAppWidget: FirstTime")
                appWidgetManager.updateAppWidget(appWidgetId, remoteViews)
                toggleIsFirstTime(context)
            }
        }
    }
}

private fun isFirstTime(context: Context) = context.dataStore.data.map { preferences ->
    preferences[booleanPreferencesKey("is_first_time")] ?: true
}

private suspend fun toggleIsFirstTime(context: Context) {
    context.dataStore.edit { settings ->
        settings[booleanPreferencesKey("is_first_time")] =
            settings[booleanPreferencesKey("is_first_time")]?.not() ?: false
    }
}