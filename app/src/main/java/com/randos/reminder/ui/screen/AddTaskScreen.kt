package com.randos.reminder.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccessTime
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.PriorityHigh
import androidx.compose.material.icons.rounded.Repeat
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.randos.reminder.R
import com.randos.reminder.enums.Priority
import com.randos.reminder.enums.ReminderScreen
import com.randos.reminder.enums.RepeatCycle
import com.randos.reminder.navigation.NavigationDestination
import com.randos.reminder.ui.component.BaseView
import com.randos.reminder.ui.component.ReminderDropDown
import com.randos.reminder.ui.component.TransparentBackgroundTextField
import com.randos.reminder.ui.theme.Black
import com.randos.reminder.ui.theme.Gray100
import com.randos.reminder.ui.theme.Gray200
import com.randos.reminder.ui.theme.Gray300
import com.randos.reminder.ui.theme.Gray500
import com.randos.reminder.ui.theme.GrayDark
import com.randos.reminder.ui.theme.GrayLight
import com.randos.reminder.ui.theme.Green
import com.randos.reminder.ui.theme.Red
import com.randos.reminder.ui.theme.shapes
import com.randos.reminder.ui.theme.Transparent
import com.randos.reminder.ui.theme.Typography
import com.randos.reminder.ui.theme.White
import com.randos.reminder.ui.theme.medium
import com.randos.reminder.ui.theme.small
import com.randos.reminder.ui.uiState.TaskUiState
import com.randos.reminder.ui.uiState.isValid
import com.randos.reminder.ui.viewmodel.AddTaskViewModel
import com.randos.reminder.utils.format
import com.vsnappy1.datepicker.DatePicker
import com.vsnappy1.datepicker.data.model.DatePickerDate
import com.vsnappy1.timepicker.TimePicker
import com.vsnappy1.timepicker.data.model.TimePickerTime
import com.vsnappy1.timepicker.enums.MinuteGap
import java.time.LocalDate
import java.time.LocalTime

object TaskAddDestination : NavigationDestination {
    override val route: String = ReminderScreen.ADD_TASK_SCREEN.name
    override val titleRes: Int = R.string.new_reminder
}
// TODO send a notification at 9:00 AM to let user know tasks for today, exclude tasks with time
// TODO Preserve notification when device restart
// TODO Optimize scrolling
// TODO add a view to explain permission for notification
// TODO write test cases
// TODO get the theme reviewed
// TODO ask for a QA
// TODO add firebase crash analytics
// TODO upload to play store

@Composable
fun AddTaskScreen(
    onAdd: () -> Unit = {}, onCancel: () -> Unit = {}, viewModel: AddTaskViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.observeAsState(initial = TaskUiState())
    BaseView(titleRes = R.string.new_reminder) {
        Column(modifier = Modifier.padding(medium)) {
            InputTitleAndNotesCard(uiState = uiState) { viewModel.updateUiState(it) }
            DetailsCard(uiState = uiState) { viewModel.updateUiState(it) }
            ActionButton(uiState = uiState, onCancel = onCancel, textRes = R.string.add) {
                viewModel.addTask()
                onAdd()
            }
        }
    }
}

@Composable
fun ActionButton(
    uiState: TaskUiState,
    textRes: Int,
    onCancel: () -> Unit,
    onDelete: () -> Unit = {},
    onAdd: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = medium)
    ) {
        ReminderButton(
            text = stringResource(id = R.string.cancel),
            onClick = onCancel,
            backgroundColor = Transparent,
            borderColor = Gray500,
            textColor = Gray500
        )
        if (uiState.done) {
            ReminderButton(
                text = stringResource(id = R.string.delete),
                onClick = onDelete,
                backgroundColor = Red,
                borderColor = Red,
            )
        }
        ReminderButton(
            text = stringResource(id = textRes),
            onClick = onAdd,
            enabled = uiState.isValid()
        )
    }
}

@Composable
fun InputTitleAndNotesCard(uiState: TaskUiState, onUpdate: (TaskUiState) -> Unit) {
    Card(
        shape = shapes.large,
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(Gray200, contentColor = Black),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            TransparentBackgroundTextField(
                value = uiState.title,
                placeHolderId = R.string.title,
                isSingleLine = true,
                textStyle = Typography.headlineSmall,
                onValueChange = {
                    onUpdate(uiState.copy(title = it))
                }
            )
            androidx.compose.material3.Divider(
                thickness = 1.dp,
                color = Green,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            TransparentBackgroundTextField(
                value = uiState.notes ?: "",
                onValueChange = { onUpdate(uiState.copy(notes = it)) },
                placeHolderId = R.string.notes,
                modifier = Modifier.height(100.dp)
            )
        }
    }
}

