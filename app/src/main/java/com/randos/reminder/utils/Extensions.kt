package com.randos.reminder.utils

import androidx.compose.ui.res.stringResource
import com.randos.reminder.data.entity.LocalDateAdapter
import com.randos.reminder.data.entity.LocalTimeAdapter
import com.randos.reminder.data.entity.Task
import com.randos.reminder.ui.uiState.TaskUiState
import com.randos.reminder.ui.uiState.toTaskUiState
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.toList
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

suspend fun <T> Flow<List<T>>.toList() =
    flatMapConcat { it.asFlow() }.toList()

fun List<Task>.toTaskUiStateList(): List<TaskUiState> {
    val l = mutableListOf<TaskUiState>()
    this.forEach {
        l.add(it.toTaskUiState())
    }
    return l
}

inline fun <reified T> T.toJson(): String {
    val moshi = Moshi.Builder()
        .add(LocalDateAdapter())
        .add(LocalTimeAdapter())
        .addLast(KotlinJsonAdapterFactory())
        .build()
    val jsonAdapter: JsonAdapter<T> =
        moshi.adapter(T::class.java).lenient()
    return jsonAdapter.toJson(this)
}

inline fun <reified T> String?.toObject(): T? {
    this?.let {
        val moshi = Moshi.Builder()
            .add(LocalDateAdapter())
            .add(LocalTimeAdapter())
            .addLast(KotlinJsonAdapterFactory())
            .build()
        val jsonAdapter: JsonAdapter<T> =
            moshi.adapter(T::class.java).lenient()
        return jsonAdapter.fromJson(this)
    }
    return null
}

fun LocalTime.format(): String {
    return format(DateTimeFormatter.ofPattern("hh:mm a"))
}

fun LocalDate.format(): String {
    if(this == LocalDate.now()) return "Today"
    if(this == LocalDate.now().plusDays(1)) return "Tomorrow"
    if(this == LocalDate.now().minusDays(1)) return "Yesterday"
    return format(DateTimeFormatter.ISO_LOCAL_DATE)
}