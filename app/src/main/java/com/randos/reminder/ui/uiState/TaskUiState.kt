package com.randos.reminder.ui.uiState

import com.randos.reminder.data.entity.Task
import com.randos.reminder.enums.Priority
import com.randos.reminder.enums.RepeatCycle
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

data class TaskUiState(
    val id: Long = 0,
    val title: String = "title",
    val notes: String? = null,
    val date: LocalDate? = LocalDate.now(),
    val time: LocalTime? = null,
    val repeat: RepeatCycle = RepeatCycle.NO_REPEAT,
    val priority: Priority = Priority.NONE,
    val done: Boolean = false,
    val addedOn: LocalDateTime = LocalDateTime.now(),
    val completedOn: LocalDateTime? = null,
    val isDateChecked: Boolean = date != null,
    val isTimeChecked: Boolean = time != null,
    val isRepeatChecked: Boolean = repeat != RepeatCycle.NO_REPEAT,
    val isDue: Boolean = (!done && (date?.isBefore(LocalDate.now()) == true ||
            (date?.isBefore(LocalDate.now()) == true && time?.isBefore(LocalTime.now()) == true)))
)

fun Task.toTaskUiState(): TaskUiState {
    return TaskUiState(
        id = this.id,
        title = this.title,
        notes = this.notes,
        date = this.date,
        time = this.time,
        repeat = this.repeat,
        priority = this.priority,
        done = this.done,
        addedOn = this.addedOn,
        completedOn = this.completedOn,
        isDateChecked = this.date != null,
        isTimeChecked = this.time != null,
        isRepeatChecked = this.repeat != RepeatCycle.NO_REPEAT,
    )
}

fun TaskUiState.toTask(): Task {
    return Task(
        id = this.id,
        title = this.title,
        notes = this.notes,
        date = this.date,
        time = this.time,
        repeat = this.repeat,
        priority = this.priority,
        done = this.done,
        addedOn = this.addedOn,
        completedOn = this.completedOn
    )
}

fun TaskUiState.isValid(): Boolean {
    return title.isNotBlank()
}
