package com.randos.reminder

import android.app.Application
import com.randos.reminder.notification.createNotificationChannel
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ReminderApplication: Application(){

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }
}

