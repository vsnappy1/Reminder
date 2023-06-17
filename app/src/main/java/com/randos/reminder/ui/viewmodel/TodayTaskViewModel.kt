package com.randos.reminder.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.randos.reminder.data.repository.TaskRepository
import com.randos.reminder.ui.screen.TaskEditDestination
import com.randos.reminder.ui.uiState.TaskUiState
import com.randos.reminder.ui.uiState.toTask
import com.randos.reminder.utils.toTaskUiStateList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class TodayTaskUiState(
    val dueTasks: List<TaskUiState> = listOf(),
    val todayTasks: List<TaskUiState> = listOf(),
    val isAllEmpty: Boolean = false,
    val indexTaskId: Int? = null,
    val scrollToPosition: Int? = null,
    val enterAnimationEnabled: Boolean = true
)

private const val TAG = "TodayTaskViewModel"

@HiltViewModel
class TodayTaskViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    taskRepository: TaskRepository
) : BaseViewModel(taskRepository) {

    private val _uiState = MutableLiveData(TodayTaskUiState())
    val uiState: LiveData<TodayTaskUiState> = _uiState
    private val taskId: String? = savedStateHandle[TaskEditDestination.taskIdArg]

    init {
        viewModelScope.launch {
            taskRepository
                .getTasksBefore(LocalDate.now())
                .map { it.toTaskUiStateList() }.collect {
                    _uiState.value =
                        _uiState.value?.copy(dueTasks = it)
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
            val tasks = taskRepository.getTasksTodayAndBackward(LocalDate.now()).first()
            if (taskId != null) {
                val id = taskId.toInt()
                for (i in tasks.indices) {
                    if (id == tasks[i].id.toInt()) {
                        _uiState.value =
                            _uiState.value?.copy(scrollToPosition = i, indexTaskId = taskId.toInt())
                    }
                }
            }
            Log.d(TAG, ": position: ${_uiState.value?.scrollToPosition}")
            cancel()
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

    fun endTaskIndexing(){
        _uiState.value = _uiState.value?.copy(scrollToPosition = null, indexTaskId = null)
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