package com.randos.reminder.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.randos.reminder.enums.ReminderScreen
import com.randos.reminder.ui.screen.AddAndModifyTaskScreen
import com.randos.reminder.ui.screen.ReminderListScreen
import com.randos.reminder.ui.viewmodel.AddAndModifyTaskViewModel

private const val TAG = "Navigation"

@Composable
fun NavGraph(
    navController: NavHostController,
    addAndModifyTaskViewModel: AddAndModifyTaskViewModel
) {
    NavHost(
        navController = navController,
        startDestination = ReminderScreen.REMINDER_LIST.name
    ) {
        composable(route = ReminderScreen.REMINDER_LIST.name) {
            ReminderListScreen(
                tasks = listOf(),
                onAddTaskClick = {
                    navController.navigate(ReminderScreen.ADD_AND_MODIFY_TASK_SCREEN.name)
                })
        }
        composable(route = ReminderScreen.ADD_AND_MODIFY_TASK_SCREEN.name) {
            AddAndModifyTaskScreen(
                onCancel = { navController.popBackStack() },
                onAdd = { navController.popBackStack() },
                onSave = { navController.popBackStack() },
                viewModel = addAndModifyTaskViewModel
            )
        }
    }
}