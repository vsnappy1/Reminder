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
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

const val DELAY = 2000L
private const val TAG = "BaseViewModel"

@HiltViewModel
open class BaseViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {

    companion object {
        var isDataModified = false
    }

    @Inject
    lateinit var notificationManager: NotificationManager

    // This job map helps to create delay when task is marked complete/incomplete
    private val jobMap = mutableMapOf<Int, Job>()

    @OptIn(DelicateCoroutinesApi::class)
    fun addTask(task: Task) {
        GlobalScope.launch {// Added some delay to make app feel more user friendly
            delay(500)
            val id = taskRepository.insertTask(task).toInt()
            notificationManager.scheduleNotification(task.copy(id = id).toNotificationData())
        }
        isDataModified = true
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            val oldTask = taskRepository.getTask(task.id).first()
            if (hasTimeChanged(task, oldTask)) {
                taskRepository.updateNotificationTriggeredStatus(task.id, false)
            }
            taskRepository.updateTask(task)
            notificationManager.updateScheduledNotification(task.toNotificationData())
        }
        isDataModified = true
    }

    private fun hasTimeChanged(taskNew: Task, oldTask: Task): Boolean {
        return taskNew.date != oldTask.date || taskNew.time != oldTask.time
    }

    fun updateTaskStatus(task: Task) {
        if (task.done) {
            markAsNotDone(task)
        } else {
            markAsDone(task)
        }
        isDataModified = true
    }

    private fun markAsDone(task: Task) {
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

    private fun markAsNotDone(task: Task) {
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