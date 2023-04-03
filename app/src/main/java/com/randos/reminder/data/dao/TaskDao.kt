package com.randos.reminder.data.dao

import androidx.room.*
import com.randos.reminder.data.entity.Task
import kotlinx.coroutines.flow.Flow


@Dao
interface TaskDao {
    @Insert
    suspend fun insert(task: Task)

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)

    @Query("SELECT * FROM task WHERE id = :id")
    fun getTask(id: Long): Flow<Task>

    @Query("SELECT * FROM task")
    fun getTasks(): Flow<List<Task>>
}