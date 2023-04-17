package com.randos.reminder.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AllInbox
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Today
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.randos.reminder.R
import com.randos.reminder.enums.ReminderScreen
import com.randos.reminder.navigation.NavigationDestination
import com.randos.reminder.ui.component.BaseViewWithFAB
import com.randos.reminder.ui.component.TaskCard
import com.randos.reminder.ui.theme.Gray300
import com.randos.reminder.ui.theme.Gray500
import com.randos.reminder.ui.theme.GrayLight
import com.randos.reminder.ui.theme.Shapes
import com.randos.reminder.ui.theme.Typography
import com.randos.reminder.ui.theme.White
import com.randos.reminder.ui.theme.medium
import com.randos.reminder.ui.theme.small
import com.randos.reminder.ui.uiState.TaskUiState
import com.randos.reminder.ui.viewmodel.HomeScreenUiState
import com.randos.reminder.ui.viewmodel.HomeViewModel
import com.randos.reminder.utils.noRippleClickable

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
    onSearchItemClick: (Long) -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {

    val homeUiState by viewModel.homeUiState.observeAsState(HomeScreenUiState())

    val timeFrames = mutableListOf(
        TimeFrame(
            textRes = R.string.today,
            count = homeUiState.todayTaskCount,
            icon = Icons.Rounded.Today,
            iconDescriptionRes = R.string.today,
            onClick = onTodayClick
        ),
        TimeFrame(
            textRes = R.string.scheduled,
            count = homeUiState.scheduledTaskCount,
            icon = Icons.Rounded.CalendarMonth,
            iconDescriptionRes = R.string.scheduled,
            onClick = onScheduledClick
        ),
        TimeFrame(
            textRes = R.string.all,
            count = homeUiState.allTaskCount,
            icon = Icons.Rounded.AllInbox,
            iconDescriptionRes = R.string.all,
            onClick = onAllClick
        ),
        TimeFrame(
            textRes = R.string.completed,
            count = homeUiState.completedTaskCount,
            icon = Icons.Rounded.Done,
            iconDescriptionRes = R.string.completed,
            onClick = onCompletedClick
        ),
    )
    val focusManager = LocalFocusManager.current
    BaseViewWithFAB(titleRes = R.string.app_name, onAddTaskClick = onAddTaskClick) {
        ReminderTextField(
            value = homeUiState.search,
            onValueChange = { viewModel.setSearchText(it) },
            focusManager = focusManager,
            onFocusChange = { viewModel.setDoesSearchHasFocus(it) }
        )
        Box {
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

            if (homeUiState.doesSearchHasFocus) {
                SearchView(homeUiState, focusManager,
                    onShowCompletedTaskClick = { viewModel.setIsCompletedTasksVisible(!homeUiState.isFilteredCompletedTasksVisible) },
                    onDoneClick = { viewModel.updateTaskStatus(it) },
                    onSearchItemClick = {
                        viewModel.setSearchText("")
                        onSearchItemClick(it)
                    })
            } else {
                viewModel.setIsCompletedTasksVisible(false)
            }
        }
    }
}

@Composable
private fun SearchView(
    homeUiState: HomeScreenUiState,
    focusManager: FocusManager,
    onShowCompletedTaskClick: () -> Unit,
    onDoneClick: (TaskUiState) -> Unit,
    onSearchItemClick: (Long) -> Unit
) {
    val alpha by animateFloatAsState(targetValue = if (homeUiState.search.isNotBlank()) 1f else 0.1f)
    val rotation by animateFloatAsState(targetValue = if (homeUiState.isFilteredCompletedTasksVisible) 180f else 0f)
    Box(modifier = Modifier
        .background(GrayLight.copy(alpha = alpha))
        .fillMaxSize()
        .noRippleClickable(enabled = homeUiState.search.isBlank()) { focusManager.clearFocus() }) {

        FadeAnimatedVisibility(homeUiState.search.isNotBlank()) {
            Column(modifier = Modifier.padding(medium)) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "${homeUiState.filteredCompletedTasksCount} Completed.",
                        style = Typography.caption.copy(fontWeight = FontWeight.Bold)
                    )
                    Icon(
                        imageVector = Icons.Rounded.KeyboardArrowDown,
                        contentDescription = stringResource(id = R.string.show_completed_task),
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .noRippleClickable { onShowCompletedTaskClick() }
                            .rotate(rotation)
                    )
                }
                LazyColumn {
                    items(homeUiState.filteredCompletedTasks) {
                        AnimatedVisibility(
                            visible = homeUiState.isFilteredCompletedTasksVisible
                        ) {
                            TaskCard(
                                task = it,
                                onItemClick = onSearchItemClick,
                                onDoneClick = onDoneClick,
                                visible = it.done
                            )
                        }
                    }

                    items(homeUiState.filteredTasks) {
                        TaskCard(
                            task = it,
                            onItemClick = onSearchItemClick,
                            onDoneClick = onDoneClick,
                            visible = !it.done
                        )
                    }
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
            .padding(medium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Rounded.Search,
            contentDescription = stringResource(id = R.string.search),
            modifier = Modifier.size(16.dp)
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = small)
        ) {
            if (value.isEmpty()) {
                Text(text = "Search", style = Typography.caption, color = Gray500)
            }
            BasicTextField(
                value = value, onValueChange = onValueChange, singleLine = true,
                textStyle = Typography.caption,
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { onFocusChange(it.hasFocus) },
                keyboardActions = KeyboardActions { focusManager.clearFocus() },
            )
        }
        FadeAnimatedVisibility(
            visible = value.isNotBlank()
        ) {
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
            )
        }
    }
}

@Composable
fun FadeAnimatedVisibility(
    visible: Boolean,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        content()
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
            .height(70.dp)
            .width(100.dp)
            .clip(Shapes.small)
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(medium), verticalArrangement = Arrangement.Center) {
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
                style = Typography.body2.copy(fontWeight = FontWeight.Bold, fontSize = 13.sp),
                modifier = Modifier.padding(top = small, start = 2.dp)
            )
        }
    }
}