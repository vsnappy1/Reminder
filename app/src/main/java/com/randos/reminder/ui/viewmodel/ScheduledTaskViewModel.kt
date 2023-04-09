package com.randos.reminder.ui.viewmodel

import androidx.lifecycle.*
import com.randos.reminder.data.repository.TaskRepository
import com.randos.reminder.ui.uiState.TaskUiState
import com.randos.reminder.utils.toTaskUiStateList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ScheduledTaskViewModel @Inject constructor(taskRepository: TaskRepository) :
    BaseViewModel(taskRepository) {

    val pastDueTasks: LiveData<List<TaskUiState>> = taskRepository
        .getTasksBefore(LocalDate.now())
        .map { it.toTaskUiStateList() }
        .asLiveData()

    val todayTasks: LiveData<List<TaskUiState>> = taskRepository
        .getTasksOn(LocalDate.now())
        .map { it.toTaskUiStateList() }
        .asLiveData()

    val tomorrowTasks: LiveData<List<TaskUiState>> = taskRepository
        .getTasksOn(LocalDate.now().plusDays(1))
        .map { it.toTaskUiStateList() }
        .asLiveData()

    val thisWeekTasks: LiveData<List<TaskUiState>> = taskRepository
        .getTasksBetween(
            start = LocalDate.now().plusDays(2),
            end = LocalDate.now().plusDays((7 - LocalDate.now().dayOfWeek.value).toLong())
        )
        .map { it.toTaskUiStateList() }
        .asLiveData()

    val upcomingTasks: LiveData<List<TaskUiState>> = taskRepository
        .getTasksAfter(LocalDate.now().plusDays((7 - LocalDate.now().dayOfWeek.value).toLong()))
        .map { it.toTaskUiStateList() }
        .asLiveData()
}