package com.randos.reminder.data.repository

import androidx.room.Query
import com.randos.reminder.data.dao.TaskDao
import com.randos.reminder.data.entity.Task
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepositoryImpl @Inject constructor(private val taskDao: TaskDao) : TaskRepository {

    override suspend fun insertTask(task: Task) = taskDao.insert(task)

    override suspend fun updateTask(task: Task) = taskDao.update(task)

    override suspend fun deleteTask(task: Task) = taskDao.delete(task)

    override fun getTask(id: Long): Flow<Task> = taskDao.getTask(id)

    override fun getTasks(): Flow<List<Task>> = taskDao.getTasks(done = false)

    override fun getTasksOn(date: LocalDate): Flow<List<Task>> =
        taskDao.getTasks(date, false)

    override fun getTasksBetween(start: LocalDate, end: LocalDate): Flow<List<Task>> =
        taskDao.getTasks(start, end, false)

    override fun getTasksAfter(date: LocalDate): Flow<List<Task>> =
        taskDao.getTasksAfter(date, false)

    override fun getTasksBefore(date: LocalDate): Flow<List<Task>> =
        taskDao.getTasksBefore(date, false)

    override fun getCompletedTasks() = taskDao.getTasks(done = true)

    override fun getCompletedTasksOn(date: LocalDate): Flow<List<Task>> =
        taskDao.getTasks(date, true)

    override fun getCompletedTasksBetween(start: LocalDate, end: LocalDate): Flow<List<Task>> =
        taskDao.getTasks(start, end, true)

    override fun getCompletedTasksAfter(date: LocalDate): Flow<List<Task>> =
        taskDao.getTasksAfter(date, true)

    override fun getCompletedTasksBefore(date: LocalDate): Flow<List<Task>> =
        taskDao.getTasksBefore(date, true)
}