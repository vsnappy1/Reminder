package com.randos.reminder.data.repository

import com.randos.reminder.data.dao.TaskDao
import com.randos.reminder.data.entity.Task
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepositoryImpl @Inject constructor(private val taskDao: TaskDao) : TaskRepository {

    override suspend fun insertTask(task: Task) = taskDao.insert(task)

    override suspend fun updateTask(task: Task) = taskDao.update(task)

    override suspend fun deleteTask(task: Task) = taskDao.delete(task)

    override fun getTask(id: Long): Flow<Task> = taskDao.getTask(id)

    override fun getTasks(): Flow<List<Task>> = taskDao.getTasks()
}