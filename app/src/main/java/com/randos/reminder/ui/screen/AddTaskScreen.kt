package com.randos.reminder.ui.screen

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.randos.reminder.R
import com.randos.reminder.enums.Priority
import com.randos.reminder.enums.ReminderScreen
import com.randos.reminder.enums.RepeatCycle
import com.randos.reminder.navigation.NavigationDestination
import com.randos.reminder.ui.component.ReminderDefaultText
import com.randos.reminder.ui.component.TransparentBackgroundTextField
import com.randos.reminder.ui.theme.*
import com.randos.reminder.ui.uiState.TaskUiState
import com.randos.reminder.ui.uiState.isValid
import com.randos.reminder.ui.viewmodel.AddTaskViewModel
import com.randos.reminder.utils.format
import java.time.LocalDate
import java.time.LocalTime
import java.util.*

object TaskAddDestination : NavigationDestination {
    override val route: String = ReminderScreen.ADD_TASK_SCREEN.name
    override val titleRes: Int = R.string.new_reminder
}

// TODO modularize this file, it has a lot of code
// TODO finalize color
// TODO apply font style to every Text
// TODO Resolve why next item is selected or deselected when one item is removed form screen
@Composable
fun AddTaskScreen(
    onAdd: () -> Unit = {}, onCancel: () -> Unit = {}, viewModel: AddTaskViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.observeAsState(initial = TaskUiState())
    Column(
        modifier = Modifier
            .background(white)
            .padding(medium)
            .fillMaxSize()
    ) {
        Header(
            uiState = uiState,
            headerResourceId = R.string.new_reminder,
            saveButtonTextResourceId = R.string.add,
            onCancel = onCancel
        ) {
            viewModel.addTask()
            onAdd()
        }
        InputTitleAndNotesCard(uiState = uiState) { viewModel.updateUiState(it) }
        DetailsCard(uiState = uiState) { viewModel.updateUiState(it) }
    }
}

@Composable
fun InputTitleAndNotesCard(uiState: TaskUiState, onUpdate: (TaskUiState) -> Unit) {
    Card(
        shape = Shapes.small,
        elevation = 0.dp,
        backgroundColor = grey,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            TransparentBackgroundTextField(
                value = uiState.title, onValueChange = {
                    onUpdate(uiState.copy(title = it))
                }, placeHolderId = R.string.title, isSingleLine = true
            )
            Divider(
                thickness = 1.dp, color = green, modifier = Modifier.padding(horizontal = 8.dp)
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
        backgroundColor = grey,
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
            if (uiState.isDateChecked) {
                Divider()
                RepeatComponent(uiState, onUpdate)
            }
            Divider()
            PriorityComponent(uiState, onUpdate)
        }
    }
}

