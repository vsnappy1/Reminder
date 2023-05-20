package com.randos.reminder.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.randos.reminder.data.repository.TaskRepository
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class AddTaskViewModelTest {


    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: AddTaskViewModel

    @Mock
    private lateinit var taskRepository: TaskRepository

    @Before
    fun setup() {
        viewModel = AddTaskViewModel(taskRepository)
    }

    @After
    fun tearDown(){
//        taskRepository = null
//        viewModel = null
    }

    @Test
    fun updateUi_whenTitleChanged_uiStateShouldMatch(){
        // Given
        val currentState = viewModel.uiState.value

        //When
        currentState?.copy(title = "title1")?.let { viewModel.updateUiState(it) }

        //Then
        assertEquals("title1", viewModel.uiState.value?.title)
    }
}