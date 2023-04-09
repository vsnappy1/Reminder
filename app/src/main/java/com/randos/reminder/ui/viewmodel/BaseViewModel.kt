@file:Suppress("unused", "unused", "unused")

package com.randos.reminder.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.randos.reminder.data.repository.TaskRepository
import com.randos.reminder.enums.RepeatCycle
import com.randos.reminder.ui.uiState.TaskUiState
import com.randos.reminder.ui.uiState.toTask
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@Suppress("unused", "unused", "unused")
@HiltViewModel
open class BaseViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {

    fun markDone(taskUiState: TaskUiState) {
        viewModelScope.launch {
            taskRepository.updateTask(
                taskUiState.copy(
                    done = true,
                    completedOn = LocalDate.now(),
                    repeat = RepeatCycle.NO_REPEAT
                ).toTask()
            )
            if (taskUiState.repeat != RepeatCycle.NO_REPEAT) {
                // Add a new task
                addTask(getNewTask(taskUiState.copy(id = 0)))
            }
        }
    }

    private fun getNewTask(taskUiState: TaskUiState): TaskUiState {
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

    fun markNotDone(taskUiState: TaskUiState) {
        viewModelScope.launch {
            taskRepository.updateTask(
                taskUiState.copy(done = false, completedOn = LocalDate.now()).toTask()
            )
        }
    }


    private fun addTask(taskUiState: TaskUiState) {
        viewModelScope.launch {
            taskRepository.insertTask(taskUiState.toTask())
        }
    }
}