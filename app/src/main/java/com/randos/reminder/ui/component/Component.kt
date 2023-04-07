package com.randos.reminder.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.runtime.R
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.randos.reminder.ui.theme.*
import com.randos.reminder.ui.uiState.TaskUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter

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
            .clickable(enabled = enabled) { onClick() },
        style = Typography.body1,
        color = if (enabled) fontColorBlack else fontColorGrey
    )

}

@Composable
fun ReminderDefaultDropdown(
    modifier: Modifier = Modifier,
    value: String,
    content: @Composable() (ColumnScope.() -> Unit)
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
    onAddTaskClick: () -> Unit = {},
    content: @Composable() (ColumnScope.() -> Unit)
) {
    Box {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(white)
        ) {
            Header(modifier = Modifier.padding(horizontal = medium), titleRes = titleRes)
            content()

        }
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
                contentDescription = stringResource(id = com.randos.reminder.R.string.add),
                tint = white,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun Header(modifier: Modifier = Modifier, titleRes: Int) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = medium),
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
                    contentDescription = stringResource(id = com.randos.reminder.R.string.search),
                )
            }
        }
    }
}

@Composable
fun TimeFrame(
    titleResourceId: Int,
    tasks: List<TaskUiState>,
    onDoneClick: (TaskUiState) -> Unit = {},
    onItemClick: (Long) -> Unit = {}
) {
    TimeFrameHeader(
        title = stringResource(id = titleResourceId),
        modifier = Modifier.padding(horizontal = medium)
    )
    ListOfTasks(
        tasks = tasks,
        modifier = Modifier.padding(horizontal = medium),
        onDoneClick = onDoneClick,
        onItemClick = onItemClick
    )
}

@Composable
fun TimeFrameHeader(title: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .padding(vertical = medium)
            .fillMaxWidth()
    ) {
        Text(
            text = title,
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
    onItemClick: (Long) -> Unit = {}
) {
    Column(modifier = modifier) {
        tasks.forEach {
            TaskCard(
                task = it,
                onItemClick = onItemClick,
                onDoneClick = onDoneClick
            )
        }
    }
}

@Composable
private fun TaskCard(
    task: TaskUiState,
    onItemClick: (Long) -> Unit = {},
    onDoneClick: (TaskUiState) -> Unit = {}
) {
    var selected by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = medium)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(small)
                .clickable { onItemClick(task.id) }
        ) {
            RadioButton(selected = selected, onClick = {
                selected = !selected
                val job = coroutineScope.launch {
                    delay(1500)
                    if (selected) onDoneClick(task)
                    selected = false // Need this because next item list gets automatically selected
                }
                if (!selected) {
                    job.cancel()
                }
            })
            Column(modifier = Modifier.weight(1f, true)) {
                Text(text = task.title)
                Text(text = task.notes ?: "")
            }
            task.time?.let {
                Text(
                    text = task.time.format(DateTimeFormatter.ofPattern(stringResource(id = com.randos.reminder.R.string.time_format)))
                )
            }
        }
    }
}
