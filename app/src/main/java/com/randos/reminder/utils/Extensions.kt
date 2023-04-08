package com.randos.reminder.utils

import com.randos.reminder.data.entity.LocalDateAdapter
import com.randos.reminder.data.entity.LocalTimeAdapter
import com.randos.reminder.data.entity.Task
import com.randos.reminder.ui.uiState.TaskUiState
import com.randos.reminder.ui.uiState.toTaskUiState
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.time.LocalDate
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