@Composable
fun DetailsCard(
    uiState: TaskUiState, onUpdate: (TaskUiState) -> Unit
) {
    Card(
        shape = shapes.large,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = medium),
        colors = CardDefaults.cardColors(Gray200, contentColor = Black),
    ) {
        Column(modifier = Modifier.padding(medium)) {
            Text(
                text = stringResource(id = R.string.details), style = Typography.bodyMedium
            )
            DateComponent(uiState, onUpdate)
            Divider()
            TimeComponent(uiState, onUpdate)
            AnimatedVisibility(uiState.isDateChecked) {
                Divider()
            }
            AnimatedVisibility(uiState.isDateChecked) {
                RepeatComponent(uiState, onUpdate)
            }
            Divider()
            PriorityComponent(uiState, onUpdate)
        }
    }
}

@Composable
private fun PriorityComponent(uiState: TaskUiState, onUpdate: (TaskUiState) -> Unit) {
    DetailDropdown(icon = Icons.Rounded.PriorityHigh,
        iconDescriptionId = R.string.priority,
        titleId = R.string.priority,
        priority = uiState.priority,
        onSelect = { onUpdate(uiState.copy(priority = it)) })
}

@Composable
private fun DateComponent(
    uiState: TaskUiState,
    onUpdate: (TaskUiState) -> Unit,
) {
    DetailSwitch(
        icon = Icons.Rounded.CalendarMonth,
        iconDescriptionId = R.string.date,
        titleId = R.string.date,
        checked = uiState.isDateChecked,
        onCheckedChange = {
            if (it) {
                onUpdate(
                    uiState.copy(
                        date = LocalDate.now(),
                        isDatePickerVisible = true,
                        isTimePickerVisible = false,
                        isDateChecked = true
                    )
                )
            } else {
                onUpdate(
                    uiState.copy(
                        date = null,
                        isDateChecked = false,
                        repeat = RepeatCycle.NO_REPEAT,
                        isRepeatChecked = false,
                        time = null,
                        isDatePickerVisible = false,
                        isTimePickerVisible = false,
                        isTimeChecked = false,
                    )
                )
            }
        },
        detail = uiState.date?.format(),
        onClickEnabled = uiState.isDateChecked,
        onClick = {
            onUpdate(
                uiState.copy(
                    isDatePickerVisible = !uiState.isDatePickerVisible,
                    isTimePickerVisible = false,
                )
            )
        }
    )
    AnimatedVisibility(
        visible = uiState.isDatePickerVisible,
        enter = expandVertically(animationSpec = tween(durationMillis = 400, delayMillis = 100)),
        exit = shrinkVertically(animationSpec = tween(durationMillis = 250))
    ) {
        DatePicker(
            onDateSelected = { year, month, day ->
                onUpdate(
                    uiState.copy(
                        date = LocalDate.of(year, month + 1, day), isDateChecked = true
                    )
                )
            },
            date = DatePickerDate(
                year = uiState.date?.year ?: LocalDate.now().year,
                month = (uiState.date?.monthValue ?: LocalDate.now().monthValue) - 1,
                day = uiState.date?.dayOfMonth ?: LocalDate.now().dayOfMonth
            )
        )
    }

}

