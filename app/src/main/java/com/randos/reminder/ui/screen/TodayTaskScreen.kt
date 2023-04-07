package com.randos.reminder.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.randos.reminder.R
import com.randos.reminder.enums.ReminderScreen
import com.randos.reminder.navigation.NavigationDestination
import com.randos.reminder.ui.theme.green
import com.randos.reminder.ui.theme.medium
import com.randos.reminder.ui.theme.small
import com.randos.reminder.ui.uiState.TaskUiState
import com.randos.reminder.ui.viewmodel.TodayTaskViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter

object TaskTodayDestination : NavigationDestination {
    override val route: String = ReminderScreen.TODAY_TASK_SCREEN.name
    override val titleRes: Int = R.string.today
}

@Composable
fun TodayTaskScreen(
    viewModel: TodayTaskViewModel = hiltViewModel(),
    onAddTaskClick: () -> Unit = {},
    onItemClick: (Long) -> Unit = {}
) {

}

@Composable
fun TimeFrame(
    titleResourceId: Int,
    tasks: List<TaskUiState>,
    onDoneClick: (TaskUiState) -> Unit = {},
    onItemClick: (Long) -> Unit = {}
) {
    TimeFrameHeader(
        title = stringResource(id = titleResourceId),
        modifier = Modifier.padding(horizontal = medium)
    )
    ListOfTasks(
        tasks = tasks,
        modifier = Modifier.padding(horizontal = medium),
        onDoneClick = onDoneClick,
        onItemClick = onItemClick
    )
}

@Composable
fun TimeFrameHeader(title: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .padding(vertical = medium)
            .fillMaxWidth()
    ) {
        Text(
            text = title,
            fontSize = 20.sp,
            modifier = Modifier.align(Alignment.CenterStart),
            fontWeight = FontWeight.Bold,
            color = green
        )
    }
}

@Composable
private fun ListOfTasks(
    tasks: List<TaskUiState>,
    modifier: Modifier = Modifier,
    onDoneClick: (TaskUiState) -> Unit = {},
    onItemClick: (Long) -> Unit = {}
) {
    Column(modifier = modifier) {
        tasks.forEach {
            TaskCard(
                task = it,
                onItemClick = onItemClick,
                onDoneClick = onDoneClick
            )
        }
    }
}

@Composable
fun TaskCard(
    task: TaskUiState,
    onItemClick: (Long) -> Unit = {},
    onDoneClick: (TaskUiState) -> Unit = {}
) {
    var selected by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = medium)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(small)
                .clickable { onItemClick(task.id) }
        ) {
            RadioButton(selected = selected, onClick = {
                selected = !selected
                val job = coroutineScope.launch {
                    delay(1500)
                    if (selected) onDoneClick(task)
                    selected = false // Need this because next item list gets automatically selected
                }
                if (!selected) {
                    job.cancel()
                }
            })
            Column(modifier = Modifier.weight(1f, true)) {
                Text(text = task.title)
                Text(text = task.notes ?: "")
            }
            task.time?.let {
                Text(
                    text = task.time.format(DateTimeFormatter.ofPattern(stringResource(id = R.string.time_format)))
                )
            }
        }
    }
}