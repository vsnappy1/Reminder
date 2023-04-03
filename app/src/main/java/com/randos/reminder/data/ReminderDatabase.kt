package com.randos.reminder.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.randos.reminder.data.dao.TaskDao
import com.randos.reminder.data.entity.Task

@Database(entities = [Task::class], version = 1, exportSchema = false)
abstract class ReminderDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile
        private var instance: ReminderDatabase? = null
        fun getDatabase(context: Context): ReminderDatabase {
            return instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    ReminderDatabase::class.java, "task_database"
                ).build().also { instance = it }
            }
        }
    }
}