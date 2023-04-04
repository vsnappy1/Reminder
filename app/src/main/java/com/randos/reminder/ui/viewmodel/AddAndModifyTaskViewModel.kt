package com.randos.reminder.ui.viewmodel

import androidx.lifecycle.*
import com.randos.reminder.data.entity.Task
import com.randos.reminder.data.entity.TaskUiState
import com.randos.reminder.data.entity.toTask
import com.randos.reminder.data.repository.TaskRepository
import com.randos.reminder.data.repository.TaskRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddAndModifyTaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _uiState: MutableLiveData<TaskUiState> = MutableLiveData<TaskUiState>(TaskUiState())
    val uiState: LiveData<TaskUiState> = _uiState

    fun addTask() {
        viewModelScope.launch {
            uiState.value?.toTask()?.apply {
                taskRepository.insertTask(this)
            }
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