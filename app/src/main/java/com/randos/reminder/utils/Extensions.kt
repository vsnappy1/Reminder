package com.randos.reminder.utils

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