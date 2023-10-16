package com.randos.reminder

import android.Manifest
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.navigation.compose.rememberNavController
import com.example.compose.ReminderTheme
import com.randos.reminder.navigation.NavGraph
import com.randos.reminder.notification.NotificationManager
import com.randos.reminder.ui.viewmodel.BaseViewModel
import com.randos.reminder.utils.isNotificationPermissionGranted
import com.randos.reminder.widget.ReminderAppWidgetProvider
import com.randos.reminder.widget.dataStore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "MainActivity-"

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val notificationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        if (this.isNotificationPermissionGranted()) {
            Log.d(TAG, "Notification permission granted.")
            notificationManager.setDailyNotification(this@MainActivity)
        } else {
            Log.d(TAG, "Notification permission not granted.")
        }
    }

    private fun requestNotificationPermission() {
        CoroutineScope(Dispatchers.Main).launch {
            dataStore.edit { pref ->
                val count = pref[intPreferencesKey("notification_permission_asked_count")] ?: 0
                if (count < 2 || android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.R) {
                    notificationPermissionRequest.launch(
                        arrayOf(Manifest.permission.POST_NOTIFICATIONS)
                    )
                } else {
                    openAppInSettings()
                }
                pref[intPreferencesKey("notification_permission_asked_count")] = count + 1
            }
        }
    }

    @Inject
    lateinit var notificationManager: NotificationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ReminderTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavGraph(navController = navController, this) {
                        requestNotificationPermission()
//                        openAppInSettings()
                    }
                }
            }
        }
        setupDailyNotification(this)
    }

    private fun setupDailyNotification(context: Context) {
        if (isNotificationPermissionGranted()) {
            notificationManager.setDailyNotification(context)
        } else {
            notificationManager.unsetDailyNotification(context)
        }
    }

    override fun onStop() {
        super.onStop()
        if (BaseViewModel.isDataModified) {
            updateWidget(this)
        }
    }

    fun openAppInSettings() {
        val intent = Intent()
        intent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        if (android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.N) {
            intent.putExtra("app_package", packageName)
            intent.putExtra("app_uid", applicationInfo.uid)
        } else {
            intent.putExtra("android.provider.extra.APP_PACKAGE", packageName)
        }

        startActivity(intent)
    }

    private fun updateWidget(context: Context) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val appWidgetIds: IntArray =
            appWidgetManager.getAppWidgetIds(
                ComponentName(
                    context,
                    ReminderAppWidgetProvider::class.java
                )
            )

        val intent = Intent(this@MainActivity, ReminderAppWidgetProvider::class.java)
        intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)
        sendBroadcast(intent)
    }
}