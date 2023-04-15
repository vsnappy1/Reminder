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
import com.randos.reminder.MainActivity
import com.randos.reminder.R
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Calendar
import javax.inject.Inject

private const val TAG = "NotificationManager"

interface NotificationManager {
    fun scheduleNotification(notificationData: NotificationData)
    fun updateScheduledNotification(notificationData: NotificationData)
    fun removeScheduledNotification(notificationId: Int)
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
}

fun Context.showNotification(notificationData: NotificationData) {
    val deepLinkIntent = Intent(
        Intent.ACTION_VIEW,
        notificationData.deepLinkPath?.toUri(),
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
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
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