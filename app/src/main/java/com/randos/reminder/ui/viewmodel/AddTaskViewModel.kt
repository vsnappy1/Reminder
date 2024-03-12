package com.randos.reminder.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.randos.reminder.data.repository.TaskRepository
import com.randos.reminder.ui.screen.TaskAddDestination
import com.randos.reminder.ui.uiState.TaskUiState
import com.randos.reminder.ui.uiState.toTask
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class AddTaskViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    taskRepository: TaskRepository
) : BaseViewModel(taskRepository) {

    private val _uiState: MutableLiveData<TaskUiState> = MutableLiveData<TaskUiState>(TaskUiState())
    val uiState: LiveData<TaskUiState> = _uiState

    init {
        // If user lands on add Task screen from today task we set the date as today
        val isToday: Boolean? = savedStateHandle[TaskAddDestination.isTodayArg]
        if (isToday == true) {
            setDateAsToday()
        }
    }

    fun addTask() {
        viewModelScope.launch {
            uiState.value?.copy(addedOn = LocalDateTime.now())?.toTask()?.let {
                addTask(it)
            }
        }
    }

    fun updateUiState(uiState: TaskUiState) {
        _uiState.value = uiState
    }

    private fun setDateAsToday() {
        _uiState.value = _uiState.value?.copy(
            date = LocalDate.now(),
            isDateChecked = true
        )
    }
}