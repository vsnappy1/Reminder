package com.randos.reminder.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActionScope
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.PriorityHigh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.randos.reminder.ui.theme.Blue
import com.randos.reminder.ui.theme.Gray300
import com.randos.reminder.ui.theme.Gray500
import com.randos.reminder.ui.theme.Green
import com.randos.reminder.ui.theme.Red
import com.randos.reminder.R
import com.randos.reminder.enums.Priority
import com.randos.reminder.enums.RepeatCycle
import com.randos.reminder.ui.theme.*
import com.randos.reminder.ui.uiState.TaskUiState
import com.randos.reminder.ui.viewmodel.DELAY
import com.randos.reminder.utils.format
import kotlinx.coroutines.delay

@Composable
fun TransparentBackgroundTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    placeHolderId: Int,
    isSingleLine: Boolean = false,
    textStyle: TextStyle = Typography.titleLarge,
    enabled: Boolean = true,
    imeAction: ImeAction = ImeAction.Default,
    onNext: KeyboardActionScope.() -> Unit = {},
    onDone: KeyboardActionScope.() -> Unit = {},
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 0.dp),
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Sentences, // Capitalize first character of each sentence
            imeAction = imeAction
        ),
        keyboardActions = KeyboardActions(onNext = onNext, onDone = onDone),
        singleLine = isSingleLine,
        textStyle = textStyle
    )
    { textField ->
        if (value.isEmpty()) {
            Text(
                text = stringResource(id = placeHolderId),
                style = textStyle.copy(color = Gray500)
            )
        }
        textField()
    }
}

@Preview
@Composable
fun PreviewTransparentBackgroundTextField() {
    TransparentBackgroundTextField(
        value = "",
        onValueChange = {},
        placeHolderId = R.string.add
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
                .padding(all = medium)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Header(titleRes = titleRes)
            Divider(modifier = Modifier.padding(horizontal = 16.dp))
            if (animationEnabled) {
                AnimatedVisibility(
                    visible = isContentVisible,
                    enter = expandVertically()
                ) {
                    contentColumn()
                }
            } else {
                contentColumn()
            }
        }
        contentBox()
    }
    if (animationEnabled) {
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
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = stringResource(id = R.string.add),
                    tint = MaterialTheme.colorScheme.onPrimary,
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
            .background(MaterialTheme.colorScheme.background)
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
            color = MaterialTheme.colorScheme.primary,
            style = Typography.headlineSmall.copy(fontSize = 18.sp)
        )
    }
}

const val defaultAnimationDuration = 500

@Composable
fun TaskCard(
    modifier: Modifier = Modifier,
    task: TaskUiState = TaskUiState(),
    onItemClick: (Int) -> Unit = {},
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
            targetValue = if (indexed) Gray300 else Transparent,
            animationSpec = tween(durationMillis = 1000), label = ""
        )
        Box(
//            shape = shapes.large,
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(large))
                .clickable { onItemClick(task.id) }
                .background(cardBackground)
                .padding(top = small),
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
                                color = Gray500
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
                            color = Gray500
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
fun PreviewTaskCard() {
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
        val color = if (task.isDue) Red else Gray500
        if (isDateVisible) {
            task.date?.let {
                Text(
                    text = it.format(),
                    style = Typography.titleMedium,
                    color = if (task.done) Gray500 else color
                )
            }
        }

        var shouldAddComma = isDateVisible && task.isDateChecked
        if (isTimeVisible) {
            task.time?.let {
                Text(
                    text = "${if (shouldAddComma) ", " else ""}${it.format(LocalContext.current)}",
                    style = Typography.titleMedium,
                    color = if (task.done) Gray500 else color
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
                    color = if (task.done) Gray500 else color
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
            color = if (task.done) Gray500 else MaterialTheme.colorScheme.onBackground
        )

        if (task.priority != Priority.NONE) {
            Row(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = medium),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val color =
                    if (task.priority == Priority.HIGH) Red else if (task.priority == Priority.MEDIUM) MaterialTheme.colorScheme.secondary else Blue

                Text(
                    text = task.priority.value,
                    style = Typography.bodyMedium,
                    color = if (task.done) Gray500 else color
                )
                Icon(
                    imageVector = Icons.Rounded.PriorityHigh,
                    contentDescription = stringResource(id = R.string.priority),
                    tint = if (task.done) Gray500 else color,
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
        colors = CardDefaults.cardColors(containerColor = if (selected) Green else MaterialTheme.colorScheme.background),
        border = BorderStroke(1.5.dp, Green),
    ) {
        if (selected) {
            Icon(
                imageVector = Icons.Rounded.Done,
                contentDescription = stringResource(id = R.string.done),
                modifier = Modifier.padding(1.dp),
                tint = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

@Composable
fun ReminderDropDown(
    value: String = " None",
    onClick: () -> Unit = {},
    onDismiss: () -> Unit = {},
    enabled: Boolean = true,
    expanded: Boolean = false,
    content: @Composable (ColumnScope.() -> Unit) = {}
) {
    Box {
        Row(modifier = Modifier
            .padding(vertical = small)
            .clip(shapes.large)
            .clickable(enabled = enabled) { onClick() }) {
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
