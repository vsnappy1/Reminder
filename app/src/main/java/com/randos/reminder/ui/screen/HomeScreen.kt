package com.randos.reminder.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.randos.reminder.R
import com.randos.reminder.enums.ReminderScreen
import com.randos.reminder.navigation.NavigationDestination
import com.randos.reminder.ui.component.BaseViewWithFAB
import com.randos.reminder.ui.theme.*
import com.randos.reminder.ui.viewmodel.HomeViewModel
import com.randos.reminder.utils.NoRippleInteractionSource

object HomeDestination : NavigationDestination {
    override val route: String = ReminderScreen.HOME_SCREEN.name
    override val titleRes: Int = R.string.home
}

@Composable
fun HomeScreen(
    onTodayClick: () -> Unit = {},
    onScheduledClick: () -> Unit = {},
    onAllClick: () -> Unit = {},
    onCompletedClick: () -> Unit = {},
    onAddTaskClick: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val todayTaskCount by viewModel.todayTaskCount.observeAsState(0)
    val scheduledTasksCount by viewModel.scheduledTasksCount.observeAsState(0)
    val allTasksCount by viewModel.allTasksCount.observeAsState(0)
    val completedTaskCount by viewModel.completedTaskCount.observeAsState(0)

    val timeFrames = mutableListOf(
        TimeFrame(
            textRes = R.string.today,
            count = todayTaskCount,
            icon = Icons.Rounded.Today,
            iconDescriptionRes = R.string.today,
            onClick = onTodayClick
        ),
        TimeFrame(
            textRes = R.string.scheduled,
            count = scheduledTasksCount,
            icon = Icons.Rounded.CalendarMonth,
            iconDescriptionRes = R.string.scheduled,
            onClick = onScheduledClick
        ),
        TimeFrame(
            textRes = R.string.all,
            count = allTasksCount,
            icon = Icons.Rounded.AllInbox,
            iconDescriptionRes = R.string.all,
            onClick = onAllClick
        ),
        TimeFrame(
            textRes = R.string.completed,
            count = completedTaskCount,
            icon = Icons.Rounded.Done,
            iconDescriptionRes = R.string.completed,
            onClick = onCompletedClick
        ),
    )
    var value by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    var focusState by remember { mutableStateOf(false) }
    BaseViewWithFAB(titleRes = R.string.app_name, onAddTaskClick = onAddTaskClick) {
        ReminderTextField(
            value = value,
            onValueChange = { value = it },
            focusManager = focusManager,
            onFocusChange = { focusState = it }
        )

        if (focusState) {
            Box(modifier = Modifier
                .fillMaxSize()
                .indication(NoRippleInteractionSource(), null)
                .clickable { focusManager.clearFocus() }) {
            }
        } else {
            LazyVerticalGrid(columns = GridCells.Fixed(2)) {
                items(timeFrames) {
                    TimeFrameCard(
                        textRes = it.textRes,
                        count = it.count,
                        icon = it.icon,
                        iconDescriptionRes = it.iconDescriptionRes,
                        onClick = it.onClick
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun ReminderTextField(
    value: String = "Hello",
    onValueChange: (String) -> Unit = {},
    focusManager: FocusManager = LocalFocusManager.current,
    onFocusChange: (Boolean) -> Unit = {}
) {
    Row(
        modifier = Modifier
            .padding(medium)
            .background(color = White, shape = Shapes.small)
            .border(width = 1.dp, color = Gray300, shape = Shapes.small)
            .fillMaxWidth()
            .padding(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Rounded.Search,
            contentDescription = stringResource(id = R.string.search),
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(small))
        Box {
            if (value.isEmpty())
                Text(text = "Search", style = Typography.caption, color = Gray500)
            BasicTextField(
                value = value, onValueChange = onValueChange, singleLine = true,
                textStyle = Typography.caption,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 20.dp)
                    .onFocusChanged { onFocusChange(it.hasFocus) },
                keyboardActions = KeyboardActions { focusManager.clearFocus() },
            )
            if (value.isNotBlank()) {
                Icon(
                    imageVector = Icons.Rounded.Cancel,
                    contentDescription = stringResource(id = R.string.cancel),
                    modifier = Modifier
                        .size(16.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            onValueChange("")
                            focusManager.clearFocus()
                        }
                        .align(Alignment.CenterEnd)
                )
            }
        }

    }
}

data class TimeFrame(
    val textRes: Int,
    val count: Int,
    val icon: ImageVector,
    val iconDescriptionRes: Int,
    val onClick: () -> Unit
)

@Composable
fun TimeFrameCard(
    textRes: Int = R.string.today,
    count: Int = 5,
    icon: ImageVector = Icons.Rounded.Today,
    iconDescriptionRes: Int = R.string.today,
    onClick: () -> Unit = {}
) {
    Card(
        shape = Shapes.small,
        modifier = Modifier
            .padding(medium)
            .height(62.dp)
            .width(100.dp)
            .clip(Shapes.small)
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(medium)) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Icon(
                    imageVector = icon,
                    contentDescription = stringResource(id = iconDescriptionRes)
                )
                Text(
                    text = "$count",
                    modifier = Modifier.align(Alignment.CenterEnd),
                    style = Typography.h6.copy(fontWeight = FontWeight.Bold)
                )
            }
            Text(
                text = stringResource(id = textRes),
                style = Typography.caption.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(top = small, start = 2.dp)
            )
        }
    }
}