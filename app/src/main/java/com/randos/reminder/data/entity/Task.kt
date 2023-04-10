package com.randos.reminder.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.randos.reminder.enums.Priority
import com.randos.reminder.enums.RepeatCycle
import java.time.LocalDate
import java.time.LocalDateTime
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
    val done: Boolean = false,
    val addedOn: LocalDateTime,
    val completedOn: LocalDateTime? = null
)

class Converters {
    @TypeConverter
    fun fromStringDate(value: String?): LocalDate? {
        return value?.let { LocalDate.parse(it) }
    }

    @TypeConverter
    fun toDate(date: LocalDate?): String? {
        return date?.toString()
    }

    @TypeConverter
    fun fromStringTime(value: String?): LocalTime? {
        return value?.let { LocalTime.parse(it) }
    }

    @TypeConverter
    fun toTime(time: LocalTime?): String? {
        return time?.toString()
    }

    @TypeConverter
    fun fromStringDateTime(value: String?): LocalDateTime? {
        return value?.let { LocalDateTime.parse(it) }
    }

    @TypeConverter
    fun toDateTime(time: LocalDateTime?): String? {
        return time?.toString()
    }
}

