package com.randos.reminder.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null && intent != null) {
            val notificationData: NotificationData? =
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                    intent.getSerializableExtra("notificationData", NotificationData::class.java)
                } else {
                    intent.getSerializableExtra("notificationData") as NotificationData
                }
            notificationData?.let { context.showNotification(it) }
        }
    }
}