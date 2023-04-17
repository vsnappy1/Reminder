@file:Suppress("unused", "unused", "unused")

package com.randos.reminder.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.randos.reminder.data.entity.Task
import com.randos.reminder.data.repository.TaskRepository
import com.randos.reminder.enums.RepeatCycle
import com.randos.reminder.notification.NotificationManager
import com.randos.reminder.notification.toNotificationData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

const val DELAY = 2000L
private const val TAG = "BaseViewModel"

@HiltViewModel
open class BaseViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {

    @Inject
    lateinit var notificationManager: NotificationManager

    private val jobMap = mutableMapOf<Long, Job>()

    suspend fun addTask(task: Task) {
        val id = taskRepository.insertTask(task)
        notificationManager.scheduleNotification(task.copy(id = id).toNotificationData())
    }

    suspend fun updateTask(task: Task) {
        taskRepository.updateTask(task)
        notificationManager.updateScheduledNotification(task.toNotificationData())
    }

    suspend fun updateTaskStatus(task: Task) {
        if (task.done) {
            markAsNotDone(task)
        } else {
            markAsDone(task)
        }
    }

    private suspend fun markAsDone(task: Task) {
        val job = viewModelScope.launch {
            taskRepository.updateTask(task.copy(done = true))
            delay(DELAY)
            taskRepository.updateTask(
                task.copy(
                    done = true,
                    completedOn = LocalDateTime.now(),
                    repeat = RepeatCycle.NO_REPEAT
                )
            )
            notificationManager.removeScheduledNotification(task.id.toInt())
            if (task.repeat != RepeatCycle.NO_REPEAT) {
                addTask(getTaskForNextCycle(task.copy(id = 0)))
            }
        }
        if (jobMap.containsKey(task.id)) {
            jobMap[task.id]?.apply {
                if (isActive) cancel()
            }
        }
        jobMap[task.id] = job
    }

    private suspend fun markAsNotDone(task: Task) {
        val job = viewModelScope.launch {
            taskRepository.updateTask(task.copy(done = false))
            delay(DELAY)
            taskRepository.updateTask(task.copy(done = false, completedOn = null))
            notificationManager.scheduleNotification(task.toNotificationData())
        }
        if (jobMap.containsKey(task.id)) {
            jobMap[task.id]?.apply {
                if (isActive) cancel()
            }
        }
        jobMap[task.id] = job
    }

    private fun getTaskForNextCycle(task: Task): Task {
        if (task.repeat == RepeatCycle.HOURLY) {
            return task.copy(time = task.time?.plusHours(1))
        }
        if (task.repeat == RepeatCycle.DAILY) {
            return task.copy(date = task.date?.plusDays(1))
        }
        if (task.repeat == RepeatCycle.WEEKLY) {
            return task.copy(date = task.date?.plusWeeks(1))
        }
        if (task.repeat == RepeatCycle.MONTHLY) {
            return task.copy(date = task.date?.plusMonths(1))
        }
        if (task.repeat == RepeatCycle.YEARLY) {
            return task.copy(date = task.date?.plusYears(1))
        }
        return task
    }
}