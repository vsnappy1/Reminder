package com.randos.reminder.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.randos.reminder.R
import com.randos.reminder.enums.ReminderScreen
import com.randos.reminder.navigation.NavigationDestination
import com.randos.reminder.ui.component.ReminderButton

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
) {
    Column(modifier = Modifier.fillMaxSize()) {
        ReminderButton(
            valueRes = R.string.today,
            onClick = onTodayClick
        )
        ReminderButton(
            valueRes = R.string.scheduled,
            onClick = onScheduledClick
        )
        ReminderButton(
            valueRes = R.string.all,
            onClick = onAllClick
        )
        ReminderButton(
            valueRes = R.string.completed,
            onClick = onCompletedClick
        )
    }
}