@Composable
private fun PriorityComponent(uiState: TaskUiState, onUpdate: (TaskUiState) -> Unit) {
    DetailDropdown(icon = Icons.Filled.PriorityHigh,
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
        icon = Icons.Filled.CalendarMonth,
        iconDescriptionId = R.string.date,
        titleId = R.string.date,
        checked = uiState.isDateChecked,
        onCheckedChange = {
            if (it) {
                showDatePicker(
                    onDateSetListener = { year, month, day ->
                        onUpdate(
                            uiState.copy(
                                date = LocalDate.of(year, month + 1, day), isDateChecked = true
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
                        isRepeatChecked = false
                    )
                )
            }

        },
        detail = uiState.date?.format() ?: ""
    )
}

@Composable
private fun TimeComponent(
    uiState: TaskUiState,
    onUpdate: (TaskUiState) -> Unit,
) {
    val context = LocalContext.current
    DetailSwitch(
        icon = Icons.Filled.AccessTime,
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
    DetailSwitchRepeat(icon = Icons.Filled.Repeat,
        iconDescriptionId = R.string.repeat,
        titleId = R.string.repeat,
        checked = uiState.isRepeatChecked,
        onCheckedChange = {
            onUpdate(
                uiState.copy(
                    isRepeatChecked = it, repeat = if (it) uiState.repeat else RepeatCycle.NO_REPEAT
                )
            )
        })
    if (uiState.isRepeatChecked) {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = small)
        ) {
            if (uiState.isTimeChecked) {
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

@Composable
private fun Divider() {
    Divider(
        thickness = 1.dp,
        color = green,
        modifier = Modifier
            .padding(horizontal = medium)
            .padding(vertical = small)
    )
}

@Composable
fun Header(
    uiState: TaskUiState,
    headerResourceId: Int,
    saveButtonTextResourceId: Int,
    onCancel: () -> Unit = {},
    onSave: () -> Unit = {},
) {
    Box(
        Modifier
            .fillMaxWidth()
            .padding(bottom = medium)
    ) {
        ReminderDefaultText(textResourceId = R.string.cancel,
            modifier = Modifier.align(Alignment.CenterStart),
            onClick = { onCancel() })

        ReminderDefaultText(
            textResourceId = headerResourceId, modifier = Modifier.align(Alignment.Center)
        )

        ReminderDefaultText(
            textResourceId = saveButtonTextResourceId,
            modifier = Modifier.align(Alignment.CenterEnd),
            enabled = uiState.isValid(),
            onClick = { onSave() },
        )
    }
}

@Composable
private fun RepeatCard(titleId: Int, onClick: () -> Unit, selected: Boolean = false) {
    Card(
        shape = Shapes.small,
        elevation = 0.dp,
        modifier = Modifier
            .clip(Shapes.small)
            .clickable { onClick() },
        border = BorderStroke(
            width = if (selected) 1.dp else 0.dp, color = if (selected) green else transparent
        )
    ) {
        Text(
            text = stringResource(id = titleId),
            modifier = Modifier.padding(vertical = small, horizontal = medium),
            color = if (selected) green else fontBlack,
            style = Typography.caption
        )
    }
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
                selectedYear, selectedMonth, selectedDay
            )
        }, presentYear, presentMonth, presentDay
    )
    datePicker.show()
}

fun showTimePicker(onTimeSetListener: (Int, Int, Boolean) -> Unit, context: Context) {
    val calendar = Calendar.getInstance()

    var presentHour = calendar.get(Calendar.HOUR_OF_DAY)
    val presentMinute = 0
    val is24HourFormat = android.text.format.DateFormat.is24HourFormat(context)
    presentHour = (presentHour + 1) % if (is24HourFormat) 24 else 12

    val timePicker = TimePickerDialog(
        context, { _, selectedHour, selectedMinute ->
            onTimeSetListener(
                selectedHour, selectedMinute, is24HourFormat
            )
        }, presentHour, presentMinute, is24HourFormat
    )
    timePicker.show()
}

@Composable
private fun DetailSwitch(
    icon: ImageVector,
    iconDescriptionId: Int,
    titleId: Int,
    checked: Boolean = false,
    onCheckedChange: (Boolean) -> Unit = {},
    detail: String
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
            if (checked) {
                Text(
                    text = detail, style = Typography.caption, color = fontGrey
                )
            }
        }
        Switch(
            checked = checked, onCheckedChange = onCheckedChange,
            modifier = Modifier
                .padding(0.dp)
                .align(Alignment.CenterEnd),
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
        }
        Switch(
            checked = checked, onCheckedChange = onCheckedChange,
            modifier = Modifier
                .padding(0.dp)
                .align(Alignment.CenterEnd),
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
        }
        var expanded by remember { mutableStateOf(false) }
        Box(
            modifier = Modifier
                .padding(end = medium)
                .align(Alignment.CenterEnd)
        ) {
            Row(modifier = Modifier
                .clip(Shapes.small)
                .clickable { expanded = true }) {
                Text(text = priority.value, modifier = Modifier.padding(start = 6.dp))
                Icon(
                    imageVector = Icons.Rounded.ArrowDropDown,
                    contentDescription = stringResource(id = R.string.arrow_drop_down)
                )
            }

            DropdownMenu(expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.clickable { expanded = true }) {
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

    }
}

@Composable
private fun PriorityDropdownMenuItem(onClick: () -> Unit, value: String) {
    DropdownMenuItem(onClick = onClick) {
        Text(text = value)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DefaultAddTask() {
    AddTaskScreen()
}