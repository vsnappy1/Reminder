package com.randos.reminder.utils

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import com.randos.reminder.data.entity.Task
import com.randos.reminder.ui.uiState.TaskUiState
import com.randos.reminder.ui.uiState.toTaskUiState
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

fun List<Task>.toTaskUiStateList(): List<TaskUiState> {
    val l = mutableListOf<TaskUiState>()
    this.forEach {
        l.add(it.toTaskUiState())
    }
    return l
}

fun LocalTime.format(): String {
    return format(DateTimeFormatter.ofPattern("hh:mm a"))
}

fun LocalDate.format(): String {
    if (this == LocalDate.now()) return "Today"
    if (this == LocalDate.now().plusDays(1)) return "Tomorrow"
    if (this == LocalDate.now().minusDays(1)) return "Yesterday"
    return format(DateTimeFormatter.ISO_LOCAL_DATE)
}

fun LocalDateTime.format(): String {
    return "${this.toLocalDate().format()}, ${this.toLocalTime().format()}"
}

fun Modifier.noRippleClickable( enabled: Boolean = true, onClick: () -> Unit): Modifier = composed {
    clickable(
        indication = null,
        interactionSource = remember { MutableInteractionSource() },
        enabled = enabled
    ) {
        onClick()
    }
}