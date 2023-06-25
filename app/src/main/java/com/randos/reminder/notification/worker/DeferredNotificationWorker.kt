package com.randos.reminder.notification.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.randos.reminder.data.ReminderDatabase
import com.randos.reminder.notification.scheduleNotificationUsingAlarmManager
import com.randos.reminder.notification.toNotificationData

class DeferredNotificationWorker(
    val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val taskId = inputData.getInt("id", 0)
        ReminderDatabase.getDatabase(context).taskDao().getTask(taskId.toLong()).collect { task ->
            context.scheduleNotificationUsingAlarmManager(task.toNotificationData())
        }
        return Result.success()
    }
}