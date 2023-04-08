package com.randos.reminder.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.PriorityHigh
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.randos.reminder.R
import com.randos.reminder.enums.Priority
import com.randos.reminder.enums.RepeatCycle
import com.randos.reminder.ui.theme.*
import com.randos.reminder.ui.uiState.TaskUiState
import com.randos.reminder.utils.format
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

@Composable
fun TransparentBackgroundTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    placeHolderId: Int,
    isSingleLine: Boolean = false
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 0.dp),
        placeholder = { Text(text = stringResource(id = placeHolderId)) },
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = transparent,
            unfocusedIndicatorColor = transparent,
            focusedIndicatorColor = transparent
        ),
        singleLine = isSingleLine
    )
}

@Composable
fun ReminderDefaultText(
    modifier: Modifier = Modifier,
    textResourceId: Int,
    enabled: Boolean = true,
    onClick: () -> Unit = {}
) {
    Text(
        text = stringResource(id = textResourceId),
        modifier = modifier
            .clip(Shapes.small)
            .clickable(enabled = enabled) { onClick() },
        style = Typography.body1,
        color = if (enabled) fontBlack else fontGrey
    )
}

@Composable
fun ReminderDefaultDropdown(
    modifier: Modifier = Modifier,
    value: String,
    content: @Composable (ColumnScope.() -> Unit)
) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = modifier) {
        Text(text = value, modifier = Modifier.clickable { expanded = true })
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.clickable { expanded = true }) {
            content()
        }
    }
}

@Composable
fun BaseView(
    modifier: Modifier = Modifier,
    titleRes: Int,
    contentBox: @Composable (BoxScope.() -> Unit) = {},
    contentColumn: @Composable (ColumnScope.() -> Unit)
) {
    Box {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(grey)
        ) {
            Header(titleRes = titleRes)
            contentColumn()

        }
        contentBox()
    }
}

@Composable
fun BaseViewWithFAB(
    modifier: Modifier = Modifier,
    titleRes: Int,
    onAddTaskClick: () -> Unit = {},
    content: @Composable (ColumnScope.() -> Unit)
) {
    BaseView(
        modifier = modifier,
        titleRes = titleRes,
        contentBox = {
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
        }) {
        content()
    }
}

