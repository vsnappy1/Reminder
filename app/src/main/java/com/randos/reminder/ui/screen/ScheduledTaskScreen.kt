package com.randos.reminder.ui.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.randos.reminder.R
import com.randos.reminder.enums.ReminderScreen
import com.randos.reminder.navigation.NavigationDestination
import com.randos.reminder.ui.component.BaseViewWithFAB
import com.randos.reminder.ui.component.NoTaskMessage
import com.randos.reminder.ui.component.TaskCard
import com.randos.reminder.ui.component.TimeFrameHeader
import com.randos.reminder.ui.theme.medium
import com.randos.reminder.ui.uiState.TaskUiState
import com.randos.reminder.ui.viewmodel.ScheduledTaskViewModel

object TaskScheduledDestination : NavigationDestination {
    override val route: String = ReminderScreen.SCHEDULED_TASK_SCREEN.name
    override val titleRes: Int = R.string.scheduled
}

@Composable
fun ScheduledTaskScreen(
    viewModel: ScheduledTaskViewModel = hiltViewModel(),
    onAddTaskClick: () -> Unit = {},
    onItemClick: (Long) -> Unit = {}
) {
    val pastDueTasks by viewModel.pastDueTasks.observeAsState(listOf())
    val todayTasks by viewModel.todayTasks.observeAsState(listOf())
    val tomorrowTasks by viewModel.tomorrowTasks.observeAsState(listOf())
    val thisWeekTasks by viewModel.thisWeekTasks.observeAsState(listOf())
    val upcomingTasks by viewModel.upcomingTasks.observeAsState(listOf())

    // Creating flat list of views because nested lazy columns are not supported
    val list = mutableListOf<@Composable () -> Unit>()
    if (pastDueTasks.isNotEmpty()) {
        list.add { TimeFrameHeader(titleRes = R.string.past_due) }
    }
    list.addAll(
        getListOfTaskCards(
            tasks = pastDueTasks,
            onItemClick = onItemClick,
            onDoneClick = { state -> viewModel.markDone(state) })
    )

    if (todayTasks.isNotEmpty()) {
        list.add { TimeFrameHeader(titleRes = R.string.today) }
    }
    list.addAll(
        getListOfTaskCards(
            tasks = todayTasks,
            onItemClick = onItemClick,
            onDoneClick = { state -> viewModel.markDone(state) })
    )

    if (tomorrowTasks.isNotEmpty()) {
        list.add { TimeFrameHeader(titleRes = R.string.tomorrow) }
    }
    list.addAll(
        getListOfTaskCards(
            tasks = tomorrowTasks,
            onItemClick = onItemClick,
            onDoneClick = { state -> viewModel.markDone(state) })
    )

    if (thisWeekTasks.isNotEmpty()) {
        list.add { TimeFrameHeader(titleRes = R.string.this_week) }
    }
    list.addAll(
        getListOfTaskCards(
            tasks = thisWeekTasks,
            onItemClick = onItemClick,
            onDoneClick = { state -> viewModel.markDone(state) })
    )

    if (upcomingTasks.isNotEmpty()) {
        list.add { TimeFrameHeader(titleRes = R.string.upcoming) }
    }
    list.addAll(
        getListOfTaskCards(
            tasks = upcomingTasks,
            onItemClick = onItemClick,
            onDoneClick = { state -> viewModel.markDone(state) })
    )

    BaseViewWithFAB(titleRes = R.string.scheduled, onAddTaskClick = onAddTaskClick) {
        LazyColumn(modifier = Modifier.padding(horizontal = medium)) {
            items(list) {
                it()
            }
        }
        if (pastDueTasks.isEmpty() &&
            todayTasks.isEmpty() &&
            tomorrowTasks.isEmpty() &&
            thisWeekTasks.isEmpty() &&
            upcomingTasks.isEmpty()
        ) {
            NoTaskMessage()
        }
    }
}

fun getListOfTaskCards(
    tasks: List<TaskUiState>,
    onItemClick: (Long) -> Unit,
    onDoneClick: (TaskUiState) -> Unit
): List<@Composable () -> Unit> {
    val list = mutableListOf<@Composable () -> Unit>()
    tasks.forEach {
        list.add {
            TaskCard(
                task = it,
                onItemClick = onItemClick,
                onDoneClick = onDoneClick
            )
        }
    }
    return list
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DefaultPreview() {
    ScheduledTaskScreen(
        viewModel()
    )
}