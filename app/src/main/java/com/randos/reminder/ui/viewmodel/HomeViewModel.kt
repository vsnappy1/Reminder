package com.randos.reminder.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.randos.reminder.data.repository.TaskRepository
import com.randos.reminder.ui.uiState.TaskUiState
import com.randos.reminder.ui.uiState.toTask
import com.randos.reminder.utils.toTaskUiStateList
import com.randos.reminder.widget.dataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
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

    private lateinit var job: Job
    fun setSearchText(text: String) {
        _homeUiState.value = _homeUiState.value?.copy(search = text)
        if (this::job.isInitialized && job.isActive) {
            job.cancel()
        }
        job = viewModelScope.launch {// This will create a debounce for searching
            delay(500)
            updateSearchResult()
        }
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

    fun updateTaskStatus(taskUiState: TaskUiState) {
        viewModelScope.launch {
            updateTaskStatus(taskUiState.toTask())
        }
    }

    fun updateUiState(uiState: HomeScreenUiState) {
        _homeUiState.value = uiState
    }

    suspend fun shouldShowNotificationRequestDialog(context: Context): Boolean {
        val count = 5

        val appStartCount = context.dataStore.data.map { preferences ->
            preferences[intPreferencesKey("app_start_count")] ?: 0
        }
        val result = appStartCount.first() == 0
        Log.d("--TAG", "shouldShowNotificationRequestDialog: ${appStartCount.first()} ${result}")

        context.dataStore.edit { settings ->
            if(settings[intPreferencesKey("app_start_count")] == 0){
                settings[intPreferencesKey("app_start_count")] = count
            }else{
                settings[intPreferencesKey("app_start_count")] =
                    settings[intPreferencesKey("app_start_count")]?.minus(
                        1
                    ) ?: count
            }
        }

        return result
    }
}