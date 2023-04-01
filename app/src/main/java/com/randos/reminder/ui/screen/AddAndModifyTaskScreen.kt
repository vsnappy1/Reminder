package com.randos.reminder.ui.screen

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.randos.reminder.R
import com.randos.reminder.ui.theme.*
import java.util.*


@Composable
fun AddAndModifyTaskScreen() {
    Box(
        modifier = Modifier
            .background(white)
            .padding(medium)
    ) {
        Column {
            Header()
            InputTitleAndNotesCard()
            DetailsCard()
        }
    }
}

@Composable
private fun InputTitleAndNotesCard() {
    Card(
        elevation = 0.dp,
        backgroundColor = grey,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            TransparentBackgroundTextField("", {}, R.string.title)
            Divider(
                thickness = 1.dp,
                color = green,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            TransparentBackgroundTextField("", {}, R.string.notes, Modifier.height(100.dp))
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
private fun DetailsCard() {
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
            Detail(
                icon = Icons.Filled.CalendarMonth,
                iconDescriptionId = R.string.date,
                titleId = R.string.date
            )
            Divider(
                thickness = 1.dp,
                color = green,
                modifier = Modifier.padding(horizontal = medium)
            )
            Detail(
                icon = Icons.Filled.AccessTime,
                iconDescriptionId = R.string.time,
                titleId = R.string.time
            )
            Divider(
                thickness = 1.dp,
                color = green,
                modifier = Modifier.padding(horizontal = medium)
            )
            Detail(
                icon = Icons.Filled.Repeat,
                iconDescriptionId = R.string.repeat,
                titleId = R.string.repeat
            )
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = small)
            ) {
                RepeatCard(titleId = R.string.hourly, onClick = {})
                RepeatCard(titleId = R.string.daily, onClick = {})
                RepeatCard(titleId = R.string.weekly, onClick = {}, selected = true)
                RepeatCard(titleId = R.string.monthly, onClick = {})
                RepeatCard(titleId = R.string.yearly, onClick = {})
            }
        }
    }
}

@Composable
private fun Header() {
    Box(
        Modifier
            .fillMaxWidth()
            .padding(bottom = medium)
    ) {
        Text(
            text = "Cancel",
            modifier = Modifier
                .align(Alignment.CenterStart)
                .clickable { },
            style = Typography.body1
        )
        Text(
            text = "New Reminder",
            modifier = Modifier.align(Alignment.Center),
            style = Typography.body1
        )
        Text(
            text = stringResource(id = R.string.add),
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .clickable { },
            style = Typography.body1
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
private fun Detail(
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

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DefaultAddTask() {
    AddAndModifyTaskScreen()
}