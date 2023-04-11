@file:Suppress("unused", "unused", "unused")

package com.randos.reminder.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.randos.reminder.data.repository.TaskRepository
import com.randos.reminder.enums.RepeatCycle
import com.randos.reminder.ui.uiState.TaskUiState
import com.randos.reminder.ui.uiState.toTask
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

const val DELAY = 3000L

@HiltViewModel
open class BaseViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val jobMap = mutableMapOf<Long, Job>()

    fun updateTaskStatus(taskUiState: TaskUiState) {
        viewModelScope.launch {
            if (taskUiState.done) {
                markAsNotDone(taskUiState)
            } else {
                markAsDone(taskUiState)
            }
        }
    }

    private suspend fun markAsDone(taskUiState: TaskUiState) {
        val job = viewModelScope.launch {
            taskRepository.updateTask(taskUiState.copy(done = true).toTask())
            delay(DELAY)
            taskRepository.updateTask(
                taskUiState.copy(
                    done = true,
                    completedOn = LocalDateTime.now(),
                    repeat = RepeatCycle.NO_REPEAT
                ).toTask()
            )
            if (taskUiState.repeat != RepeatCycle.NO_REPEAT) {
                // Add a new task for next cycle
                taskRepository.insertTask(getTaskForNextCycle(taskUiState.copy(id = 0)).toTask())
            }
        }
        if (jobMap.containsKey(taskUiState.id)) {
            jobMap[taskUiState.id]?.apply {
                if (isActive) cancel()
            }
        }
        jobMap[taskUiState.id] = job
    }

    private suspend fun markAsNotDone(taskUiState: TaskUiState) {
        val job = viewModelScope.launch {
            taskRepository.updateTask(taskUiState.copy(done = false).toTask())
            delay(DELAY)
            taskRepository.updateTask(taskUiState.copy(done = false, completedOn = null).toTask())
        }
        if (jobMap.containsKey(taskUiState.id)) {
            jobMap[taskUiState.id]?.apply {
                if (isActive) cancel()
            }
        }
        jobMap[taskUiState.id] = job
    }

    private fun getTaskForNextCycle(taskUiState: TaskUiState): TaskUiState {
        if (taskUiState.repeat == RepeatCycle.HOURLY) {
            return taskUiState.copy(time = taskUiState.time?.plusHours(1))
        }
        if (taskUiState.repeat == RepeatCycle.DAILY) {
            return taskUiState.copy(date = taskUiState.date?.plusDays(1))
        }
        if (taskUiState.repeat == RepeatCycle.WEEKLY) {
            return taskUiState.copy(date = taskUiState.date?.plusWeeks(1))
        }
        if (taskUiState.repeat == RepeatCycle.MONTHLY) {
            return taskUiState.copy(date = taskUiState.date?.plusMonths(1))
        }
        if (taskUiState.repeat == RepeatCycle.YEARLY) {
            return taskUiState.copy(date = taskUiState.date?.plusYears(1))
        }
        return taskUiState
    }
}