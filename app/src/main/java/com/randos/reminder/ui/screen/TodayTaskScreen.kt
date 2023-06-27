package com.randos.reminder.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.randos.reminder.R
import com.randos.reminder.enums.ReminderScreen
import com.randos.reminder.navigation.NavigationDestination
import com.randos.reminder.ui.component.BaseViewWithFAB
import com.randos.reminder.ui.component.FadeAnimatedVisibility
import com.randos.reminder.ui.component.NoTaskMessage
import com.randos.reminder.ui.component.TaskCard
import com.randos.reminder.ui.theme.medium
import com.randos.reminder.ui.viewmodel.TodayTaskUiState
import com.randos.reminder.ui.viewmodel.TodayTaskViewModel
import kotlinx.coroutines.delay

object TaskTodayDestination : NavigationDestination {
    override val route: String = ReminderScreen.TODAY_TASK_SCREEN.name
    override val titleRes: Int = R.string.today
}

@Composable
fun TodayTaskScreen(
    viewModel: TodayTaskViewModel = hiltViewModel(),
    onAddTaskClick: () -> Unit = {},
    onItemClick: (Int) -> Unit = {}
) {
    val uiState by viewModel.uiState.observeAsState(TodayTaskUiState())
    var indexedId by remember { mutableStateOf(-1) }
    val listState = rememberLazyListState()

    BaseViewWithFAB(
        titleRes = R.string.today,
        animationEnabled = uiState.enterAnimationEnabled,
        onAddTaskClick = onAddTaskClick
    ) {
        LazyColumn(modifier = Modifier.padding(medium), state = listState) {
            items(uiState.dueTasks) {
                TaskCard(
                    task = it,
                    onItemClick = onItemClick,
                    onDoneClick = { state -> viewModel.updateTaskStatus(state) },
                    visible = !it.done,
                    indexed = it.id.toInt() == indexedId
                )
            }
            items(uiState.todayTasks) {
                TaskCard(
                    task = it,
                    onItemClick = onItemClick,
                    onDoneClick = { state -> viewModel.updateTaskStatus(state) },
                    isDateVisible = false,
                    visible = !it.done,
                    indexed = it.id.toInt() == indexedId
                )
            }
            items(1) {
                Box(modifier = Modifier.height(75.dp))
            }
        }
        FadeAnimatedVisibility(uiState.isAllEmpty) {
            NoTaskMessage()
        }

        DisposableEffect(key1 = Unit) {
            onDispose {
                viewModel.updateEnterAnimationEnabled(false)
            }
        }
    }
    // If user comes to this screen when user taps on notification following code scroll to that particular task
    uiState.scrollToPosition?.let {
        LaunchedEffect(key1 = it) {
            listState.animateScrollToItem(it)
            delay(200)
            indexedId = uiState.indexTaskId ?: -1
            delay(1300)
            viewModel.endTaskIndexing()
            indexedId = -1
        }
    }
}
