package com.randos.reminder.notification

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.randos.reminder.MainActivity
import com.randos.reminder.R
import com.randos.reminder.notification.worker.DeferredNotificationWorker
import com.randos.reminder.notification.worker.TodayTaskNotificationWorker
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private const val TAG = "NotificationManager"

interface NotificationManager {
    fun scheduleNotification(notificationData: NotificationData)
    fun updateScheduledNotification(notificationData: NotificationData)
    fun removeScheduledNotification(notificationId: Int)
    fun setDailyNotification()
}

class NotificationManagerImpl @Inject constructor(val context: Context) : NotificationManager {
    override fun scheduleNotification(notificationData: NotificationData) {
        notificationData.calender?.let { calendar ->
            if (calendar.before(Calendar.getInstance())) {
                Log.d(TAG, "Alarm not scheduled for past time, id = ${notificationData.id}.")
                return
            }

            // If task is scheduled in next 24 hours use the Alarm Manager for notification.
            if (getHourDifference(calendar) <= 24) {
                context.scheduleNotificationUsingAlarmManager(notificationData)
            } else { // Else use Work Manager to schedule the Alarm Manager, doing this to be easy on battery.
                scheduleDeferredNotification(notificationData)
            }
        }
    }

    private fun scheduleDeferredNotification(notificationData: NotificationData) {
        val data = Data.Builder()
        data.putInt("id", notificationData.id)

        val delay =
            notificationData.calender?.timeInMillis?.minus(Calendar.getInstance().timeInMillis)
                ?.minus(24 * 60 * 60 * 1000) ?: 0

        val oneTimeWorkRequest =
            OneTimeWorkRequestBuilder<DeferredNotificationWorker>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setInputData(data.build())
                .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "${notificationData.id}",
            ExistingWorkPolicy.REPLACE,
            oneTimeWorkRequest
        )
        Log.d(TAG, "Scheduled alarm manager worker, id = ${notificationData.id}.")
    }

    private fun getHourDifference(calender: Calendar): Long {
        val difference = calender.timeInMillis.minus(Calendar.getInstance().timeInMillis)
        return difference.div(60 * 60 * 1000)
    }

    override fun updateScheduledNotification(notificationData: NotificationData) {
        removeScheduledNotification(notificationData.id)
        scheduleNotification(notificationData)
    }

    override fun removeScheduledNotification(notificationId: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        // Cancel the alarm if present
        alarmManager.cancel(pendingIntent)

        // Cancel the worker if present
        WorkManager.getInstance(context).cancelUniqueWork("$notificationId")
        Log.d(TAG, "Scheduled notification removed, id = $notificationId")
    }

    override fun setDailyNotification() {
        val dailyMorningNotification =
            PeriodicWorkRequestBuilder<TodayTaskNotificationWorker>(1, TimeUnit.DAYS)
                .setInitialDelay(getInitialDelay(), TimeUnit.MILLISECONDS)
                .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "DailyNotification",
            ExistingPeriodicWorkPolicy.KEEP,
            dailyMorningNotification
        )
        Log.d(TAG, "setDailyNotification: setup")
    }

    private fun getInitialDelay(): Long {
        val now = Calendar.getInstance()
        val due = Calendar.getInstance()
        due[Calendar.HOUR_OF_DAY] = 9
        due[Calendar.MINUTE] = 0
        due[Calendar.SECOND] = 0
        if (now.after(due)) {
            due.add(Calendar.DAY_OF_MONTH, 1)
        }
        return due.timeInMillis - now.timeInMillis
    }
}

fun Context.scheduleNotificationUsingAlarmManager(
    notificationData: NotificationData
) {
    val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(this, NotificationReceiver::class.java).apply {
        putExtra("notificationData", notificationData)
    }
    val pendingIntent = PendingIntent.getBroadcast(
        this,
        notificationData.id,
        intent,
        PendingIntent.FLAG_IMMUTABLE
    )
    notificationData.calender?.let {
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            notificationData.calender.timeInMillis,
            pendingIntent
        )
    }
    Log.d(TAG, "Scheduled notification using alarm manager, id = ${notificationData.id}.")
}

fun Context.showNotification(notificationData: NotificationData) {
    val deepLinkIntent = Intent(
        Intent.ACTION_VIEW,
        "${notificationData.deepLinkPath}/${notificationData.id}".toUri(),
        this,
        MainActivity::class.java
    )
    val deepLinkPendingIntent: PendingIntent? = TaskStackBuilder.create(this).run {
        addNextIntentWithParentStack(deepLinkIntent)
        getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE)
    }
    val builder = NotificationCompat.Builder(this, getString(notificationData.chanelIdRes))
        .setSmallIcon(notificationData.iconRes)
        .setContentTitle(notificationData.title)
        .setContentText(notificationData.description)
        .setPriority(notificationData.priority)
        .setContentIntent(deepLinkPendingIntent)
        .setAutoCancel(notificationData.autoCancel)

    with(NotificationManagerCompat.from(this)) {
        if (ActivityCompat.checkSelfPermission(
                this@showNotification,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        notify(notificationData.id, builder.build())
    }
}

fun Context.createNotificationChannel() {
    // Create channel
    val id = getString(R.string.notification_channel_id)
    val name = getString(R.string.notification_channel_name)
    val descriptionText = getString(R.string.notification_channel_description)
    val importance = android.app.NotificationManager.IMPORTANCE_DEFAULT
    val channel =
        NotificationChannel(id, name, importance).apply { description = descriptionText }

    // Register the channel with system
    val notificationManager: android.app.NotificationManager =
        getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
    notificationManager.createNotificationChannel(channel)
}