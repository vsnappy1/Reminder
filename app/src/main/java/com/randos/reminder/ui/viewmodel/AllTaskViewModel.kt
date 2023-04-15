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
import javax.inject.Inject

@HiltViewModel
class AllTaskViewModel @Inject constructor(
    taskRepository: TaskRepository
) : BaseViewModel(taskRepository) {

    val tasks: LiveData<List<TaskUiState>> = taskRepository
        .getTasks()
        .map { it.toTaskUiStateList() }
        .asLiveData()


    fun updateTaskStatus(taskUiState: TaskUiState) {
        viewModelScope.launch {
            updateTaskStatus(taskUiState.toTask())
        }
    }
}