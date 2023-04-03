package com.randos.reminder.ui.screen

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.randos.reminder.R
import com.randos.reminder.data.entity.TaskUiState
import com.randos.reminder.enums.Priority
import com.randos.reminder.ui.theme.*
import com.randos.reminder.ui.viewmodel.AddAndModifyTaskViewModel
import java.util.*

private const val TAG = "AddAndModifyTaskScreen"

@Composable
fun AddAndModifyTaskScreen(
    onAdd: () -> Unit = {},
    onSave: () -> Unit = {},
    onCancel: () -> Unit = {},
    viewModel: AddAndModifyTaskViewModel
) {
    Box(
        modifier = Modifier
            .background(white)
            .padding(medium)
    ) {
        Column {
            val uiState by viewModel.uiState.observeAsState(initial = TaskUiState())
            Header(onAdd, onSave, onCancel, uiState)
            InputTitleAndNotesCard(uiState) { viewModel.updateUiState(it) }
            DetailsCard(uiState, {  })
        }
    }
}

@Composable
private fun InputTitleAndNotesCard(uiState: TaskUiState, onUpdate: (TaskUiState) -> Unit) {
    Card(
        elevation = 0.dp,
        backgroundColor = grey,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            TransparentBackgroundTextField(
                uiState.title,
                { onUpdate(uiState.copy(title = it)) },
                R.string.title
            )
            Divider(
                thickness = 1.dp,
                color = green,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            TransparentBackgroundTextField(
                uiState.notes,
                { onUpdate(uiState.copy(notes = it)) },
                R.string.notes,
                Modifier.height(100.dp)
            )
        }
    }
}

@Composable
private fun TransparentBackgroundTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeHolderId: Int,
    modifier: Modifier = Modifier
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
        )
    )
}

@Composable
private fun DetailsCard(uiState: TaskUiState, onUpdate: (TaskUiState) -> Unit) {
    Card(
        elevation = 0.dp,
        backgroundColor = grey,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = medium)
    ) {
        Column(modifier = Modifier.padding(medium)) {
            Text(
                text = stringResource(id = R.string.details),
                style = Typography.body2
            )
            DetailSwitch(
                icon = Icons.Filled.CalendarMonth,
                iconDescriptionId = R.string.date,
                titleId = R.string.date,
                checked = uiState.isDateChecked,
                onCheckedChange = {
                    Log.d(TAG, "DetailsCard: $it")
                    onUpdate(uiState.copy(isDateChecked = it))
                }
            )
            Divider(
                thickness = 1.dp,
                color = green,
                modifier = Modifier.padding(horizontal = medium)
            )
            DetailSwitch(
                icon = Icons.Filled.AccessTime,
                iconDescriptionId = R.string.time,
                titleId = R.string.time
            )
            Divider(
                thickness = 1.dp,
                color = green,
                modifier = Modifier.padding(horizontal = medium)
            )
            DetailSwitch(
                icon = Icons.Filled.Repeat,
                iconDescriptionId = R.string.repeat,
                titleId = R.string.repeat
            )
            if (false) {
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = small)
                ) {
                    RepeatCard(titleId = R.string.hourly, onClick = {})
                    RepeatCard(titleId = R.string.daily, onClick = {}, selected = true)
                    RepeatCard(titleId = R.string.weekly, onClick = {})
                    RepeatCard(titleId = R.string.monthly, onClick = {})
                    RepeatCard(titleId = R.string.yearly, onClick = {})
                }
            }

            Divider(
                thickness = 1.dp,
                color = green,
                modifier = Modifier.padding(horizontal = medium)
            )
            DetailDropdown(
                icon = Icons.Filled.PriorityHigh,
                iconDescriptionId = R.string.priority,
                titleId = R.string.priority
            )
        }
    }
}

@Composable
private fun Header(
    onAdd: () -> Unit = {},
    onSave: () -> Unit = {},
    onCancel: () -> Unit = {},
    viewModel: TaskUiState
) {
    Box(
        Modifier
            .fillMaxWidth()
            .padding(bottom = medium)
    ) {
        Text(
            text = stringResource(id = R.string.cancel),
            modifier = Modifier
                .align(Alignment.CenterStart)
                .clickable { onCancel() },
            style = Typography.body1,
            color = fontColorBlack
        )
        Text(
            text = stringResource(id = R.string.new_reminder),
            modifier = Modifier.align(Alignment.Center),
            style = Typography.body1,
            color = fontColorBlack
        )
        Text(
            text = stringResource(id = R.string.add),
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .clickable { onAdd() },
            style = Typography.body1,
            color = fontColorBlack
        )
    }
}

@Composable
private fun RepeatCard(titleId: Int, onClick: () -> Unit, selected: Boolean = false) {
    Card(
        elevation = 0.dp,
        modifier = Modifier.clickable { onClick() },
        border = BorderStroke(
            width = if (selected) 1.dp else 0.dp,
            color = if (selected) green else transparent
        )
    ) {
        Text(
            text = stringResource(id = titleId),
            modifier = Modifier.padding(vertical = small, horizontal = medium),
            color = if (selected) green else fontColorBlack,
            style = Typography.caption
        )
    }
}

@Composable
fun ShowDatePicker(onDateSetListener: (Int, Int, Int) -> Unit) {
    val context = LocalContext.current
    val calender = Calendar.getInstance()

    val presentYear = calender.get(Calendar.YEAR)
    val presentMonth = calender.get(Calendar.MONTH)
    val presentDay = calender.get(Calendar.DAY_OF_MONTH)

    val datePicker = DatePickerDialog(
        context,
        { _, selectedYear, selectedMonth, selectedDay ->
            onDateSetListener(
                selectedYear,
                selectedMonth,
                selectedDay
            )
        },
        presentYear,
        presentMonth,
        presentDay
    )
    datePicker.show()
}

@Composable
fun ShowTimePicker(onTimeSetListener: (Int, Int, Boolean) -> Unit) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    val presentHour = calendar.get(Calendar.HOUR_OF_DAY)
    val presentMinute = calendar.get(Calendar.MINUTE)
    val is24HourFormat = android.text.format.DateFormat.is24HourFormat(context)

    val timePicker = TimePickerDialog(
        context,
        { _, selectedHour, selectedMinute ->
            onTimeSetListener(
                selectedHour,
                selectedMinute,
                is24HourFormat
            )
        },
        presentHour,
        presentMinute,
        is24HourFormat
    )
    timePicker.show()
}

@Composable
private fun DetailSwitch(
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
                text = stringResource(id = titleId),
                style = Typography.body2
            )
            if (checked) {
                Text(
                    text = "date",
                    style = Typography.caption,
                    color = fontColorGrey
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
private fun DetailDropdown(
    icon: ImageVector,
    iconDescriptionId: Int,
    titleId: Int,
    onSelect: (Priority) -> Unit = {}
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
                text = stringResource(id = titleId),
                style = Typography.body2
            )
        }
        var expanded by remember { mutableStateOf(false) }
        DropdownMenu(
            expanded = expanded,
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

@Composable
private fun PriorityDropdownMenuItem(onClick: () -> Unit, value: String) {
    DropdownMenuItem(onClick = onClick) {
        Text(text = value)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DefaultAddTask() {

    AddAndModifyTaskScreen(viewModel = viewModel())
}