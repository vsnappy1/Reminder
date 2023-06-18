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

data class CompletedTaskUiState(
    val todayTasks: List<TaskUiState> = listOf(),
    val yesterdayTasks: List<TaskUiState> = listOf(),
    val lastSevenDaysTasks: List<TaskUiState> = listOf(),
    val previousTasks: List<TaskUiState> = listOf(),
    val completedTaskCount: Int = 0,
    val isAllEmpty: Boolean = false,
    val enterAnimationEnabled: Boolean = true
)

@HiltViewModel
class CompletedTaskViewModel @Inject constructor(
    val taskRepository: TaskRepository
) : BaseViewModel(taskRepository) {

    private val _uiState = MutableLiveData(CompletedTaskUiState())
    val uiState: LiveData<CompletedTaskUiState> = _uiState

    init {
        viewModelScope.launch {
            taskRepository
                .getCompletedTasksOn(LocalDate.now())
                .map { it.toTaskUiStateList() }.collect {
                    _uiState.value =
                        _uiState.value?.copy(todayTasks = it)
                    updateIsAllEmpty()
                }
        }

        viewModelScope.launch {
            taskRepository
                .getCompletedTasksOn(LocalDate.now().minusDays(1))
                .map { it.toTaskUiStateList() }.collect {
                    _uiState.value =
                        _uiState.value?.copy(yesterdayTasks = it)
                    updateIsAllEmpty()
                }
        }

        viewModelScope.launch {
            taskRepository
                .getCompletedTasksBetween(
                    start = LocalDate.now().minusDays(8),
                    end = LocalDate.now().minusDays(2)
                )
                .map { it.toTaskUiStateList() }.collect {
                    _uiState.value =
                        _uiState.value?.copy(lastSevenDaysTasks = it)
                    updateIsAllEmpty()
                }
        }
        viewModelScope.launch {
            taskRepository
                .getCompletedTasksBefore(LocalDate.now().minusDays(8))
                .map { it.toTaskUiStateList() }.collect {
                    _uiState.value =
                        _uiState.value?.copy(previousTasks = it)
                    updateIsAllEmpty()
                }
        }
        viewModelScope.launch {
            taskRepository.getCompletedTasksCount().collect {
                _uiState.value =
                    _uiState.value?.copy(completedTaskCount = it)
                updateIsAllEmpty()
            }
        }
    }

    private fun updateIsAllEmpty() {
        _uiState.value = _uiState.value?.copy(isAllEmpty = isAllEmpty())
    }

    private fun isAllEmpty(): Boolean {
        _uiState.value?.apply {
            return todayTasks.isEmpty() && yesterdayTasks.isEmpty() && lastSevenDaysTasks.isEmpty() && previousTasks.isEmpty()
        }
        return true
    }

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

    fun updateEnterAnimationEnabled(enabled: Boolean){
        _uiState.value = _uiState.value?.copy(enterAnimationEnabled = enabled)
    }
}