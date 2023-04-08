package com.randos.reminder.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.randos.reminder.R
import com.randos.reminder.enums.ReminderScreen
import com.randos.reminder.navigation.NavigationDestination
import com.randos.reminder.ui.theme.medium
import com.randos.reminder.ui.theme.White
import com.randos.reminder.ui.uiState.TaskUiState
import com.randos.reminder.ui.viewmodel.EditTaskViewModel

object TaskEditDestination : NavigationDestination {
    override val route: String = ReminderScreen.EDIT_TASK_SCREEN.name
    override val titleRes: Int = R.string.details
    const val taskIdArg = "taskId"
    val routeWithArgs = "$route/{$taskIdArg}"
}

@Composable
fun EditTaskScreen(
    onSave: () -> Unit = {},
    onCancel: () -> Unit = {},
    viewModel: EditTaskViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.observeAsState(initial = TaskUiState())
    Column(
        modifier = Modifier
            .background(White)
            .padding(medium)
            .fillMaxSize()
    ) {
        Header(
            uiState = uiState,
            headerResourceId = R.string.details,
            saveButtonTextResourceId = R.string.save,
            onCancel = onCancel
        ) {
            viewModel.updateTask()
            onSave()
        }
        InputTitleAndNotesCard(uiState = uiState) { viewModel.updateUiState(it) }
        DetailsCard(uiState = uiState) { viewModel.updateUiState(it) }
    }
}