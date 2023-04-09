package com.randos.reminder.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AllInbox
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.Today
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.randos.reminder.R
import com.randos.reminder.enums.ReminderScreen
import com.randos.reminder.navigation.NavigationDestination
import com.randos.reminder.ui.component.BaseViewWithFAB
import com.randos.reminder.ui.theme.Shapes
import com.randos.reminder.ui.theme.Typography
import com.randos.reminder.ui.theme.medium
import com.randos.reminder.ui.theme.small
import com.randos.reminder.ui.viewmodel.HomeViewModel

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
    BaseViewWithFAB(titleRes = R.string.app_name, onAddTaskClick = onAddTaskClick) {
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

data class TimeFrame(
    val textRes: Int,
    val count: Int,
    val icon: ImageVector,
    val iconDescriptionRes: Int,
    val onClick: () -> Unit
)

@Preview
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