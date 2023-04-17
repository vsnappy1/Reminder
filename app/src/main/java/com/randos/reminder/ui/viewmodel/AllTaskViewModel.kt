package com.randos.reminder.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.randos.reminder.data.repository.TaskRepository
import com.randos.reminder.ui.uiState.TaskUiState
import com.randos.reminder.ui.uiState.toTask
import com.randos.reminder.utils.toTaskUiStateList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject


data class AllTaskUiState(
    val tasks: List<TaskUiState> = listOf(),
    val isAllEmpty: Boolean = false
)

@HiltViewModel
class AllTaskViewModel @Inject constructor(
    taskRepository: TaskRepository
) : BaseViewModel(taskRepository) {

    private val _uiState: MutableLiveData<AllTaskUiState> = MutableLiveData(AllTaskUiState())
    val uiState: LiveData<AllTaskUiState> = _uiState

    init {
        viewModelScope.launch {
            taskRepository.getTasks().map { it.toTaskUiStateList() }.collect {
                _uiState.value = _uiState.value?.copy(tasks = it, isAllEmpty = it.isEmpty())
            }
        }
    }

    fun updateTaskStatus(taskUiState: TaskUiState) {
        viewModelScope.launch {
            updateTaskStatus(taskUiState.toTask())
        }
    }
}