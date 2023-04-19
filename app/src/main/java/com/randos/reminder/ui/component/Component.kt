package com.randos.reminder.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.PriorityHigh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.randos.reminder.R
import com.randos.reminder.enums.Priority
import com.randos.reminder.enums.RepeatCycle
import com.randos.reminder.ui.theme.*
import com.randos.reminder.ui.uiState.TaskUiState
import com.randos.reminder.ui.viewmodel.DELAY
import com.randos.reminder.utils.format

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
        placeholder = {
            Text(
                text = stringResource(id = placeHolderId),
                style = Typography.body1
            )
        },
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Transparent,
            unfocusedIndicatorColor = Transparent,
            focusedIndicatorColor = Transparent
        ),
        singleLine = isSingleLine,
        textStyle = Typography.body1
    )
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
                .background(GrayLight)
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
                    .padding(16.dp)
//                    .size(50.dp)
                    .align(Alignment.BottomEnd),
                backgroundColor = White
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = stringResource(id = R.string.add),
                    tint = Green,
                    modifier = Modifier.size(25.dp)
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
            .background(White)
            .fillMaxWidth()
            .padding(all = medium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = titleRes),
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            style = Typography.body2
        )
    }
}

@Composable
fun TimeFrameHeader(titleRes: Int, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .padding(vertical = small)
            .fillMaxWidth()
    ) {
        Text(
            text = stringResource(id = titleRes),
            modifier = Modifier.align(Alignment.CenterStart),
            fontWeight = FontWeight.Bold,
            color = Green,
            style = Typography.h6.copy(fontSize = 18.sp)
        )
    }
}

const val defaultAnimationDuration = 250

@Composable
fun TaskCard(
    modifier: Modifier = Modifier,
    task: TaskUiState = TaskUiState(),
    onItemClick: (Long) -> Unit = {},
    onDoneClick: (TaskUiState) -> Unit = {},
    isDateVisible: Boolean = true,
    isTimeVisible: Boolean = true,
    isRepeatVisible: Boolean = true,
    visible: Boolean,
    indexed: Boolean = false
) {
    AnimatedVisibility(
        visible = visible,
        enter = EnterTransition.None,
        exit = fadeOut(
            tween(
                durationMillis = defaultAnimationDuration,
                delayMillis = (DELAY - 2 * defaultAnimationDuration).toInt()
            )
        )
    ) {
        val color by animateColorAsState(targetValue = if(indexed) Gray500 else White, animationSpec = tween(durationMillis = 1000))
        Card(
            shape = Shapes.small,
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = small)
                .clip(Shapes.small)
                .clickable { onItemClick(task.id) },
            backgroundColor = color
        ) {
            Row(
                modifier = Modifier
                    .padding(small)
            ) {
                ReminderRadioButton(
                    selected = task.done, onClick = { onDoneClick(task) },
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
                            color = if (task.done) GrayDark else Black
                        )

                        if (task.priority != Priority.NONE) {
                            Row(
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .padding(end = medium),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val color =
                                    if (task.priority == Priority.HIGH) Red else if (task.priority == Priority.MEDIUM) Green else Blue

                                Text(
                                    text = task.priority.value,
                                    style = Typography.body2,
                                    color = if (task.done) GrayDark else color
                                )
                                Icon(
                                    imageVector = Icons.Rounded.PriorityHigh,
                                    contentDescription = stringResource(id = R.string.priority),
                                    tint = if (task.done) GrayDark else color,
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
                                color = GrayDark
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Row {
                        val color = if (task.isDue) Red else GrayDark

                        if (isDateVisible) {
                            task.date?.let {
                                Text(
                                    text = it.format(),
                                    fontWeight = FontWeight.SemiBold,
                                    style = Typography.caption,
                                    color = if (task.done) GrayDark else color
                                )
                            }
                        }

                        if (isDateVisible && task.isDateChecked && isTimeVisible && task.isTimeChecked) {
                            Text(
                                text = ", ",
                                style = Typography.caption,
                                fontWeight = FontWeight.SemiBold,
                                color = if (task.done) GrayDark else color
                            )
                        }

                        if (isTimeVisible) {
                            task.time?.let {
                                Text(
                                    text = it.format(),
                                    style = Typography.caption,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (task.done) GrayDark else color
                                )
                            }
                        }

                        if ((isDateVisible && task.isDateChecked || isTimeVisible && task.isTimeChecked) && isRepeatVisible && task.isRepeatChecked) {
                            Text(
                                text = ", ",
                                style = Typography.caption,
                                fontWeight = FontWeight.SemiBold,
                                color = if (task.done) GrayDark else color
                            )
                        }

                        if (isRepeatVisible) {
                            if (task.repeat != RepeatCycle.NO_REPEAT) {
                                Text(
                                    text = task.repeat.value,
                                    style = Typography.caption,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (task.done) GrayDark else color
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    task.completedOn?.let {
                        Text(
                            text = "Completed: ${it.format()}",
                            style = Typography.caption,
                            fontWeight = FontWeight.SemiBold,
                            color = GrayDark
                        )
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
    val color by animateColorAsState(targetValue = if (selected) Green else White)
    Card(
        shape = RoundedCornerShape(9.dp),
        modifier = modifier
            .padding(small)
            .size(18.dp)
            .clip(RoundedCornerShape(9.dp))
            .clickable { onClick() },
        border = BorderStroke(1.dp, Green),
        backgroundColor = color
    ) {
        AnimatedVisibility (visible = selected, enter = fadeIn(), exit = fadeOut()) {
            Icon(
                imageVector = Icons.Rounded.Done,
                contentDescription = stringResource(id = R.string.done),
                modifier = Modifier.padding(1.dp),
                tint = White
            )
        }
    }
}

@Composable
fun ReminderDropDown(
    value: String = " None",
    onClick: () -> Unit = {},
    onDismiss: () -> Unit = {},
    expanded: Boolean = false,
    content: @Composable (ColumnScope.() -> Unit) = {}
) {
    Box {
        Row(modifier = Modifier
            .padding(vertical = small)
            .clip(Shapes.small)
            .clickable { onClick() }) {
            Text(
                text = value,
                modifier = Modifier
                    .height(20.dp)
                    .padding(start = medium),
                style = Typography.body2,
                textAlign = TextAlign.Center
            )
            Icon(
                imageVector = Icons.Rounded.ArrowDropDown,
                contentDescription = stringResource(id = R.string.arrow_drop_down),
                modifier = Modifier.size(20.dp)
            )
        }
        DropdownMenu(expanded = expanded,
            onDismissRequest = { onDismiss() }) {
            content()
        }
    }
}

@Composable
fun NoTaskMessage() {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
        Text(
            text = stringResource(id = R.string.no_task_message),
            modifier = Modifier
                .padding(large),
            style = Typography.body1,
            textAlign = TextAlign.Center
        )
    }
}
