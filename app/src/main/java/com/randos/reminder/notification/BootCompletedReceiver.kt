package com.randos.reminder.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.randos.reminder.data.ReminderDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate

class BootCompletedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        CoroutineScope(Dispatchers.Main).launch {

            // Schedule all notification for today
            val tasksToday =
                ReminderDatabase.getDatabase(context).taskDao()
                    .getTasksScheduled(LocalDate.now()).first()
            tasksToday
                .filter { !it.notificationTriggered }
                .forEach { context.scheduleNotificationUsingAlarmManager(it.toNotificationData()) }

            // Schedule all notification for tomorrow
            val tasksTomorrow = ReminderDatabase.getDatabase(context).taskDao()
                .getTasksScheduled(LocalDate.now().plusDays(1)).first()
            tasksTomorrow
                .filter { !it.notificationTriggered }
                .forEach { context.scheduleNotificationUsingAlarmManager(it.toNotificationData()) }
        }
    }
}