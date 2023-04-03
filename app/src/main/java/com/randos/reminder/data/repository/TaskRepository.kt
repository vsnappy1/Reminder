package com.randos.reminder.data.repository

import com.randos.reminder.data.entity.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {

    suspend fun insertTask(task: Task)

    suspend fun updateTask(task: Task)

    suspend fun deleteTask(task: Task)

    fun getTask(id: Long): Flow<Task>

    fun getTasks():Flow<List<Task>>
}