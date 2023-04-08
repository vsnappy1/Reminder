package com.randos.reminder.ui.viewmodel

import androidx.lifecycle.*
import com.randos.reminder.ui.uiState.TaskUiState
import com.randos.reminder.data.repository.TaskRepository
import com.randos.reminder.ui.screen.TaskEditDestination
import com.randos.reminder.ui.uiState.toTask
import com.randos.reminder.ui.uiState.toTaskUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditTaskViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _uiState: MutableLiveData<TaskUiState> = MutableLiveData<TaskUiState>(TaskUiState())
    val uiState: LiveData<TaskUiState> = _uiState


    init {
        val taskId: Long = checkNotNull(savedStateHandle[TaskEditDestination.taskIdArg])
        viewModelScope.launch {
            _uiState.value = taskRepository.getTask(taskId).first().toTaskUiState()
        }
    }

    fun updateTask() {
        viewModelScope.launch {
            uiState.value?.toTask()?.apply {
                taskRepository.updateTask(this)
            }
        }
    }

    fun updateUiState(uiState: TaskUiState) {
        _uiState.value = uiState
    }
}