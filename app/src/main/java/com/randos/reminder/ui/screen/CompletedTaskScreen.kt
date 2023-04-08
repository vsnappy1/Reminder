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
import com.randos.reminder.ui.component.ListOfTasks
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
    val previousSevenDaysTasks by viewModel.previousSevenDaysTasks.observeAsState(listOf())
    val allOtherTasks by viewModel.allOtherTasks.observeAsState(listOf())

    BaseView(titleRes = R.string.completed) {
        val timeFrames = listOf(
            R.string.today,
            R.string.yesterday,
            R.string.previous_seven_days,
            R.string.all_other
        )
        LazyColumn(modifier = Modifier.padding(medium)) {
            items(items = timeFrames, itemContent = {
                when (it) {
                    R.string.today -> {
                        TimeFrameHeader(titleRes = it)
                        ListOfTasks(
                            tasks = todayTasks,
                            onDoneClick = { task -> viewModel.markNotDone(task) },
                            onItemClick = onItemClick,
                            isDateVisible = false
                        )
                    }
                    R.string.yesterday -> {
                        TimeFrameHeader(titleRes = it)
                        ListOfTasks(
                            tasks = yesterdayTasks,
                            onDoneClick = { task -> viewModel.markNotDone(task) },
                            onItemClick = onItemClick,
                            isDateVisible = false
                        )
                    }
                    R.string.previous_seven_days -> {
                        TimeFrameHeader(titleRes = it)
                        ListOfTasks(
                            tasks = previousSevenDaysTasks,
                            onDoneClick = { task -> viewModel.markNotDone(task) },
                            onItemClick = onItemClick,
                        )
                    }
                    R.string.all_other -> {
                        TimeFrameHeader(titleRes = it)
                        ListOfTasks(
                            tasks = allOtherTasks,
                            onDoneClick = { task -> viewModel.markNotDone(task) },
                            onItemClick = onItemClick,
                        )
                    }
                }
            })
        }
    }
}