@Composable
private fun TimeComponent(
    uiState: TaskUiState,
    onUpdate: (TaskUiState) -> Unit,
) {
    DetailSwitch(
        icon = Icons.Rounded.AccessTime,
        iconDescriptionId = R.string.time,
        titleId = R.string.time,
        checked = uiState.isTimeChecked,
        onCheckedChange = {
            if (it) {
                onUpdate(
                    uiState.copy(
                        isTimePickerVisible = true,
                        isDatePickerVisible = false,
                        time = LocalTime.now(),
                        isTimeChecked = true,
                        date = uiState.date ?: LocalDate.now(),
                        isDateChecked = true
                    )
                )
            } else {
                onUpdate(
                    uiState.copy(
                        time = null,
                        isTimePickerVisible = false,
                        isTimeChecked = false,
                        repeat = if (uiState.repeat == RepeatCycle.HOURLY) RepeatCycle.NO_REPEAT else uiState.repeat
                    )
                )
            }
        },
        detail = uiState.time?.format() ?: "",
        onClickEnabled = uiState.isTimeChecked,
        onClick = {
            onUpdate(
                uiState.copy(
                    isTimePickerVisible = !uiState.isTimePickerVisible,
                    isDatePickerVisible = false
                )
            )
        }
    )
    AnimatedVisibility(
        visible = uiState.isTimePickerVisible,
        enter = expandVertically(animationSpec = tween(durationMillis = 400, delayMillis = 100)),
        exit = shrinkVertically(animationSpec = tween(durationMillis = 250))
    ) {
        TimePicker(
            onTimeSelected = { hour, minute ->
                onUpdate(
                    uiState.copy(
                        time = LocalTime.of(hour, minute)
                    )
                )
            },
            minuteGap = MinuteGap.FIVE,
            time = TimePickerTime(
                hour = uiState.time?.hour ?: LocalTime.now().hour,
                minute = uiState.time?.minute ?: LocalTime.now().minute
            ),
        )
    }
}

@Composable
private fun RepeatComponent(
    uiState: TaskUiState, onUpdate: (TaskUiState) -> Unit
) {
    Column {
        DetailSwitchRepeat(icon = Icons.Rounded.Repeat,
            iconDescriptionId = R.string.repeat,
            titleId = R.string.repeat,
            checked = uiState.isRepeatChecked,
            onCheckedChange = {
                onUpdate(
                    uiState.copy(
                        isRepeatChecked = it,
                        repeat = if (it) uiState.repeat else RepeatCycle.NO_REPEAT
                    )
                )
            })
        AnimatedVisibility(uiState.isRepeatChecked) {
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = small)
            ) {
                AnimatedVisibility(
                    visible = uiState.isTimeChecked,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    RepeatCard(
                        titleId = R.string.hourly, onClick = {
                            onUpdate(uiState.copy(repeat = RepeatCycle.HOURLY))
                        }, selected = uiState.repeat == RepeatCycle.HOURLY
                    )
                }
                RepeatCard(
                    titleId = R.string.daily, onClick = {
                        onUpdate(uiState.copy(repeat = RepeatCycle.DAILY))
                    }, selected = uiState.repeat == RepeatCycle.DAILY
                )
                RepeatCard(
                    titleId = R.string.weekly, onClick = {
                        onUpdate(uiState.copy(repeat = RepeatCycle.WEEKLY))
                    }, selected = uiState.repeat == RepeatCycle.WEEKLY
                )
                RepeatCard(
                    titleId = R.string.monthly, onClick = {
                        onUpdate(uiState.copy(repeat = RepeatCycle.MONTHLY))
                    }, selected = uiState.repeat == RepeatCycle.MONTHLY
                )
                RepeatCard(
                    titleId = R.string.yearly, onClick = {
                        onUpdate(uiState.copy(repeat = RepeatCycle.YEARLY))
                    }, selected = uiState.repeat == RepeatCycle.YEARLY
                )
            }
        }
    }

}

@Composable
private fun RepeatCard(titleId: Int, onClick: () -> Unit, selected: Boolean = false) {
    val borderWidth by animateDpAsState(targetValue = if (selected) 1.dp else 0.dp)
    val color by animateColorAsState(targetValue = if (selected) Green else Transparent)
    Card(
        shape = shapes.large,
        modifier = Modifier
            .clip(shapes.large)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = White),
        border = BorderStroke(width = borderWidth, color = color)
    ) {
        Text(
            text = stringResource(id = titleId),
            modifier = Modifier.padding(vertical = small, horizontal = medium),
            color = if (selected) Green else Black,
            style = Typography.labelLarge
        )
    }
}


@Composable
private fun Divider() {
    androidx.compose.material3.Divider(
        thickness = 1.dp,
        color = Green,
        modifier = Modifier
            .padding(horizontal = medium)
            .padding(vertical = small)
    )
}

