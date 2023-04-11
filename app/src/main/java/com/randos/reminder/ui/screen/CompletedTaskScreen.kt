package com.randos.reminder.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
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
import com.randos.reminder.ui.component.NoTaskMessage
import com.randos.reminder.ui.component.TimeFrameHeader
import com.randos.reminder.ui.theme.*
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
    val todayTasks by viewModel.todayTasks.observeAsState(listOf())
    val yesterdayTasks by viewModel.yesterdayTasks.observeAsState(listOf())
    val lastSevenDaysTasks by viewModel.lastSevenDaysTasks.observeAsState(listOf())
    val previousTasks by viewModel.allOtherTasks.observeAsState(listOf())
    val completeTaskCount by viewModel.completedTaskCount.observeAsState(0)

    // Creating flat list of views because nested lazy columns are not supported
    val list = mutableListOf<@Composable () -> Unit>()

    if (todayTasks.isNotEmpty()) {
        list.add { TimeFrameHeader(titleRes = R.string.today) }
    }
    list.addAll(
        getListOfTaskCards(
            tasks = todayTasks,
            onItemClick = onItemClick,
            onDoneClick = { state -> viewModel.updateTaskStatus(state) }
        )
    )

    if (yesterdayTasks.isNotEmpty()) {
        list.add { TimeFrameHeader(titleRes = R.string.yesterday) }
    }
    list.addAll(
        getListOfTaskCards(
            tasks = yesterdayTasks,
            onItemClick = onItemClick,
            onDoneClick = { state -> viewModel.updateTaskStatus(state) })
    )

    if (lastSevenDaysTasks.isNotEmpty()) {
        list.add { TimeFrameHeader(titleRes = R.string.last_seven_days) }
    }
    list.addAll(
        getListOfTaskCards(
            tasks = lastSevenDaysTasks,
            onItemClick = onItemClick,
            onDoneClick = { state -> viewModel.updateTaskStatus(state) })
    )

    if (previousTasks.isNotEmpty()) {
        list.add { TimeFrameHeader(titleRes = R.string.previous) }
    }
    list.addAll(
        getListOfTaskCards(
            tasks = previousTasks,
            onItemClick = onItemClick,
            onDoneClick = { state -> viewModel.updateTaskStatus(state) })
    )
    var isDialogVisible by remember { mutableStateOf(false) }
    BaseView(titleRes = R.string.completed) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column() {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(medium)
                ) {
                    Text(
                        text = "$completeTaskCount Completed.",
                        style = Typography.body2.copy(fontWeight = FontWeight.Bold)
                    )
                    if(completeTaskCount > 0) {
                        Icon(
                            imageVector = Icons.Rounded.Delete,
                            contentDescription = stringResource(id = R.string.delete),
                            modifier = Modifier
                                .size(18.dp)
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
                if (
                    todayTasks.isEmpty() &&
                    yesterdayTasks.isEmpty() &&
                    lastSevenDaysTasks.isEmpty() &&
                    previousTasks.isEmpty()
                ) {
                    NoTaskMessage()
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
    }
}

@Preview
@Composable
private fun DialogView(
    messageRes: Int = R.string.delete_all_confirmation_message,
    onDeleteClick: () -> Unit = {},
    onCancelClick: () -> Unit = {},
) {
    Card(modifier = Modifier.padding(large), shape = Shapes.small) {
        Column(modifier = Modifier.padding(large)) {
            Text(
                text = stringResource(id = messageRes),
                style = Typography.body2
            )
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .padding(top = medium)
                    .fillMaxWidth()
            ) {
                Text(
                    text = stringResource(id = R.string.cancel),
                    style = Typography.body2,
                    color = Gray500,
                    modifier = Modifier.noRippleClickable { onCancelClick() }
                )
                Text(
                    text = stringResource(id = R.string.delete),
                    style = Typography.body2,
                    color = Red,
                    modifier = Modifier.noRippleClickable { onDeleteClick() }
                )
            }
        }
    }
}