package com.randos.reminder.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.ListenableWorker
import com.randos.reminder.R
import com.randos.reminder.data.ReminderDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Calendar

class DailyNotificationReceiver : BroadcastReceiver() {
    private val TAG = "DailyNotificationReceiv"
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null && intent != null) {
            CoroutineScope(Dispatchers.Main).launch {
                Log.d(TAG, "doWork initiated.")
                val tasks =
                    ReminderDatabase.getDatabase(context).taskDao().getTasks(LocalDate.now()).first()
                Log.d(TAG, "Task count ${tasks.size}.")
                if (tasks.isNotEmpty()) {
                    val notificationData = NotificationData(
                        id = 0,
                        title = "Reminder",
                        description = "You have ${tasks.size} ${if (tasks.size == 1) "reminder" else "reminders"} for today.",
                        priority = NotificationCompat.PRIORITY_DEFAULT,
                        calender = Calendar.getInstance(),
                        iconRes = R.drawable.ic_launcher_foreground,
                        chanelIdRes = R.string.notification_channel_id,
                        deepLinkPath = "reminder://today"
                    )
                    context.showNotification(notificationData)
                }

                Log.d(TAG, "doWork finished.")
            }
        }
    }
}