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
        Button(onClick = onTodayClick) {
            Text(text = stringResource(id = R.string.today))
        }
        Button(onClick = onScheduledClick) {
            Text(text = stringResource(id = R.string.scheduled))
        }
        Button(onClick = onAllClick) {
            Text(text = stringResource(id = R.string.all))
        }
        Button(onClick = onCompletedClick) {
            Text(text = stringResource(id = R.string.completed))
        }

    }
}