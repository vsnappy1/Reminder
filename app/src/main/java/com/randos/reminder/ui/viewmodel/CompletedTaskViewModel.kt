package com.randos.reminder.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.randos.reminder.data.repository.TaskRepository
import com.randos.reminder.ui.uiState.TaskUiState
import com.randos.reminder.ui.uiState.toTask
import com.randos.reminder.utils.toTaskUiStateList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class CompletedTaskViewModel @Inject constructor(
    val taskRepository: TaskRepository
) : BaseViewModel(taskRepository) {
    fun deleteAll() {
        viewModelScope.launch {
            taskRepository.deleteCompletedTasks()
        }
    }

    fun updateTaskStatus(taskUiState: TaskUiState) {
        viewModelScope.launch {
            updateTaskStatus(taskUiState.toTask())
        }
    }

    val todayTasks: LiveData<List<TaskUiState>> = taskRepository
        .getCompletedTasksOn(LocalDate.now())
        .map { it.toTaskUiStateList() }
        .asLiveData()

    val yesterdayTasks: LiveData<List<TaskUiState>> = taskRepository
        .getCompletedTasksOn(LocalDate.now().minusDays(1))
        .map { it.toTaskUiStateList() }
        .asLiveData()

    val lastSevenDaysTasks: LiveData<List<TaskUiState>> = taskRepository
        .getCompletedTasksBetween(
            start = LocalDate.now().minusDays(8),
            end = LocalDate.now().minusDays(2)
        )
        .map { it.toTaskUiStateList() }
        .asLiveData()

    val allOtherTasks: LiveData<List<TaskUiState>> = taskRepository
        .getCompletedTasksBefore(LocalDate.now().minusDays(8))
        .map { it.toTaskUiStateList() }
        .asLiveData()

    val completedTaskCount = taskRepository.getCompletedTasksCount().asLiveData()
}