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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.randos.reminder.R
import com.randos.reminder.enums.ReminderScreen
import com.randos.reminder.navigation.NavigationDestination
import com.randos.reminder.ui.component.BaseView
import com.randos.reminder.ui.component.ListOfTasks
import com.randos.reminder.ui.theme.*
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
    BaseView(titleRes = R.string.all, onAddTaskClick = onAddTaskClick) {
        ListOfTasks(tasks = tasks, onItemClick = onItemClick, onDoneClick = {
            viewModel.markDone(it)
        })
    }
}