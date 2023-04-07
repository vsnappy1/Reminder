package com.randos.reminder.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.hilt.navigation.compose.hiltViewModel
import com.randos.reminder.R
import com.randos.reminder.enums.ReminderScreen
import com.randos.reminder.navigation.NavigationDestination
import com.randos.reminder.ui.component.BaseView
import com.randos.reminder.ui.component.ListOfTasks
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
    val tasks by viewModel.tasks.observeAsState(listOf())
    BaseView(titleRes = R.string.today, onAddTaskClick = onAddTaskClick) {
        ListOfTasks(tasks = tasks, onItemClick = onItemClick, onDoneClick = {
            viewModel.markDone(it)
        })
    }

}