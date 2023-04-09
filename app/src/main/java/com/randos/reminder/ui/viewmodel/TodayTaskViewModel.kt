package com.randos.reminder.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.randos.reminder.data.repository.TaskRepository
import com.randos.reminder.ui.uiState.TaskUiState
import com.randos.reminder.utils.toTaskUiStateList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class TodayTaskViewModel @Inject constructor(
    taskRepository: TaskRepository
) : BaseViewModel(taskRepository) {

    val todayTasks: LiveData<List<TaskUiState>> = taskRepository
        .getTasksOn(LocalDate.now())
        .map { it.toTaskUiStateList() }
        .asLiveData()

    val dueTasks: LiveData<List<TaskUiState>> = taskRepository
        .getTasksBefore(LocalDate.now())
        .map { it.toTaskUiStateList() }
        .asLiveData()
}