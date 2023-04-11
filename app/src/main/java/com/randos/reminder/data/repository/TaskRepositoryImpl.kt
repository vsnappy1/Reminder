package com.randos.reminder.data.repository

import com.randos.reminder.data.dao.TaskDao
import com.randos.reminder.data.entity.Task
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepositoryImpl @Inject constructor(private val taskDao: TaskDao) : TaskRepository {

    override suspend fun insertTask(task: Task) =
        taskDao.insert(task)

    override suspend fun updateTask(task: Task) =
        taskDao.update(task)

    override suspend fun deleteTask(task: Task) =
        taskDao.delete(task)

    override fun getTask(id: Long): Flow<Task> =
        taskDao.getTask(id)

    override fun getTasks(): Flow<List<Task>> =
        taskDao.getTasks()

    override fun getTasksOn(date: LocalDate): Flow<List<Task>> =
        taskDao.getTasks(date)

    override fun getTasksBetween(start: LocalDate, end: LocalDate): Flow<List<Task>> =
        taskDao.getTasks(start, end)

    override fun getTasksAfter(date: LocalDate): Flow<List<Task>> =
        taskDao.getTasksAfter(date)

    override fun getTasksBefore(date: LocalDate): Flow<List<Task>> =
        taskDao.getTasksBefore(date)

    override fun getCompletedTasks() =
        taskDao.getCompletedTasks()

    override fun getCompletedTasksOn(date: LocalDate): Flow<List<Task>> =
        taskDao.getCompletedTasks(date)

    override fun getCompletedTasksBetween(start: LocalDate, end: LocalDate): Flow<List<Task>> =
        taskDao.getCompletedTasks(start, end)

    override fun getCompletedTasksAfter(date: LocalDate): Flow<List<Task>> =
        taskDao.getCompletedTasksAfter(date)

    override fun getCompletedTasksBefore(date: LocalDate): Flow<List<Task>> =
        taskDao.getCompletedTasksBefore(date)

    override fun getTasksCount(): Flow<Int> =
        taskDao.getTasksCount()

    override fun getTodayTasksCount(date: LocalDate): Flow<Int> =
        taskDao.getTodayTasksCount(date)

    override fun getScheduledTasksCount(): Flow<Int> =
        taskDao.getScheduledTasksCount()

    override fun getCompletedTasksCount(): Flow<Int> =
        taskDao.getCompletedTasksCount()

    override fun getTasksByKeyword(keyword: String): Flow<List<Task>> =
        taskDao.getTasksByKeyword(keyword)

    override fun getCompletedTasksByKeyword(keyword: String): Flow<List<Task>> =
        taskDao.getCompletedTasksByKeyword(keyword)

    override fun getCompletedTasksByKeywordCount(keyword: String): Flow<Int> =
        taskDao.getCompletedTasksByKeywordCount(keyword)

    override suspend fun deleteCompletedTasks() =
        taskDao.deleteCompletedTasks()
}