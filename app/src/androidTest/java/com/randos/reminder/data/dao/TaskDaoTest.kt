package com.randos.reminder.data.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.randos.reminder.data.ReminderDatabase
import com.randos.reminder.data.entity.Task
import com.randos.reminder.enums.Priority
import com.randos.reminder.enums.RepeatCycle
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@RunWith(AndroidJUnit4::class)
class TaskDaoTest {

    private lateinit var database: ReminderDatabase
    private lateinit var taskDao: TaskDao
    private val task = Task(
        0,
        "title",
        null,
        LocalDate.now(),
        LocalTime.now(),
        RepeatCycle.NO_REPEAT,
        Priority.HIGH,
        false,
        LocalDateTime.now()
    )

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            ReminderDatabase::class.java
        ).allowMainThreadQueries().build()
        taskDao = database.taskDao()
    }


    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun deleteTask_when_taskIsAvailable() = runBlocking {
        //Given
       val id = taskDao.insert(task)

        //When
        taskDao.delete(task.copy(id = id.toInt()))

        //Then
        val tasks = taskDao.getTasks().first()
        assertEquals(0, tasks.size)
    }
}