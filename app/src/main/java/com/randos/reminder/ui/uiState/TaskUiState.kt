package com.randos.reminder.ui.uiState

import com.randos.reminder.data.entity.Task
import com.randos.reminder.enums.Priority
import com.randos.reminder.enums.RepeatCycle
import java.time.LocalDate
import java.time.LocalTime

data class TaskUiState(
    val id: Long = 0,
    val title: String = "aa",
    val notes: String? = null,
    val isDateChecked: Boolean = true,
    val date: LocalDate? = LocalDate.now(),
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
        this.date != null,
        this.date,
        this.time != null,
        this.time,
        this.repeat != RepeatCycle.NO_REPEAT,
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
