package com.randos.reminder.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.PriorityHigh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.randos.reminder.R
import com.randos.reminder.enums.Priority
import com.randos.reminder.enums.RepeatCycle
import com.randos.reminder.ui.theme.*
import com.randos.reminder.ui.uiState.TaskUiState
import com.randos.reminder.ui.viewmodel.DELAY
import com.randos.reminder.utils.format
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransparentBackgroundTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    placeHolderId: Int,
    isSingleLine: Boolean = false,
    textStyle: TextStyle = Typography.titleLarge
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
                style = textStyle
            )
        },
        colors = TextFieldDefaults.textFieldColors(
            containerColor = Transparent,
            unfocusedIndicatorColor = Transparent,
            focusedIndicatorColor = Transparent
        ),
        singleLine = isSingleLine,
        textStyle = textStyle
    )
}

@Composable
fun BaseView(
    modifier: Modifier = Modifier,
    titleRes: Int,
    animationEnabled: Boolean = false,
    contentBox: @Composable (BoxScope.() -> Unit) = {},
    contentColumn: @Composable (ColumnScope.() -> Unit)
) {
    var isContentVisible by remember { mutableStateOf(false) }
    Box {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(White)
        ) {
            Header(titleRes = titleRes)
            if (animationEnabled) {
                AnimatedVisibility(visible = isContentVisible,
                enter = expandHorizontally()
                ) {
                    contentColumn()
                }
            } else {
                contentColumn()
            }
        }
        contentBox()
    }
    if(animationEnabled){
        LaunchedEffect(key1 = Unit, block = {
            delay(250)
            isContentVisible = true
        })
    }
}

@Composable
fun BaseViewWithFAB(
    modifier: Modifier = Modifier,
    titleRes: Int,
    animationEnabled: Boolean = false,
    onAddTaskClick: () -> Unit = {},
    content: @Composable (ColumnScope.() -> Unit)
) {
    BaseView(
        modifier = modifier,
        titleRes = titleRes,
        animationEnabled = animationEnabled,
        contentBox = {
            FloatingActionButton(
                onClick = onAddTaskClick,
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.BottomEnd),
                containerColor = White
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
fun FadeAnimatedVisibility(
    visible: Boolean,
    enterDuration: Int = 400,
    enterDelay: Int = 100,
    exitDuration: Int = 400,
    exitDelay: Int = 100,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(
            animationSpec = tween(
                durationMillis = enterDuration,
                delayMillis = enterDelay
            )
        ),
        exit = fadeOut(
            animationSpec = tween(
                durationMillis = exitDuration,
                delayMillis = exitDelay
            )
        )
    ) {
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
            style = Typography.bodyMedium
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
            style = Typography.headlineSmall.copy(fontSize = 18.sp)
        )
    }
}

const val defaultAnimationDuration = 500

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
                delayMillis = (DELAY - defaultAnimationDuration).toInt()
            )
        )
    ) {
        val cardBackground by animateColorAsState(
            targetValue = if (indexed) Gray500 else Transparent,
            animationSpec = tween(durationMillis = 1000)
        )
        Box(
//            shape = shapes.large,
            modifier = modifier
                .fillMaxWidth()
                .padding(top = small)
//                .clip(shapes.large)
                .background(cardBackground)
                .clickable { onItemClick(task.id) },
//            colors = CardDefaults.cardColors(containerColor = cardBackground),
        ) {
            Row(
                modifier = Modifier
                    .padding(start = small)
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
                    TitleAndPriority(task)
                    task.notes?.let {
                        Spacer(modifier = Modifier.height(2.dp))
                        if (it.isNotBlank()) {
                            Text(
                                text = it,
                                style = Typography.titleMedium,
                                color = GrayDark
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    DateTimeRepeat(task, isDateVisible, isTimeVisible, isRepeatVisible)
                    Spacer(modifier = Modifier.height(2.dp))
                    task.completedOn?.let {
                        Text(
                            text = "Completed: ${it.format(LocalContext.current)}",
                            style = Typography.titleMedium,
                            color = GrayDark
                        )
                    }
                    Box(
                        modifier = Modifier
                            .padding(top = small)
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Gray300)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun previewTaskCard() {
    TaskCard(
        visible = true,
        task = TaskUiState(id = 1, title = "Title", notes = "These are the notes")
    )
}

@Composable
private fun DateTimeRepeat(
    task: TaskUiState,
    isDateVisible: Boolean,
    isTimeVisible: Boolean,
    isRepeatVisible: Boolean
) {
    Row {
        val color = if (task.isDue) Red else GrayDark
        if (isDateVisible) {
            task.date?.let {
                Text(
                    text = it.format(),
                    style = Typography.titleMedium,
                    color = if (task.done) GrayDark else color
                )
            }
        }

        var shouldAddComma = isDateVisible && task.isDateChecked
        if (isTimeVisible) {
            task.time?.let {
                Text(
                    text = "${if (shouldAddComma) ", " else ""}${it.format(LocalContext.current)}",
                    style = Typography.titleMedium,
                    color = if (task.done) GrayDark else color
                )
            }
        }

        shouldAddComma =
            isDateVisible && task.isDateChecked || isTimeVisible && task.isTimeChecked
        if (isRepeatVisible) {
            if (task.repeat != RepeatCycle.NO_REPEAT) {
                Text(
                    text = "${if (shouldAddComma) ", " else ""}${task.repeat.value}",
                    style = Typography.titleMedium,
                    color = if (task.done) GrayDark else color
                )
            }
        }
    }
}

@Composable
private fun TitleAndPriority(task: TaskUiState) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = task.title,
            style = Typography.titleLarge,
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
                    style = Typography.bodyMedium,
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
            .size(20.dp)
            .clip(CircleShape)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = if (selected) Green else White),
        border = BorderStroke(1.dp, Green),
    ) {
        if (selected) {
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
            .clip(shapes.large)
            .clickable { onClick() }) {
            Text(
                text = value,
                modifier = Modifier
                    .height(20.dp)
                    .padding(start = medium),
                style = Typography.bodyMedium,
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
            style = Typography.bodyLarge,
            textAlign = TextAlign.Center
        )
    }
}
