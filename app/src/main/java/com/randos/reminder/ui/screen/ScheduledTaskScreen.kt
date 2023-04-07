package com.randos.reminder.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.randos.reminder.R
import com.randos.reminder.enums.ReminderScreen
import com.randos.reminder.navigation.NavigationDestination
import com.randos.reminder.ui.theme.*
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

    Box {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(white)
        ) {
            Header(modifier = Modifier.padding(horizontal = medium))

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
        FloatingActionButton(
            onClick = onAddTaskClick,
            modifier = Modifier
                .padding(large)
                .size(50.dp)
                .align(Alignment.BottomEnd),
            backgroundColor = green
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = stringResource(id = R.string.add),
                tint = white,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun Header(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = medium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = R.string.all_tasks),
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold
        )
        Box(
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.CenterEnd
        ) {
            Row {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = stringResource(id = R.string.search),
                )
            }
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