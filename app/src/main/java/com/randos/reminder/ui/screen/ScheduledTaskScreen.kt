package com.randos.reminder.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
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
import com.randos.reminder.ui.component.BaseView
import com.randos.reminder.ui.component.TimeFrame
import com.randos.reminder.ui.theme.grey
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
    val allOtherTasks by viewModel.allOtherTasks.observeAsState(listOf())

    BaseView(titleRes = R.string.scheduled, onAddTaskClick = onAddTaskClick) {
        val timeFrames = listOf(
            R.string.past_due,
            R.string.today,
            R.string.tomorrow,
            R.string.this_week,
            R.string.all_other
        )
        LazyColumn(
            modifier = Modifier
                .background(grey)
                .fillMaxSize()
        ) {
            items(items = timeFrames, itemContent = {
                when (it) {
                    R.string.past_due -> TimeFrame(
                        titleResourceId = it,
                        tasks = pastDueTasks,
                        onDoneClick = { task -> viewModel.markDone(task) },
                        onItemClick = onItemClick
                    )
                    R.string.today -> TimeFrame(
                        titleResourceId = it,
                        tasks = todayTasks,
                        onDoneClick = { task -> viewModel.markDone(task) },
                        onItemClick = onItemClick
                    )
                    R.string.tomorrow -> TimeFrame(
                        titleResourceId = it,
                        tasks = tomorrowTasks,
                        onDoneClick = { task -> viewModel.markDone(task) },
                        onItemClick = onItemClick
                    )
                    R.string.this_week -> TimeFrame(
                        titleResourceId = it,
                        tasks = thisWeekTasks,
                        onDoneClick = { task -> viewModel.markDone(task) },
                        onItemClick = onItemClick
                    )
                    R.string.all_other -> TimeFrame(
                        titleResourceId = it,
                        tasks = allOtherTasks,
                        onDoneClick = { task -> viewModel.markDone(task) },
                        onItemClick = onItemClick
                    )
                }
            })
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DefaultPreview() {
    ScheduledTaskScreen(
        viewModel()
    )
}