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
import com.randos.reminder.ui.component.BaseView
import com.randos.reminder.ui.component.NoTaskMessage
import com.randos.reminder.ui.component.TimeFrameHeader
import com.randos.reminder.ui.theme.medium
import com.randos.reminder.ui.viewmodel.CompletedTaskViewModel

object TaskCompletedDestination : NavigationDestination {
    override val route: String = ReminderScreen.COMPLETED_TASK_SCREEN.name
    override val titleRes: Int = R.string.completed
}

@Composable
fun CompletedTaskScreen(
    viewModel: CompletedTaskViewModel = hiltViewModel(),
    onItemClick: (Long) -> Unit = {}
) {
    val todayTasks by viewModel.todayTasks.observeAsState(listOf())
    val yesterdayTasks by viewModel.yesterdayTasks.observeAsState(listOf())
    val lastSevenDaysTasks by viewModel.lastSevenDaysTasks.observeAsState(listOf())
    val previousTasks by viewModel.allOtherTasks.observeAsState(listOf())

    // Creating flat list of views because nested lazy columns are not supported
    val list = mutableListOf<@Composable () -> Unit>()

    if (todayTasks.isNotEmpty()) {
        list.add { TimeFrameHeader(titleRes = R.string.today) }
    }
    list.addAll(
        getListOfTaskCards(
            tasks = todayTasks,
            onItemClick = onItemClick,
            onDoneClick = { state -> viewModel.updateTaskStatus(state) }
        )
    )

    if (yesterdayTasks.isNotEmpty()) {
        list.add { TimeFrameHeader(titleRes = R.string.yesterday) }
    }
    list.addAll(
        getListOfTaskCards(
            tasks = yesterdayTasks,
            onItemClick = onItemClick,
            onDoneClick = { state -> viewModel.updateTaskStatus(state) })
    )

    if (lastSevenDaysTasks.isNotEmpty()) {
        list.add { TimeFrameHeader(titleRes = R.string.last_seven_days) }
    }
    list.addAll(
        getListOfTaskCards(
            tasks = lastSevenDaysTasks,
            onItemClick = onItemClick,
            onDoneClick = { state -> viewModel.updateTaskStatus(state) })
    )

    if (previousTasks.isNotEmpty()) {
        list.add { TimeFrameHeader(titleRes = R.string.previous) }
    }
    list.addAll(
        getListOfTaskCards(
            tasks = previousTasks,
            onItemClick = onItemClick,
            onDoneClick = { state -> viewModel.updateTaskStatus(state) })
    )

    BaseView(titleRes = R.string.completed) {
        LazyColumn(modifier = Modifier.padding(horizontal = medium)) {
            items(list) {
                it()
            }
        }
        if (
            todayTasks.isEmpty() &&
            yesterdayTasks.isEmpty() &&
            lastSevenDaysTasks.isEmpty() &&
            previousTasks.isEmpty()
        ) {
            NoTaskMessage()
        }
    }
}