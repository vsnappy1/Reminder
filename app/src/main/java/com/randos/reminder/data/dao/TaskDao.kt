package com.randos.reminder.data.dao

import androidx.room.*
import com.randos.reminder.data.entity.Task
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

private const val orderBy = "ORDER BY date IS NULL, date ASC, time IS NULL, time ASC"

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

    @Query("SELECT * FROM task WHERE done = :done $orderBy")
    fun getTasks(done: Boolean): Flow<List<Task>>

    @Query("SELECT * FROM task WHERE date = :date AND done = :done $orderBy")
    fun getTasks(date: LocalDate, done: Boolean): Flow<List<Task>>

    @Query("SELECT * FROM task WHERE date >= :start AND date <= :end AND done = :done $orderBy")
    fun getTasks(start: LocalDate, end: LocalDate, done: Boolean): Flow<List<Task>>

    @Query("SELECT * FROM task WHERE date > :date AND done = :done $orderBy")
    fun getTasksAfter(date: LocalDate, done: Boolean): Flow<List<Task>>

    @Query("SELECT * FROM task WHERE date < :date AND done = :done $orderBy")
    fun getTasksBefore(date: LocalDate, done: Boolean): Flow<List<Task>>
}