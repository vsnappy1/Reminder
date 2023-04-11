package com.randos.reminder.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.randos.reminder.data.repository.TaskRepository
import com.randos.reminder.ui.uiState.TaskUiState
import com.randos.reminder.utils.toTaskUiStateList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class HomeScreenUiState(
    val todayTaskCount: Int = 0,
    val scheduledTaskCount: Int = 0,
    val allTaskCount: Int = 0,
    val completedTaskCount: Int = 0,
    val doesSearchHasFocus: Boolean = false,
    val search: String = "",
    val filteredTasks: List<TaskUiState> = listOf(),
    val filteredCompletedTasks: List<TaskUiState> = listOf(),
    val filteredCompletedTasksCount: Int = 0,
    val isFilteredCompletedTasksVisible: Boolean = false
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    val taskRepository: TaskRepository
) : BaseViewModel(taskRepository) {

    private val _homeUiState = MutableLiveData(HomeScreenUiState())
    val homeUiState: LiveData<HomeScreenUiState> = _homeUiState

    init {
        viewModelScope.launch {
            taskRepository.getTodayTasksCount(LocalDate.now()).collect {
                _homeUiState.value = _homeUiState.value?.copy(todayTaskCount = it)
            }
        }
        viewModelScope.launch {
            taskRepository.getScheduledTasksCount().collect {
                _homeUiState.value = _homeUiState.value?.copy(scheduledTaskCount = it)
            }
        }
        viewModelScope.launch {
            taskRepository.getTasksCount().collect {
                _homeUiState.value = _homeUiState.value?.copy(allTaskCount = it)
            }
        }
        viewModelScope.launch {
            taskRepository.getCompletedTasksCount().collect {
                _homeUiState.value = _homeUiState.value?.copy(completedTaskCount = it)
            }
        }
    }

    fun setDoesSearchHasFocus(hasFocus: Boolean) {
        _homeUiState.value = _homeUiState.value?.copy(doesSearchHasFocus = hasFocus)
    }

    fun setSearchText(text: String) {
        _homeUiState.value = _homeUiState.value?.copy(search = text)
        updateSearchResult()
    }

    fun setIsCompletedTasksVisible(isVisible: Boolean) {
        _homeUiState.value = _homeUiState.value?.copy(isFilteredCompletedTasksVisible = isVisible)
        updateSearchResult()
    }

    private fun updateSearchResult() {
        if (_homeUiState.value?.search?.isEmpty() == true) {
            _homeUiState.value =
                _homeUiState.value?.copy(
                    filteredTasks = listOf(),
                    filteredCompletedTasks = listOf(),
                    filteredCompletedTasksCount = 0
                )
        } else {
            viewModelScope.launch {
                taskRepository.getTasksByKeyword(homeUiState.value?.search ?: "")
                    .map { it.toTaskUiStateList() }.collect {
                        _homeUiState.value = _homeUiState.value?.copy(filteredTasks = it)
                    }
            }
            viewModelScope.launch {
                taskRepository.getCompletedTasksByKeywordCount(homeUiState.value?.search ?: "")
                    .collect {
                        _homeUiState.value =
                            _homeUiState.value?.copy(filteredCompletedTasksCount = it)
                    }
            }
            if (_homeUiState.value?.isFilteredCompletedTasksVisible == true) {
                viewModelScope.launch {
                    taskRepository.getCompletedTasksByKeyword(homeUiState.value?.search ?: "")
                        .map { it.toTaskUiStateList() }.collect {
                            _homeUiState.value =
                                _homeUiState.value?.copy(filteredCompletedTasks = it)
                        }
                }
            }
        }
    }
}