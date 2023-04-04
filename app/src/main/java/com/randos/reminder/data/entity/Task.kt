package com.randos.reminder.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.randos.reminder.enums.Priority
import com.randos.reminder.enums.RepeatCycle
import java.time.LocalDate
import java.time.LocalTime

@Entity
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val notes: String,
    val date: LocalDate? = null,
    val time: LocalTime? = null,
    val repeat: RepeatCycle = RepeatCycle.NO_REPEAT,
    val priority: Priority = Priority.NONE,
    val done: Boolean = false
)

class Converters {
    @TypeConverter
    fun localDateToEpochDay(localDate: LocalDate): Long {
        return localDate.toEpochDay()
    }

    @TypeConverter
    fun epochDayToLocalDate(epochDay: Long): LocalDate {
        return LocalDate.ofEpochDay(epochDay)
    }

    @TypeConverter
    fun localTimeToNanoOfDay(localTime: LocalTime): Long {
        return localTime.toNanoOfDay()
    }

    @TypeConverter
    fun nanoOfDayToLocalTime(nanoOfDay: Long): LocalTime {
        return LocalTime.ofNanoOfDay(nanoOfDay)
    }
}

data class TaskUiState(
    val id: Long = 0,
    val title: String = "",
    val notes: String = "",
    val isDateChecked: Boolean = false,
    val date: LocalDate? = null,
    val isTimeChecked: Boolean = false,
    val time: LocalTime? = null,
    val isRepeatChecked: Boolean = false,
    val repeat: RepeatCycle = RepeatCycle.NO_REPEAT,
    val priority: Priority = Priority.NONE,
    val done: Boolean = false
)

fun Task.toTaskUiState(): TaskUiState {
    return TaskUiState(
        this.id,
        this.title,
        this.notes,
        false,
        this.date,
        false,
        this.time,
        false,
        this.repeat,
        this.priority,
        this.done
    )
}

fun TaskUiState.toTask(): Task {
    return Task(
        this.id,
        this.title,
        this.notes,
        this.date,
        this.time,
        this.repeat,
        this.priority,
        this.done
    )
}

fun TaskUiState.isValid(): Boolean {
    return title.isNotBlank()
}


