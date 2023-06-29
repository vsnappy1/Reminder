package com.randos.reminder.notification.worker

import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.randos.reminder.R
import com.randos.reminder.data.ReminderDatabase
import com.randos.reminder.notification.NotificationData
import com.randos.reminder.notification.showNotification
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.util.Calendar

private const val TAG = "TodayTaskNotification"

class TodayTaskNotificationWorker(
    val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
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
        return Result.success()
    }
}