@Composable
fun Header(modifier: Modifier = Modifier, titleRes: Int) {
    Row(
        modifier = modifier
            .background(white)
            .fillMaxWidth()
            .padding(all = medium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = titleRes),
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold
        )
        Box(
            modifier = Modifier.fillMaxWidth(),
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

//@Composable
//fun TimeFrame(
//    titleResourceId: Int,
//    tasks: List<TaskUiState>,
//    onDoneClick: (TaskUiState) -> Unit = {},
//    onItemClick: (Long) -> Unit = {}
//) {
//    TimeFrameHeader(
//        title = stringResource(id = titleResourceId),
//        modifier = Modifier.padding(horizontal = medium)
//    )
//    ListOfTasks(
//        tasks = tasks,
//        modifier = Modifier.padding(horizontal = medium),
//        onDoneClick = onDoneClick,
//        onItemClick = onItemClick
//    )
//}

@Composable
fun TimeFrameHeader(titleRes: Int, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .padding(vertical = medium)
            .fillMaxWidth()
    ) {
        Text(
            text = stringResource(id = titleRes),
            fontSize = 20.sp,
            modifier = Modifier.align(Alignment.CenterStart),
            fontWeight = FontWeight.Bold,
            color = green
        )
    }
}

@Composable
fun ListOfTasks(
    tasks: List<TaskUiState>,
    modifier: Modifier = Modifier,
    onDoneClick: (TaskUiState) -> Unit = {},
    onItemClick: (Long) -> Unit = {},
    isDateVisible: Boolean = true,
    isTimeVisible: Boolean = true,
    isRepeatVisible: Boolean = true,
) {
    Column(modifier = modifier) {
        tasks.forEach {
            TaskCard(
                task = it,
                onItemClick = onItemClick,
                onDoneClick = onDoneClick,
                isDateVisible = isDateVisible,
                isTimeVisible = isTimeVisible,
                isRepeatVisible = isRepeatVisible
            )
        }
    }
}

@Composable
@Preview
fun TaskCard(
    task: TaskUiState = TaskUiState(
        id = 1,
        title = "Shopping",
        notes = "Get ready for fast and convenient shopping together with the Shopping List app! It’s a simple and smart application where you can input tasks on one screen. The app will help you to prepare for food shopping, purchasing clothes, online ordering and any other shopping activity.",
        isDateChecked = true,
        date = LocalDate.now(),
        isTimeChecked = true,
        time = LocalTime.now(),
        isRepeatChecked = true,
        repeat = RepeatCycle.WEEKLY,
        priority = Priority.MEDIUM
    ),
    onItemClick: (Long) -> Unit = {},
    onDoneClick: (TaskUiState) -> Unit = {},
    isDateVisible: Boolean = true,
    isTimeVisible: Boolean = true,
    isRepeatVisible: Boolean = true,
) {
    var selected by remember { mutableStateOf(task.done) }
    val coroutineScope = rememberCoroutineScope()
    Card(
        shape = Shapes.small,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = small)
            .clip(Shapes.small)
            .clickable { onItemClick(task.id) }
    ) {
        Row(
            modifier = Modifier
                .padding(small)
        ) {
            var updatingStatus by remember { mutableStateOf(false) }
            ReminderRadioButton(
                selected = selected, onClick = {
                    selected = !selected
                    updatingStatus = !updatingStatus
                    coroutineScope.launch {
                        delay(1000)
                        if (updatingStatus) onDoneClick(task)
                    }
                },
                modifier = Modifier.padding(0.dp)
            )
            Column(
                modifier = Modifier
                    .weight(1f, true)
                    .padding(start = small)
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = task.title,
                        fontWeight = FontWeight.Bold,
                        style = Typography.body1,
                        color = if (selected) fontGrey else fontBlack,
                        modifier = Modifier.padding(top = 2.dp)
                    )

                    if (task.priority != Priority.NONE) {
                        Row(
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .padding(end = medium),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val color =
                                if (task.priority == Priority.HIGH) red else if (task.priority == Priority.MEDIUM) green_dark else blue

                            Text(
                                text = task.priority.value,
                                style = Typography.body2,
                                color = if (selected) fontGrey else color
                            )
                            Icon(
                                imageVector = Icons.Rounded.PriorityHigh,
                                contentDescription = stringResource(id = R.string.priority),
                                tint = if (selected) fontGrey else color,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }

                task.notes?.let {
                    if (it.isNotBlank()) {
                        Text(
                            text = it,
                            style = Typography.caption,
                            color = fontGrey
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Row {
                    val color = if (task.isDue) fontRed else fontGrey

                    if (isDateVisible) {
                        task.date?.let {
                            Text(
                                text = it.format(),
                                fontWeight = FontWeight.SemiBold,
                                style = Typography.caption,
                                color = if (selected) fontGrey else color
                            )
                        }
                    }

                    if (isDateVisible && task.isDateChecked && isTimeVisible && task.isTimeChecked) {
                        Text(
                            text = ", ",
                            style = Typography.caption,
                            fontWeight = FontWeight.SemiBold,
                            color = if (selected) fontGrey else color
                        )
                    }

                    if (isTimeVisible) {
                        task.time?.let {
                            Text(
                                text = it.format(),
                                style = Typography.caption,
                                fontWeight = FontWeight.SemiBold,
                                color = if (selected) fontGrey else color
                            )
                        }
                    }

                    if ((isDateVisible && task.isDateChecked || isTimeVisible && task.isTimeChecked) && isRepeatVisible && task.isRepeatChecked) {
                        Text(
                            text = ", ",
                            style = Typography.caption,
                            fontWeight = FontWeight.SemiBold,
                            color = if (selected) fontGrey else color
                        )
                    }

                    if (isRepeatVisible) {
                        if (task.repeat != RepeatCycle.NO_REPEAT) {
                            Text(
                                text = task.repeat.value,
                                style = Typography.caption,
                                fontWeight = FontWeight.SemiBold,
                                color = if (selected) fontGrey else color
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ReminderRadioButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    selected: Boolean = false
) {
    Card(
        shape = RoundedCornerShape(9.dp),
        modifier = modifier
            .padding(small)
            .size(18.dp)
            .clip(RoundedCornerShape(9.dp))
            .clickable { onClick() },
        border = BorderStroke(1.dp, green),
        backgroundColor = if (selected) green else white
    ) {
        if (selected) {
            Icon(
                imageVector = Icons.Rounded.Done,
                contentDescription = stringResource(id = R.string.done),
                modifier = Modifier.padding(1.dp),
                tint = white
            )
        }
    }
}
