package com.randos.reminder.notification.worker

import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.randos.reminder.R
import com.randos.reminder.data.repository.TaskRepository
import com.randos.reminder.notification.NotificationData
import com.randos.reminder.notification.showNotification
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.util.Calendar
import javax.inject.Inject

private const val TAG = "TodayTaskNotification"

class TodayTaskNotificationWorker(
    val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    @Inject
    lateinit var taskRepository: TaskRepository

    override suspend fun doWork(): Result {
        Log.d(TAG, "doWork: ")
        try {
            val list = taskRepository.getTasksOn(LocalDate.now()).first().filter { it.time == null }
            if (list.isNotEmpty()) {
                val notificationData = NotificationData(
                    id = -1,
                    title = "Today Tasks",
                    description = "You have ${list.size} today.",
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