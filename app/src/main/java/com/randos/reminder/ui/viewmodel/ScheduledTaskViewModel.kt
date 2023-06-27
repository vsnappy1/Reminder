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

data class ScheduledTaskUiState(
    val pastDueTasks: List<TaskUiState> = listOf(),
    val todayTasks: List<TaskUiState> = listOf(),
    val tomorrowTasks: List<TaskUiState> = listOf(),
    val thisWeekTasks: List<TaskUiState> = listOf(),
    val upcomingTasks: List<TaskUiState> = listOf(),
    val isAllEmpty: Boolean = false,
    val enterAnimationEnabled: Boolean = true
)

@HiltViewModel
class ScheduledTaskViewModel @Inject constructor(taskRepository: TaskRepository) :
    BaseViewModel(taskRepository) {

    private val _uiState = MutableLiveData(ScheduledTaskUiState())
    val uiState: LiveData<ScheduledTaskUiState> = _uiState

    init {
        viewModelScope.launch {
            taskRepository
                .getTasksBefore(LocalDate.now())
                .map { it.toTaskUiStateList() }.collect {
                    _uiState.value =
                        _uiState.value?.copy(pastDueTasks = it)
                    updateIsAllEmpty()
                }
        }

        viewModelScope.launch {
            taskRepository
                .getTasksOn(LocalDate.now())
                .map { it.toTaskUiStateList() }.collect {
                    _uiState.value =
                        _uiState.value?.copy(todayTasks = it)
                    updateIsAllEmpty()
                }
        }
        viewModelScope.launch {
            taskRepository
                .getTasksOn(LocalDate.now().plusDays(1))
                .map { it.toTaskUiStateList() }.collect {
                    _uiState.value =
                        _uiState.value?.copy(tomorrowTasks = it)
                    updateIsAllEmpty()
                }
        }
        viewModelScope.launch {
            taskRepository
                .getTasksBetween(
                    start = LocalDate.now().plusDays(2),
                    end = LocalDate.now().plusDays((7 - LocalDate.now().dayOfWeek.value).toLong())
                )
                .map { it.toTaskUiStateList() }.collect {
                    _uiState.value =
                        _uiState.value?.copy(thisWeekTasks = it)
                    updateIsAllEmpty()
                }
        }
        viewModelScope.launch {
            taskRepository
                .getTasksAfter(
                    LocalDate.now().plusDays((8 - LocalDate.now().dayOfWeek.value).toLong())
                )
                .map { it.toTaskUiStateList() }.collect {
                    _uiState.value =
                        _uiState.value?.copy(upcomingTasks = it)
                    updateIsAllEmpty()
                }
        }
    }

    private fun updateIsAllEmpty() {
        _uiState.value = _uiState.value?.copy(isAllEmpty = isAllEmpty())
    }

    private fun isAllEmpty(): Boolean {
        _uiState.value?.apply {
            return pastDueTasks.isEmpty() && todayTasks.isEmpty() && tomorrowTasks.isEmpty() && thisWeekTasks.isEmpty() && upcomingTasks.isEmpty()
        }
        return true
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