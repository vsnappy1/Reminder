package com.randos.reminder.ui.uiState

import com.randos.reminder.data.entity.Task
import com.randos.reminder.enums.Priority
import com.randos.reminder.enums.RepeatCycle
import java.time.LocalDate
import java.time.LocalTime

data class TaskUiState(
    val id: Long = 0,
    val title: String = "",
    val notes: String? = null,
    val date: LocalDate? = null,
    val time: LocalTime? = null,
    val repeat: RepeatCycle = RepeatCycle.NO_REPEAT,
    val priority: Priority = Priority.NONE,
    val done: Boolean = false,
    val isDateChecked: Boolean = date != null,
    val isTimeChecked: Boolean = time != null,
    val isRepeatChecked: Boolean = repeat != RepeatCycle.NO_REPEAT,
    val isDue: Boolean = (!done && (date?.isBefore(LocalDate.now()) == true || time?.isBefore(LocalTime.now()) == true))
)

fun Task.toTaskUiState(): TaskUiState {
    return TaskUiState(
        id = this.id,
        title = this.title,
        notes = this.notes,
        isDateChecked = this.date != null,
        date = this.date,
        isTimeChecked = this.time != null,
        time = this.time,
        isRepeatChecked = this.repeat != RepeatCycle.NO_REPEAT,
        repeat = this.repeat,
        priority = this.priority,
        done = this.done
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
