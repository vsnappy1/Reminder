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
import com.randos.reminder.ui.viewmodel.ScheduledTaskUiState
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
    val uiState by viewModel.uiState.observeAsState(ScheduledTaskUiState())
    // Creating flat list of views because nested lazy columns are not supported
    val list = mutableListOf<@Composable () -> Unit>()
    if (uiState.pastDueTasks.isNotEmpty()) {
        list.add { TimeFrameHeader(titleRes = R.string.past_due) }
    }
    list.addAll(
        getListOfTaskCards(
            tasks = uiState.pastDueTasks,
            onItemClick = onItemClick,
            onDoneClick = { state -> viewModel.updateTaskStatus(state) })
    )

    if (uiState.todayTasks.isNotEmpty()) {
        list.add { TimeFrameHeader(titleRes = R.string.today) }
    }
    list.addAll(
        getListOfTaskCards(
            tasks = uiState.todayTasks,
            onItemClick = onItemClick,
            onDoneClick = { state -> viewModel.updateTaskStatus(state) },
            isDateVisible = false
        )
    )

    if (uiState.tomorrowTasks.isNotEmpty()) {
        list.add { TimeFrameHeader(titleRes = R.string.tomorrow) }
    }
    list.addAll(
        getListOfTaskCards(
            tasks = uiState.tomorrowTasks,
            onItemClick = onItemClick,
            onDoneClick = { state -> viewModel.updateTaskStatus(state) },
            isDateVisible = false
        )
    )

    if (uiState.thisWeekTasks.isNotEmpty()) {
        list.add { TimeFrameHeader(titleRes = R.string.this_week) }
    }
    list.addAll(
        getListOfTaskCards(
            tasks = uiState.thisWeekTasks,
            onItemClick = onItemClick,
            onDoneClick = { state -> viewModel.updateTaskStatus(state) })
    )

    if (uiState.upcomingTasks.isNotEmpty()) {
        list.add { TimeFrameHeader(titleRes = R.string.upcoming) }
    }
    list.addAll(
        getListOfTaskCards(
            tasks = uiState.upcomingTasks,
            onItemClick = onItemClick,
            onDoneClick = { state -> viewModel.updateTaskStatus(state) })
    )

    BaseViewWithFAB(titleRes = R.string.scheduled, onAddTaskClick = onAddTaskClick) {
        LazyColumn(modifier = Modifier.padding(horizontal = medium)) {
            items(list) {
                it()
            }
        }
        FadeAnimatedVisibility (uiState.isAllEmpty) {
            NoTaskMessage()
        }
    }
}

private fun getListOfTaskCards(
    tasks: List<TaskUiState>,
    onItemClick: (Long) -> Unit,
    onDoneClick: (TaskUiState) -> Unit,
    isDateVisible: Boolean = true,
    isTimeVisible: Boolean = true,
    isRepeatVisible: Boolean = true,
): List<@Composable () -> Unit> {
    val list = mutableListOf<@Composable () -> Unit>()
    tasks.forEach {
        list.add {
            TaskCard(
                task = it,
                onItemClick = onItemClick,
                onDoneClick = onDoneClick,
                isDateVisible = isDateVisible,
                isTimeVisible = isTimeVisible,
                isRepeatVisible = isRepeatVisible,
                visible = !it.done
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