package com.randos.reminder.widget

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.randos.reminder.R
import com.randos.reminder.data.ReminderDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


private const val TAG = "ReminderWidgetService"

class ReminderWidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return ReminderWidgetRemoteViewsFactory(applicationContext)
    }

    class ReminderWidgetRemoteViewsFactory(private val context: Context) :
        RemoteViewsFactory {
        private var allTasks: MutableList<String> = mutableListOf("Task 1", "Task 2", "Task 3")

        override fun onCreate() {
            Log.d(TAG, "onCreate: ")
        }

        override fun onDataSetChanged() {
            Log.d(TAG, "onDataSetChanged: ")
            CoroutineScope(Dispatchers.Main).launch {
                val tasks = ReminderDatabase.getDatabase(context).taskDao().getTasks().first()
                allTasks.clear()
                allTasks.addAll(tasks.map { task -> task.title })
                Log.d(TAG, "onDataSetChanged: ItemCount = ${allTasks.size}")
            }
            // Adding some delay so that above coroutine finishes its task and then getCount method gets called
            Thread.sleep(1000)
        }

        override fun onDestroy() {
            Log.d(TAG, "onDestroy: ")
        }

        override fun getCount(): Int {
            Log.d(TAG, "getCount: ")
            return allTasks.size
        }

        override fun getViewAt(position: Int): RemoteViews {
            Log.d(TAG, "getViewAt: ")
            val item = allTasks[position]

            val remoteViews = RemoteViews(context.packageName, R.layout.widget_item)
            remoteViews.setTextViewText(R.id.textView, item)
            remoteViews.setOnClickFillInIntent(R.id.textView, Intent())

            return remoteViews
        }

        override fun getLoadingView(): RemoteViews? = null

        override fun getViewTypeCount(): Int = 1

        override fun getItemId(position: Int): Long = position.toLong()

        override fun hasStableIds(): Boolean = true
    }
}