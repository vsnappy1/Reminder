package com.randos.reminder.data.dao

import androidx.room.*
import com.randos.reminder.data.entity.Task
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

private const val orderBy = "ORDER BY date IS NULL, date ASC, time IS NULL, time ASC"

@Dao
interface TaskDao {
    @Insert
    suspend fun insert(task: Task): Long

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)

    @Query("SELECT * FROM task WHERE id = :id")
    fun getTask(id: Long): Flow<Task>

    @Query("SELECT * FROM task WHERE completedOn IS NULL $orderBy")
    fun getTasks(): Flow<List<Task>>

    @Query("SELECT * FROM task WHERE date = :date AND completedOn IS NULL $orderBy")
    fun getTasks(date: LocalDate): Flow<List<Task>>

    @Query("SELECT * FROM task WHERE date >= :start AND date <= :end AND completedOn IS NULL $orderBy")
    fun getTasks(start: LocalDate, end: LocalDate): Flow<List<Task>>

    @Query("SELECT * FROM task WHERE date > :date AND completedOn IS NULL $orderBy")
    fun getTasksAfter(date: LocalDate): Flow<List<Task>>

    @Query("SELECT * FROM task WHERE date < :date AND completedOn IS NULL $orderBy")
    fun getTasksBefore(date: LocalDate): Flow<List<Task>>

    @Query("SELECT * FROM task WHERE completedOn IS NOT NULL $orderBy")
    fun getCompletedTasks(): Flow<List<Task>>

    @Query("SELECT * FROM task WHERE date(completedOn) = date(:date) $orderBy")
    fun getCompletedTasks(date: LocalDate): Flow<List<Task>>

    @Query("SELECT * FROM task WHERE date(completedOn) >= date(:start) AND date(completedOn) <= date(:end) $orderBy")
    fun getCompletedTasks(start: LocalDate, end: LocalDate): Flow<List<Task>>

    @Query("SELECT * FROM task WHERE date(completedOn) > date(:date) $orderBy")
    fun getCompletedTasksAfter(date: LocalDate): Flow<List<Task>>

    @Query("SELECT * FROM task WHERE date(completedOn) < date(:date) $orderBy")
    fun getCompletedTasksBefore(date: LocalDate): Flow<List<Task>>

    @Query("SELECT COUNT(*) FROM task WHERE completedOn IS NULL $orderBy")
    fun getTasksCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM task WHERE date <= :date AND completedOn IS NULL $orderBy")
    fun getTodayTasksCount(date: LocalDate): Flow<Int>

    @Query("SELECT COUNT(*) FROM task WHERE date IS NOT NULL AND completedOn IS NULL $orderBy")
    fun getScheduledTasksCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM task WHERE completedOn IS NOT NULL $orderBy")
    fun getCompletedTasksCount(): Flow<Int>

    @Query("SELECT * FROM task WHERE completedOn IS NULL AND title LIKE :keyword || '%' OR title LIKE '% ' || :keyword || '%' $orderBy")
    fun getTasksByKeyword(keyword: String): Flow<List<Task>>

    @Query("SELECT * FROM task WHERE completedOn IS NOT NULL AND title LIKE :keyword || '%' OR title LIKE '% ' || :keyword || '%' $orderBy")
    fun getCompletedTasksByKeyword(keyword: String): Flow<List<Task>>

    @Query("SELECT COUNT(*) FROM task WHERE completedOn IS NOT NULL AND title LIKE :keyword || '%' OR title LIKE '% ' || :keyword || '%'")
    fun getCompletedTasksByKeywordCount(keyword: String): Flow<Int>

    @Query("DELETE FROM task WHERE completedOn IS NOT NULL")
    suspend fun deleteCompletedTasks()
}