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
import com.randos.reminder.ui.component.BaseViewWithFAB
import com.randos.reminder.ui.component.NoTaskMessage
import com.randos.reminder.ui.component.TaskCard
import com.randos.reminder.ui.theme.medium
import com.randos.reminder.ui.viewmodel.AllTaskViewModel

object TaskAllDestination : NavigationDestination {
    override val route: String = ReminderScreen.ALL_TASK_SCREEN.name
    override val titleRes: Int = R.string.all
}

@Composable
fun AllTaskScreen(
    viewModel: AllTaskViewModel = hiltViewModel(),
    onAddTaskClick: () -> Unit = {},
    onItemClick: (Long) -> Unit = {}
) {
    val tasks by viewModel.tasks.observeAsState(listOf())
    BaseViewWithFAB(titleRes = R.string.all, onAddTaskClick = onAddTaskClick) {
        LazyColumn(modifier = Modifier.padding(medium)) {
            items(tasks) {
                TaskCard(
                    task = it,
                    onItemClick = onItemClick,
                    onDoneClick = { state -> viewModel.updateTaskStatus(state) },
                    visible = !it.done
                )
            }
        }
        if (tasks.isEmpty()) {
            NoTaskMessage()
        }
    }
}