@Composable
private fun DetailItem(
    icon: ImageVector,
    iconDescriptionId: Int,
    titleId: Int,
    onClick: () -> Unit = {},
    onClickEnabled: Boolean = false,
    contentColumn: @Composable (ColumnScope.() -> Unit) = {},
    trailingComposable: @Composable (BoxScope.() -> Unit) = {},
) {
    Box(
        modifier = Modifier
            .clip(ShapeDefaults.Large)
            .clickable(enabled = onClickEnabled) { onClick() }
            .fillMaxWidth()
            .height(35.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = stringResource(id = iconDescriptionId),
            modifier = Modifier
                .align(Alignment.CenterStart)
                .size(20.dp)
        )
        Column(
            modifier = Modifier
                .padding(start = 28.dp)
                .align(Alignment.CenterStart)
        ) {
            Text(
                text = stringResource(id = titleId), style = Typography.bodyMedium
            )
            contentColumn()
        }
        Box(
            modifier = Modifier
                .padding(0.dp)
                .align(Alignment.CenterEnd)
        ) {
            trailingComposable()
        }
    }
}

@Composable
private fun DetailSwitch(
    icon: ImageVector,
    iconDescriptionId: Int,
    titleId: Int,
    checked: Boolean = false,
    onCheckedChange: (Boolean) -> Unit = {},
    detail: String?,
    onClickEnabled: Boolean,
    onClick: () -> Unit
) {
    DetailItem(
        icon = icon,
        iconDescriptionId = iconDescriptionId,
        titleId = titleId,
        contentColumn = {
            AnimatedVisibility(checked) {
                detail?.let {
                    Text(
                        text = it,
                        style = Typography.labelLarge.copy(fontSize = 11.sp),
                        color = GrayDark
                    )
                }
            }
        },
        onClickEnabled = onClickEnabled,
        onClick = { onClick() }
    ) {
        Switch(
            checked = checked, onCheckedChange = onCheckedChange
        )
    }
}

@Composable
private fun DetailSwitchRepeat(
    icon: ImageVector,
    iconDescriptionId: Int,
    titleId: Int,
    checked: Boolean = false,
    onCheckedChange: (Boolean) -> Unit = {}
) {
    DetailItem(
        icon = icon,
        iconDescriptionId = iconDescriptionId,
        titleId = titleId
    ) {
        Switch(
            checked = checked, onCheckedChange = onCheckedChange
        )
    }
}

@Composable
private fun DetailDropdown(
    icon: ImageVector,
    iconDescriptionId: Int,
    titleId: Int,
    onSelect: (Priority) -> Unit = {},
    priority: Priority
) {
    DetailItem(
        icon = icon,
        iconDescriptionId = iconDescriptionId,
        titleId = titleId
    ) {
        PriorityDropDown(priority, onSelect)
    }
}

@Composable
private fun PriorityDropDown(
    priority: Priority,
    onSelect: (Priority) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    ReminderDropDown(
        value = priority.value,
        onClick = { expanded = true },
        onDismiss = { expanded = false },
        expanded = expanded
    ) {
        PriorityDropdownMenuItem({
            expanded = false
            onSelect(Priority.NONE)
        }, Priority.NONE.value)
        PriorityDropdownMenuItem({
            expanded = false
            onSelect(Priority.LOW)
        }, Priority.LOW.value)
        PriorityDropdownMenuItem({
            expanded = false
            onSelect(Priority.MEDIUM)
        }, Priority.MEDIUM.value)
        PriorityDropdownMenuItem({
            expanded = false
            onSelect(Priority.HIGH)
        }, Priority.HIGH.value)
    }
}

@Composable
private fun PriorityDropdownMenuItem(onClick: () -> Unit, value: String) {
    DropdownMenuItem(onClick = onClick, text = { Text(text = value) })
}

@Preview
@Composable
fun ReminderButton(
    modifier: Modifier = Modifier,
    text: String = "Save",
    onClick: () -> Unit = {},
    enabled: Boolean = true,
    backgroundColor: Color = Green,
    borderColor: Color = Green,
    textColor: Color = White
) {

    val color by animateColorAsState(targetValue = if (enabled) backgroundColor else Gray500)
    val border by animateColorAsState(targetValue = if (enabled) borderColor else Transparent)
    OutlinedButton(
        modifier = modifier
            .width(100.dp),
        onClick = onClick,
        enabled = enabled,
        shape = shapes.large,
        colors = ButtonDefaults.buttonColors(
            containerColor = color,
            disabledContainerColor = color
        ),
        border = BorderStroke(1.dp, color = border)
    ) {
        Text(
            text = text,
            style = Typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = if (enabled) textColor else GrayLight
        )
    }
}