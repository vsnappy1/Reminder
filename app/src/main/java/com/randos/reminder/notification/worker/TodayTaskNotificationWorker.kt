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
import kotlinx.coroutines.flow.toList
import java.time.LocalDate
import java.util.Calendar

private const val TAG = "TodayTaskNotification"

class TodayTaskNotificationWorker(
    val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        Log.d(TAG, "doWork: ")
        val tasks =
            ReminderDatabase.getDatabase(context).taskDao().getTasks(LocalDate.now()).toList()[0]
        Log.d(TAG, "Number of tasks ${tasks.size}")
        try {
            if (tasks.isNotEmpty()) {
                val notificationData = NotificationData(
                    id = 0,
                    title = "Today Tasks",
                    description = "You have ${tasks.size} today.",
                    priority = NotificationCompat.PRIORITY_DEFAULT,
                    calender = Calendar.getInstance(),
                    iconRes = R.drawable.ic_launcher_foreground,
                    chanelIdRes = R.string.notification_channel_id,
                    deepLinkPath = "reminder://today"
                )
                context.showNotification(notificationData)
            }
            return Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "doWork: ", e.cause)
            return Result.failure()
        }
    }
}