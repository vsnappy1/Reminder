package com.randos.reminder.data.repository

import com.randos.reminder.data.entity.Task
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface TaskRepository {

    suspend fun insertTask(task: Task)

    suspend fun updateTask(task: Task)

    suspend fun deleteTask(task: Task)

    fun getTask(id: Long): Flow<Task>

    fun getTasks(): Flow<List<Task>>

    fun getTodayAndDueTasks(date: LocalDate): Flow<List<Task>>

    fun getTasksOn(date: LocalDate): Flow<List<Task>>

    fun getTasksBetween(start: LocalDate, end: LocalDate): Flow<List<Task>>

    fun getTasksAfter(date: LocalDate): Flow<List<Task>>

    fun getTasksBefore(date: LocalDate): Flow<List<Task>>

    fun getCompletedTasks(): Flow<List<Task>>

    fun getCompletedTasksOn(date: LocalDate): Flow<List<Task>>

    fun getCompletedTasksBetween(start: LocalDate, end: LocalDate): Flow<List<Task>>

    fun getCompletedTasksAfter(date: LocalDate): Flow<List<Task>>

    fun getCompletedTasksBefore(date: LocalDate): Flow<List<Task>>
}