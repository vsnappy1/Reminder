package com.randos.reminder.navigation

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.randos.reminder.ui.screen.*

@Composable
fun NavGraph(
    navController: NavHostController,
    activity: Activity
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
                onScheduledClick = { navController.navigate(TaskScheduledDestination.route) },
                onAddTaskClick = { navController.navigate(TaskAddDestination.route) },
                onSearchItemClick = { navController.navigate("${TaskEditDestination.route}/${it}") },
                onBackPress = { activity.finish() }
            )
        }

        composable(
            route = TaskTodayDestination.route,
            deepLinks = listOf(navDeepLink { uriPattern = "reminder://today/{taskId}" })
        ) {
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
                onDelete = { navController.popBackStack() },
            )
        }
    }
}