package com.randos.reminder.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.randos.reminder.data.ReminderDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null && intent != null) {
            val notificationData: NotificationData? =
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                    intent.getSerializableExtra("notificationData", NotificationData::class.java)
                } else {
                    intent.getSerializableExtra("notificationData") as NotificationData
                }

            notificationData?.let {
                CoroutineScope(Dispatchers.Main).launch {
                    val task =
                        ReminderDatabase.getDatabase(context).taskDao().getTask(it.id.toLong())
                            .first()
                    if (!task.notificationTriggered) {
                        ReminderDatabase.getDatabase(context).taskDao()
                            .updateNotificationTriggeredStatus(it.id, true)
                        context.showNotification(it)
                    }
                }
            }
        }
    }
}