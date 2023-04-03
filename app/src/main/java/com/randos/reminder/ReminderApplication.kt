package com.randos.reminder

import android.app.Application
import android.content.Context
import com.randos.reminder.data.ReminderDatabase
import com.randos.reminder.data.dao.TaskDao
import com.randos.reminder.data.repository.TaskRepository
import com.randos.reminder.data.repository.TaskRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@HiltAndroidApp
class ReminderApplication: Application() {
}

