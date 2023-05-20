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
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.randos.reminder.MainActivity
import com.randos.reminder.R
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
        if (notificationData.calender?.before(Calendar.getInstance()) == true) {
            Log.d(TAG, "scheduleNotification: Alarm not scheduled for past.")
            return
        }
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("notificationData", notificationData)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
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
        alarmManager.cancel(pendingIntent)
    }

    override fun setDailyNotification() {
        //TODO set work manager
//        val dailyMorningNotification =
//            PeriodicWorkRequestBuilder<TodayTaskNotificationWorker>(5, TimeUnit.MINUTES)
////                .setInitialDelay(getInitialDelay(), TimeUnit.MILLISECONDS)
//                .build()
//
//        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
//            "DailyNotification",
//            ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
//            dailyMorningNotification
//        )
//        Log.d(TAG, "setDailyNotification: setup")
    }

    private fun getInitialDelay(): Long {
        val now = Calendar.getInstance()
        val due = Calendar.getInstance()
        due.set(Calendar.HOUR_OF_DAY, 9)
        due.set(Calendar.MINUTE, 0)
        due.set(Calendar.SECOND, 0)
        if (now.after(due)) {
            due.add(Calendar.DAY_OF_MONTH, 1)
        }
        return due.timeInMillis - now.timeInMillis
    }
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