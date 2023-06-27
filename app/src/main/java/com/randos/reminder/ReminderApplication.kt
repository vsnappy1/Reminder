package com.randos.reminder

import android.app.Application
import androidx.work.Configuration
import com.randos.reminder.notification.createNotificationChannel
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ReminderApplication : Application(), Configuration.Provider {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun getWorkManagerConfiguration(): Configuration =
        Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.DEBUG)
            .build()
}

