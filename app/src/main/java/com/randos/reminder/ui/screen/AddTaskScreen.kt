package com.randos.reminder.ui.screen

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.platform.LocalContext
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
import com.randos.reminder.ui.theme.Gray500
import com.randos.reminder.ui.theme.GrayDark
import com.randos.reminder.ui.theme.GrayLight
import com.randos.reminder.ui.theme.Green
import com.randos.reminder.ui.theme.Red
import com.randos.reminder.ui.theme.Shapes
import com.randos.reminder.ui.theme.Transparent
import com.randos.reminder.ui.theme.Typography
import com.randos.reminder.ui.theme.White
import com.randos.reminder.ui.theme.medium
import com.randos.reminder.ui.theme.small
import com.randos.reminder.ui.uiState.TaskUiState
import com.randos.reminder.ui.uiState.isValid
import com.randos.reminder.ui.viewmodel.AddTaskViewModel
import com.randos.reminder.utils.format
import java.time.LocalDate
import java.time.LocalTime
import java.util.Calendar

object TaskAddDestination : NavigationDestination {
    override val route: String = ReminderScreen.ADD_TASK_SCREEN.name
    override val titleRes: Int = R.string.new_reminder
}

// TODO Preserve notification when device restart
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
//    val listState = rememberLazyListState()
//    listState.animateScrollToItem(1)
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
        shape = Shapes.small,
        elevation = 0.dp,
        backgroundColor = White,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            TransparentBackgroundTextField(
                value = uiState.title, onValueChange = {
                    onUpdate(uiState.copy(title = it))
                }, placeHolderId = R.string.title, isSingleLine = true
            )
            Divider(
                thickness = 1.dp, color = Green, modifier = Modifier.padding(horizontal = 8.dp)
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
        shape = Shapes.small,
        elevation = 0.dp,
        backgroundColor = White,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = medium)
    ) {
        Column(modifier = Modifier.padding(medium)) {
            Text(
                text = stringResource(id = R.string.details), style = Typography.body2
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
    val context = LocalContext.current
    DetailSwitch(
        icon = Icons.Rounded.CalendarMonth,
        iconDescriptionId = R.string.date,
        titleId = R.string.date,
        checked = uiState.isDateChecked,
        onCheckedChange = {
            if (it) {
                showDatePicker(
                    onDateSetListener = { year, month, day ->
                        onUpdate(
                            uiState.copy(
                                date = LocalDate.of(year, month, day), isDateChecked = true
                            )
                        )
                    }, context = context
                )
            } else {
                onUpdate(
                    uiState.copy(
                        date = null,
                        isDateChecked = false,
                        repeat = RepeatCycle.NO_REPEAT,
                        isRepeatChecked = false,
                        time = null,
                        isTimeChecked = false,
                    )
                )
            }
        },
        detail = uiState.date?.format()
    )
}

@Composable
private fun TimeComponent(
    uiState: TaskUiState,
    onUpdate: (TaskUiState) -> Unit,
) {
    val context = LocalContext.current
    DetailSwitch(
        icon = Icons.Rounded.AccessTime,
        iconDescriptionId = R.string.time,
        titleId = R.string.time,
        checked = uiState.isTimeChecked,
        onCheckedChange = {
            if (it) {
                showTimePicker(
                    onTimeSetListener = { hour, minute, is24 ->
                        onUpdate(
                            uiState.copy(
                                time = LocalTime.of(
                                    (hour + if (is24) 12 else 0) % 24, minute
                                ),
                                isTimeChecked = true,
                                date = uiState.date ?: LocalDate.now(),
                                isDateChecked = true
                            )
                        )
                    }, context = context
                )
            } else {
                onUpdate(
                    uiState.copy(
                        time = null,
                        isTimeChecked = false,
                        repeat = if (uiState.repeat == RepeatCycle.HOURLY) RepeatCycle.NO_REPEAT else uiState.repeat
                    )
                )
            }
        },
        detail = uiState.time?.format() ?: ""
    )
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
        shape = Shapes.small,
        elevation = 0.dp,
        modifier = Modifier
            .clip(Shapes.small)
            .clickable { onClick() },
        border = BorderStroke(width = borderWidth, color = color)
    ) {
        Text(
            text = stringResource(id = titleId),
            modifier = Modifier.padding(vertical = small, horizontal = medium),
            color = if (selected) Green else Black,
            style = Typography.caption
        )
    }
}


@Composable
private fun Divider() {
    Divider(
        thickness = 1.dp,
        color = Green,
        modifier = Modifier
            .padding(horizontal = medium)
            .padding(vertical = small)
    )
}

fun showDatePicker(
    onDateSetListener: (Int, Int, Int) -> Unit, context: Context
) {
    val calender = Calendar.getInstance()

    val presentYear = calender.get(Calendar.YEAR)
    val presentMonth = calender.get(Calendar.MONTH)
    val presentDay = calender.get(Calendar.DAY_OF_MONTH)

    val datePicker = DatePickerDialog(
        context, { _, selectedYear, selectedMonth, selectedDay ->
            onDateSetListener(
                selectedYear, selectedMonth + 1, selectedDay
            )
        }, presentYear, presentMonth, presentDay
    )
    datePicker.show()
}

fun showTimePicker(onTimeSetListener: (Int, Int, Boolean) -> Unit, context: Context) {
    val calendar = Calendar.getInstance()

    val is24HourFormat = android.text.format.DateFormat.is24HourFormat(context)
    // Advance hour by 1 to create better user experience
    val presentHour = (calendar.get(Calendar.HOUR_OF_DAY) + 1) % 24

    val timePicker = TimePickerDialog(
        context, { _, selectedHour, selectedMinute ->
            onTimeSetListener(
                selectedHour, selectedMinute, is24HourFormat
            )
        }, presentHour, 0, is24HourFormat
    )
    timePicker.show()
}

@Composable
private fun DetailItem(
    icon: ImageVector,
    iconDescriptionId: Int,
    titleId: Int,
    contentColumn: @Composable (ColumnScope.() -> Unit) = {},
    trailingComposable: @Composable (BoxScope.() -> Unit) = {},
) {
    Box(
        modifier = Modifier
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
                text = stringResource(id = titleId), style = Typography.body2
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
    detail: String?
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
                        style = Typography.caption.copy(fontSize = 11.sp),
                        color = GrayDark
                    )
                }
            }
        }
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
    DropdownMenuItem(onClick = onClick) {
        Text(text = value)
    }
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
        shape = Shapes.small,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = color,
            disabledBackgroundColor = color
        ),
        border = BorderStroke(1.dp, color = border)
    ) {
        Text(
            text = text,
            style = Typography.body2.copy(fontWeight = FontWeight.Bold),
            color = if (enabled) textColor else GrayLight
        )
    }
}