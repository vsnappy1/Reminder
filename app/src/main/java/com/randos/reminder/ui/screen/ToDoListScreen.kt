package com.randos.reminder.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.randos.reminder.model.Task
import com.randos.reminder.ui.theme.*
import com.randos.reminder.R
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun ToDoListScreen(tasks: List<Task>) {
    Box {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(white)
        ) {
            Header(modifier = Modifier.padding(horizontal = medium))
            Column(
                modifier = Modifier
                    .background(grey)
                    .fillMaxSize()
            ) {
                DayHeader(
                    title = stringResource(id = R.string.today),
                    modifier = Modifier.padding(horizontal = medium)
                )
                ListOfTasks(tasks, modifier = Modifier.padding(horizontal = medium))
                DayHeader(
                    title = stringResource(id = R.string.tomorrow),
                    modifier = Modifier.padding(horizontal = medium)
                )
                ListOfTasks(tasks, modifier = Modifier.padding(horizontal = medium))
            }
        }
        FloatingActionButton(
            onClick = { /*TODO*/ },
            modifier = Modifier
                .padding(large)
                .size(50.dp)
                .align(Alignment.BottomEnd)
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = stringResource(id = R.string.add),
                tint = white,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun ListOfTasks(tasks: List<Task>, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        tasks.forEach {
            TaskCard(task = it)
        }
    }
}

@Composable
private fun DayHeader(title: String, modifier: Modifier = Modifier) {
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
private fun Header(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = medium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = R.string.all_tasks),
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold
        )
        Box(
            modifier = Modifier
                .fillMaxWidth(),
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

@Composable
fun TaskCard(task: Task) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = medium)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(small)
        ) {
            RadioButton(selected = task.done, onClick = { /*TODO*/ })
            Column(modifier = Modifier.weight(1f, true)) {
                Text(text = task.title)
                Text(text = task.notes)
            }
            task.time?.let {
                Text(
                    text = task.time.format(DateTimeFormatter.ofPattern(stringResource(id = R.string.time_format)))
                )
            }

        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DefaultPreview() {
    ToDoListScreen(
        listOf(
            Task("Title", "Description", time = LocalTime.now()),
            Task("Title", "Description"),
            Task("Title", "Description", time = LocalTime.now()),
            Task("Title", "Description", time = LocalTime.now())
        )
    )
}

//@Preview(showBackground = true)
//@Composable
//fun DefaultTaskCard() {
//    TaskCard(Task("Title", "Description", LocalDateTime.now()))
//}