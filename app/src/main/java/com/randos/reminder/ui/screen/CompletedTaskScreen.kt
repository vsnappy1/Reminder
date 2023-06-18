package com.randos.reminder.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.randos.reminder.R
import com.randos.reminder.enums.ReminderScreen
import com.randos.reminder.navigation.NavigationDestination
import com.randos.reminder.ui.component.BaseView
import com.randos.reminder.ui.component.FadeAnimatedVisibility
import com.randos.reminder.ui.component.NoTaskMessage
import com.randos.reminder.ui.component.TaskCard
import com.randos.reminder.ui.component.TimeFrameHeader
import com.randos.reminder.ui.theme.Black
import com.randos.reminder.ui.theme.Gray500
import com.randos.reminder.ui.theme.Red
import com.randos.reminder.ui.theme.Typography
import com.randos.reminder.ui.theme.White
import com.randos.reminder.ui.theme.large
import com.randos.reminder.ui.theme.medium
import com.randos.reminder.ui.theme.shapes
import com.randos.reminder.ui.uiState.TaskUiState
import com.randos.reminder.ui.viewmodel.CompletedTaskUiState
import com.randos.reminder.ui.viewmodel.CompletedTaskViewModel
import com.randos.reminder.utils.noRippleClickable

object TaskCompletedDestination : NavigationDestination {
    override val route: String = ReminderScreen.COMPLETED_TASK_SCREEN.name
    override val titleRes: Int = R.string.completed
}

@Composable
fun CompletedTaskScreen(
    viewModel: CompletedTaskViewModel = hiltViewModel(),
    onItemClick: (Long) -> Unit = {}
) {
    val uiState by viewModel.uiState.observeAsState(CompletedTaskUiState())

    // Creating flat list of views because nested lazy columns are not supported
    val list = mutableListOf<@Composable () -> Unit>()

    if (uiState.todayTasks.isNotEmpty()) {
        list.add { TimeFrameHeader(titleRes = R.string.today) }
    }
    list.addAll(
        getListOfTaskCards(
            tasks = uiState.todayTasks,
            onItemClick = onItemClick,
            onDoneClick = { state -> viewModel.updateTaskStatus(state) }
        )
    )

    if (uiState.yesterdayTasks.isNotEmpty()) {
        list.add { TimeFrameHeader(titleRes = R.string.yesterday) }
    }
    list.addAll(
        getListOfTaskCards(
            tasks = uiState.yesterdayTasks,
            onItemClick = onItemClick,
            onDoneClick = { state -> viewModel.updateTaskStatus(state) })
    )

    if (uiState.lastSevenDaysTasks.isNotEmpty()) {
        list.add { TimeFrameHeader(titleRes = R.string.last_seven_days) }
    }
    list.addAll(
        getListOfTaskCards(
            tasks = uiState.lastSevenDaysTasks,
            onItemClick = onItemClick,
            onDoneClick = { state -> viewModel.updateTaskStatus(state) })
    )

    if (uiState.previousTasks.isNotEmpty()) {
        list.add { TimeFrameHeader(titleRes = R.string.previous) }
    }
    list.addAll(
        getListOfTaskCards(
            tasks = uiState.previousTasks,
            onItemClick = onItemClick,
            onDoneClick = { state -> viewModel.updateTaskStatus(state) })
    )
    var isDialogVisible by remember { mutableStateOf(false) }
    BaseView(titleRes = R.string.completed, animationEnabled = uiState.enterAnimationEnabled) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = medium, horizontal = large)
                ) {
                    Text(
                        text = "${uiState.completedTaskCount} Completed",
                        style = Typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    if (uiState.completedTaskCount > 0) {
                        Icon(
                            imageVector = Icons.Rounded.Delete,
                            contentDescription = stringResource(id = R.string.delete),
                            modifier = Modifier
                                .size(20.dp)
                                .align(Alignment.CenterEnd)
                                .noRippleClickable { isDialogVisible = true }
                        )
                    }
                }
                Divider(
                    thickness = 1.dp,
                    color = White,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                LazyColumn(modifier = Modifier.padding(horizontal = medium)) {
                    items(list) {
                        it()
                    }
                }
            }
            if (isDialogVisible) {
                Dialog(onDismissRequest = { isDialogVisible = false }) {
                    DialogView(
                        messageRes = R.string.delete_all_confirmation_message,
                        onDeleteClick = {
                            viewModel.deleteAll()
                            isDialogVisible = false
                        },
                        onCancelClick = { isDialogVisible = false }
                    )
                }
            }
        }
        DisposableEffect(key1 = Unit) {
            onDispose {
                viewModel.updateEnterAnimationEnabled(false)
            }
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
                visible = it.done
            )
        }
    }
    return list
}

@Preview
@Composable
private fun DialogView(
    messageRes: Int = R.string.delete_all_confirmation_message,
    onDeleteClick: () -> Unit = {},
    onCancelClick: () -> Unit = {},
) {
    Card(
        modifier = Modifier.padding(large),
        shape = shapes.large,
        colors = CardDefaults.cardColors(containerColor = White, contentColor = Black)
    ) {
        Column(modifier = Modifier.padding(large)) {
            Text(
                text = stringResource(id = messageRes),
                style = Typography.bodyMedium
            )
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .padding(top = large)
                    .fillMaxWidth()
            ) {
                Text(
                    text = stringResource(id = R.string.cancel),
                    style = Typography.bodyMedium,
                    color = Gray500,
                    modifier = Modifier.noRippleClickable { onCancelClick() }
                )
                Text(
                    text = stringResource(id = R.string.delete_all),
                    style = Typography.bodyMedium,
                    color = Red,
                    modifier = Modifier.noRippleClickable { onDeleteClick() }
                )
            }
        }
    }
}