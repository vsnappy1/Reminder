package com.randos.reminder.notification

import androidx.core.app.NotificationCompat
import com.randos.reminder.R
import com.randos.reminder.data.entity.Task
import com.randos.reminder.enums.Priority
import java.time.LocalDate
import java.time.LocalTime
import java.util.Calendar

data class NotificationData(
    val id: Int,
    val title: String,
    val description: String,
    val priority: Int,
    val iconRes: Int,
    val calender: Calendar?,
    val chanelIdRes: Int,
    val autoCancel: Boolean = true,
    val deepLinkPath: String? = null
) : java.io.Serializable

fun Task.toNotificationData(): NotificationData {
    return NotificationData(
        id = id.toInt(),
        title = title,
        description = notes ?: "",
        priority = getPriority(priority),
        calender = getCalender(date, time),
        iconRes = R.drawable.ic_launcher_foreground,
        chanelIdRes = R.string.notification_channel_id,
        deepLinkPath = "reminder://today"
    )
}

fun getCalender(date: LocalDate?, time: LocalTime?): Calendar? {
    if (date != null && time != null) {
        val calender: Calendar = Calendar.getInstance()
        calender.set(
            date.year,
            date.month.value - 1, // because it is 0 to 11
            date.dayOfMonth,
            time.hour,
            time.minute
        )
        calender.set(Calendar.SECOND, 0)
        return calender
    }
    return null
}

private fun getPriority(priority: Priority): Int {
    if (priority == Priority.HIGH) return NotificationCompat.PRIORITY_MAX
    if (priority == Priority.MEDIUM) return NotificationCompat.PRIORITY_HIGH
    if (priority == Priority.LOW) return NotificationCompat.PRIORITY_LOW
    return NotificationCompat.PRIORITY_DEFAULT
}