package com.randos.reminder.ui.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.randos.reminder.R
import com.randos.reminder.enums.ReminderScreen
import com.randos.reminder.navigation.NavigationDestination
import com.randos.reminder.ui.component.BaseViewWithFAB
import com.randos.reminder.ui.component.NoTaskMessage
import com.randos.reminder.ui.component.TaskCard
import com.randos.reminder.ui.theme.medium
import com.randos.reminder.ui.viewmodel.TodayTaskUiState
import com.randos.reminder.ui.viewmodel.TodayTaskViewModel

object TaskTodayDestination : NavigationDestination {
    override val route: String = ReminderScreen.TODAY_TASK_SCREEN.name
    override val titleRes: Int = R.string.today
}

@Composable
fun TodayTaskScreen(
    viewModel: TodayTaskViewModel = hiltViewModel(),
    onAddTaskClick: () -> Unit = {},
    onItemClick: (Long) -> Unit = {}
) {
    val uiState by viewModel.uiState.observeAsState(TodayTaskUiState())
    BaseViewWithFAB(titleRes = R.string.today, onAddTaskClick = onAddTaskClick) {
        LazyColumn(modifier = Modifier.padding(medium)) {
            items(uiState.dueTasks) {
                TaskCard(
                    task = it,
                    onItemClick = onItemClick,
                    onDoneClick = { state -> viewModel.updateTaskStatus(state) },
                    visible = !it.done
                )
            }
            items(uiState.todayTasks) {
                TaskCard(
                    task = it,
                    onItemClick = onItemClick,
                    onDoneClick = { state -> viewModel.updateTaskStatus(state) },
                    isDateVisible = false,
                    visible = !it.done
                )
            }
        }
        FadeAnimatedVisibility (uiState.isAllEmpty) {
            NoTaskMessage()
        }
    }
}
