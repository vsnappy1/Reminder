package com.randos.reminder.ui.screen

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.randos.reminder.R
import com.randos.reminder.enums.ReminderScreen
import com.randos.reminder.navigation.NavigationDestination
import com.randos.reminder.ui.viewmodel.AllTaskViewModel

object TaskAllDestination: NavigationDestination {
    override val route: String = ReminderScreen.ALL_TASK_SCREEN.name
    override val titleRes: Int = R.string.all
}

@Composable
fun AllTaskScreen(
    viewModel: AllTaskViewModel = hiltViewModel(),
    onAddTaskClick: () -> Unit = {},
    onItemClick: (Long) -> Unit = {}
) {
}