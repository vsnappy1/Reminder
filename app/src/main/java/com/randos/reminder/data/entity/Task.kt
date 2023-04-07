package com.randos.reminder.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.randos.reminder.enums.Priority
import com.randos.reminder.enums.RepeatCycle
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.time.LocalDate
import java.time.LocalTime

@Entity
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val notes: String? = null,
    val date: LocalDate? = null,
    val time: LocalTime? = null,
    val repeat: RepeatCycle = RepeatCycle.NO_REPEAT,
    val priority: Priority = Priority.NONE,
    val done: Boolean = false
)

class Converters {
    @TypeConverter
    fun fromTimestamp(value: String?): LocalDate? {
        return value?.let { LocalDate.parse(it) }
    }

    @TypeConverter
    fun toTimestamp(date: LocalDate?): String? {
        return date?.toString()
    }

    @TypeConverter
    fun localTimeToNanoOfDay(time: LocalTime?): String? {
        return time?.toString()
    }

    @TypeConverter
    fun nanoOfDayToLocalTime(value: String?): LocalTime? {
        return value?.let { LocalTime.parse(it) }
    }
}

class LocalDateAdapter {
    @ToJson
    fun toJson(localDate: LocalDate): String {
        return localDate.toString()
    }

    @FromJson
    fun fromJson(json: String): LocalDate {
        return LocalDate.parse(json)
    }
}
class LocalTimeAdapter {
    @ToJson
    fun toJson(localTime: LocalTime): String {
        return localTime.toString()
    }

    @FromJson
    fun fromJson(json: String): LocalTime {
        return LocalTime.parse(json)
    }
}

