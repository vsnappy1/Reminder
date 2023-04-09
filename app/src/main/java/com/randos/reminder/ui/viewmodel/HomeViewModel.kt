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
class HomeViewModel @Inject constructor(
    taskRepository: TaskRepository
) : BaseViewModel(taskRepository) {

    val tasks: LiveData<List<TaskUiState>> = taskRepository
        .getTasks()
        .map { it.toTaskUiStateList() }
        .asLiveData()

    val todayTaskCount          : LiveData<Int> = taskRepository.getTodayTasksCount(LocalDate.now()).asLiveData()
    val scheduledTasksCount     : LiveData<Int> = taskRepository.getScheduledTasksCount().asLiveData()
    val allTasksCount           : LiveData<Int> = taskRepository.getTasksCount().asLiveData()
    val completedTaskCount      : LiveData<Int> = taskRepository.getCompletedTasksCount().asLiveData()

}