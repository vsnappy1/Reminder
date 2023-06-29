package com.randos.reminder.di

import android.content.Context
import com.randos.reminder.data.ReminderDatabase
import com.randos.reminder.data.dao.TaskDao
import com.randos.reminder.data.repository.TaskRepository
import com.randos.reminder.data.repository.TaskRepositoryImpl
import com.randos.reminder.notification.NotificationManager
import com.randos.reminder.notification.NotificationManagerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {
    @Provides
    fun provideTaskDao(reminderDatabase: ReminderDatabase): TaskDao {
        return reminderDatabase.taskDao()
    }

    @Provides
    @Singleton
    fun provideReminderDatabase(@ApplicationContext context: Context): ReminderDatabase {
        return ReminderDatabase.getDatabase(context)
    }

    @Provides
    fun provideTaskRepository(taskDao: TaskDao): TaskRepository {
        return TaskRepositoryImpl(taskDao)
    }

    @Provides
    @Singleton
    fun provideNotificationManager(@ApplicationContext context: Context): NotificationManager {
        return NotificationManagerImpl(context)
    }
}