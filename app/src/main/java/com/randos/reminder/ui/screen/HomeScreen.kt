package com.randos.reminder.ui.screen

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AllInbox
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Today
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.compose.Blue
import com.example.compose.Gray200
import com.example.compose.Gray300
import com.example.compose.Gray500
import com.randos.reminder.R
import com.randos.reminder.enums.ReminderScreen
import com.randos.reminder.navigation.NavigationDestination
import com.randos.reminder.ui.component.BaseViewWithFAB
import com.randos.reminder.ui.component.FadeAnimatedVisibility
import com.randos.reminder.ui.component.TaskCard
import com.randos.reminder.ui.theme.*
import com.randos.reminder.ui.uiState.TaskUiState
import com.randos.reminder.ui.viewmodel.HomeScreenUiState
import com.randos.reminder.ui.viewmodel.HomeViewModel
import com.randos.reminder.utils.isNotificationPermissionGranted
import com.randos.reminder.utils.noRippleClickable
import kotlinx.coroutines.delay

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
    onSearchItemClick: (Int) -> Unit = {},
    onBackPress: () -> Unit = {},
    onRequestNotificationPermission: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(key1 = Unit) {
        delay(2000)
        visible = true
    }
    val homeUiState by viewModel.homeUiState.observeAsState(HomeScreenUiState())
    val focusManager = LocalFocusManager.current

    BackHandler() {
        if (homeUiState.doesSearchHasFocus) {
            viewModel.setSearchText("")
            focusManager.clearFocus()
            viewModel.setDoesSearchHasFocus(false)
        } else {
            onBackPress()
        }
    }

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

            if (homeUiState.doesSearchHasFocus && homeUiState.search.isNotBlank()) {
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

    val context = LocalContext.current
    var shouldShowDialog by remember { mutableStateOf(false) }
    LaunchedEffect(key1 = Unit) {
        shouldShowDialog =
            !context.isNotificationPermissionGranted() &&
                    viewModel.shouldShowNotificationRequestDialog(context)
    }

    if (shouldShowDialog) {
        Dialog(onDismissRequest = { shouldShowDialog = false }) {
            DialogView(onSkip = { shouldShowDialog = false },
                onIamIn = {
                    shouldShowDialog = false
                    onRequestNotificationPermission()
                })
        }
    }
}

@Preview
@Composable
private fun DialogView(
    onIamIn: () -> Unit = {},
    onSkip: () -> Unit = {},
) {
    Card(
        modifier = Modifier.padding(large),
        shape = shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .padding(large)
        ) {

            Icon(
                imageVector = Icons.Rounded.Notifications,
                contentDescription = "Notification",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(50.dp)
            )

            Text(
                text = stringResource(id = R.string.get_notified),
                style = Typography.headlineMedium,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = medium)
            )

            Text(
                text = stringResource(id = R.string.permission_for_notification_message),
                style = Typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(horizontal = extraExtraLarge, vertical = medium),
            )
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .padding(top = extraExtraLarge)
                    .fillMaxWidth()
            ) {
                Text(
                    text = stringResource(id = R.string.skip),
                    style = Typography.bodyLarge,
                    color = Blue,
                    modifier = Modifier
                        .noRippleClickable { onSkip() }
                        .padding(horizontal = medium)
                )
                Text(
                    text = stringResource(id = R.string.i_am_in),
                    style = Typography.bodyLarge,
                    color = MaterialTheme.colorScheme.background,
                    modifier = Modifier
                        .noRippleClickable { onIamIn() }
                        .padding(horizontal = medium)
                        .background(Blue, RoundedCornerShape(medium))
                        .padding(horizontal = extraExtraLarge, vertical = small)
                )
            }
        }
    }
}

val animDuration = 500

@Composable
private fun SearchView(
    homeUiState: HomeScreenUiState,
    focusManager: FocusManager,
    onShowCompletedTaskClick: () -> Unit,
    onDoneClick: (TaskUiState) -> Unit,
    onSearchItemClick: (Int) -> Unit
) {
    val alpha by animateFloatAsState(targetValue = if (homeUiState.search.isNotBlank()) 1f else 0.1f)
    val rotation by animateFloatAsState(
        targetValue = if (homeUiState.isFilteredCompletedTasksVisible) 180f else 0f,
        animationSpec = tween(durationMillis = animDuration), label = ""
    )
    Box(modifier = Modifier
        .background(MaterialTheme.colorScheme.surface.copy(alpha = alpha))
        .fillMaxSize()
        .noRippleClickable(enabled = homeUiState.search.isBlank()) { focusManager.clearFocus() }) {

        FadeAnimatedVisibility(homeUiState.search.isNotBlank(), exitDuration = 0, exitDelay = 0) {
            Column(modifier = Modifier.padding(medium)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = medium)
                ) {
                    Text(
                        text = "${homeUiState.filteredCompletedTasksCount} Completed",
                        style = Typography.labelLarge.copy(fontWeight = FontWeight.Bold)
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
                            visible = homeUiState.isFilteredCompletedTasksVisible,
                            enter = expandVertically(animationSpec = tween(durationMillis = animDuration)),
                            exit = shrinkVertically(animationSpec = tween(durationMillis = animDuration))
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
                    items(1) {
                        Box(modifier = Modifier.height(75.dp))
                    }
                }
            }
        }
    }
}

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
            .background(color = MaterialTheme.colorScheme.background, shape = shapes.large)
            .border(width = 1.dp, color = Gray300, shape = shapes.large)
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
                Text(text = "Search", style = Typography.labelLarge, color = Gray500)
            }
            BasicTextField(
                value = value, onValueChange = onValueChange, singleLine = true,
                textStyle = Typography.labelLarge.copy(color = MaterialTheme.colorScheme.onBackground),
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { onFocusChange(it.hasFocus) },
                keyboardActions = KeyboardActions { focusManager.clearFocus() },
                cursorBrush = SolidColor(MaterialTheme.colorScheme.onBackground)

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
        shape = shapes.large,
        modifier = Modifier
            .padding(medium)
            .height(70.dp)
            .width(100.dp)
            .clip(shapes.large)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
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
                    style = Typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                )
            }
            Text(
                text = stringResource(id = textRes),
                style = Typography.bodyMedium.copy(fontWeight = FontWeight.Bold, fontSize = 13.sp),
                modifier = Modifier.padding(top = small, start = 2.dp)
            )
        }
    }
}