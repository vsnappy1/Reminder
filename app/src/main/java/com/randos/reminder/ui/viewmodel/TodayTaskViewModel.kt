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
import java.time.LocalDate
import javax.inject.Inject

data class TodayTaskUiState(
    val dueTasks: List<TaskUiState> = listOf(),
    val todayTasks: List<TaskUiState> = listOf(),
    val isAllEmpty: Boolean = false
)

@HiltViewModel
class TodayTaskViewModel @Inject constructor(
    taskRepository: TaskRepository
) : BaseViewModel(taskRepository) {

    private val _uiState = MutableLiveData(TodayTaskUiState())
    val uiState: LiveData<TodayTaskUiState> = _uiState

    init {
        viewModelScope.launch {
            taskRepository
                .getTasksBefore(LocalDate.now())
                .map { it.toTaskUiStateList() }.collect {
                    _uiState.value =
                        _uiState.value?.copy(todayTasks = it, isAllEmpty = isAllEmpty())
                    updateIsAllEmpty()
                }
        }

        viewModelScope.launch {
            taskRepository
                .getTasksOn(LocalDate.now())
                .map { it.toTaskUiStateList() }.collect {
                    _uiState.value =
                        _uiState.value?.copy(todayTasks = it, isAllEmpty = isAllEmpty())
                    updateIsAllEmpty()
                }
        }
    }


    private fun updateIsAllEmpty() {
        _uiState.value = _uiState.value?.copy(isAllEmpty = isAllEmpty())
    }

    private fun isAllEmpty(): Boolean {
        _uiState.value?.apply {
            return dueTasks.isEmpty() && todayTasks.isEmpty()
        }
        return true
    }

    fun updateTaskStatus(taskUiState: TaskUiState) {
        viewModelScope.launch {
            updateTaskStatus(taskUiState.toTask())
        }
    }
}