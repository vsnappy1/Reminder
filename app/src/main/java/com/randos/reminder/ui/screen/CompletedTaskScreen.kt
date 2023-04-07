package com.randos.reminder.ui.screen

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.randos.reminder.R
import com.randos.reminder.enums.ReminderScreen
import com.randos.reminder.navigation.NavigationDestination
import com.randos.reminder.ui.viewmodel.AllTaskViewModel
import com.randos.reminder.ui.viewmodel.CompletedTaskViewModel
import com.randos.reminder.ui.viewmodel.TodayTaskViewModel

object TaskCompletedDestination: NavigationDestination {
    override val route: String = ReminderScreen.COMPLETED_TASK_SCREEN.name
    override val titleRes: Int = R.string.completed
}
@Composable
fun CompletedTaskScreen(
    viewModel: CompletedTaskViewModel = hiltViewModel(),
    onItemClick: (Long) -> Unit = {}
) {
}