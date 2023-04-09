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
import com.randos.reminder.ui.component.*
import com.randos.reminder.ui.theme.medium
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
    val dueTasks by viewModel.dueTasks.observeAsState(listOf())
    val todayTasks by viewModel.todayTasks.observeAsState(listOf())
    BaseViewWithFAB(titleRes = R.string.today, onAddTaskClick = onAddTaskClick) {
        LazyColumn(modifier = Modifier.padding(medium)) {
            items(dueTasks) {
                TaskCard(
                    task = it,
                    onItemClick = onItemClick,
                    onDoneClick = { state -> viewModel.updateTaskStatus(state) }
                )
            }
            items(todayTasks) {
                TaskCard(
                    task = it,
                    onItemClick = onItemClick,
                    onDoneClick = { state -> viewModel.updateTaskStatus(state) },
                    isDateVisible = false
                )
            }
        }
        if (todayTasks.isEmpty() && dueTasks.isEmpty()) {
            NoTaskMessage()
        }
    }
}