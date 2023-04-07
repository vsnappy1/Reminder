package com.randos.reminder.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.randos.reminder.ui.screen.*
import com.randos.reminder.utils.toJson

@Composable
fun NavGraph(
    navController: NavHostController,
) {
    NavHost(
        navController = navController,
        startDestination = HomeDestination.route
    ) {

        composable(route = HomeDestination.route) {
            HomeScreen(
                onTodayClick = { navController.navigate(TaskTodayDestination.route) },
                onAllClick = { navController.navigate(TaskAllDestination.route) },
                onCompletedClick = { navController.navigate(TaskCompletedDestination.route) },
                onScheduledClick = { navController.navigate(TaskScheduledDestination.route) }
            )
        }

        composable(route = TaskTodayDestination.route) {
            TodayTaskScreen(
                onAddTaskClick = { navController.navigate(TaskAddDestination.route) },
                onItemClick = { navController.navigate("${TaskEditDestination.route}/${it}") }
            )
        }
        composable(route = TaskAllDestination.route) {
            AllTaskScreen(
                onAddTaskClick = { navController.navigate(TaskAddDestination.route) },
                onItemClick = { navController.navigate("${TaskEditDestination.route}/${it}") }
            )
        }

        composable(route = TaskCompletedDestination.route) {
            CompletedTaskScreen(
                onItemClick = { navController.navigate("${TaskEditDestination.route}/${it}") }
            )
        }


        composable(route = TaskScheduledDestination.route) {
            ScheduledTaskScreen(
                onAddTaskClick = { navController.navigate(TaskAddDestination.route) },
                onItemClick = { navController.navigate("${TaskEditDestination.route}/${it}") })
        }

        composable(route = TaskAddDestination.route) {
            AddTaskScreen(
                onAdd = { navController.popBackStack() },
                onCancel = { navController.popBackStack() },
            )
        }

        composable(
            route = TaskEditDestination.routeWithArgs,
            arguments = listOf(navArgument(TaskEditDestination.taskIdArg) {
                type = NavType.LongType
            })
        ) {
            EditTaskScreen(
                onSave = { navController.popBackStack() },
                onCancel = { navController.popBackStack() },
            )
        }
    }
}