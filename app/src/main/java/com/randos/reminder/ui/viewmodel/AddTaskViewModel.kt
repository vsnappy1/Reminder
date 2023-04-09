package com.randos.reminder.ui.viewmodel

import androidx.lifecycle.*
import com.randos.reminder.ui.uiState.TaskUiState
import com.randos.reminder.data.repository.TaskRepository
import com.randos.reminder.ui.uiState.toTask
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class AddTaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _uiState: MutableLiveData<TaskUiState> = MutableLiveData<TaskUiState>(TaskUiState())
    val uiState: LiveData<TaskUiState> = _uiState

    fun addTask() {
        viewModelScope.launch {
            uiState.value?.copy(addedOn = LocalDate.now())?.toTask()?.apply {
                taskRepository.insertTask(this)
            }
        }
    }

    fun updateUiState(uiState: TaskUiState) {
        _uiState.value = uiState
    }
}