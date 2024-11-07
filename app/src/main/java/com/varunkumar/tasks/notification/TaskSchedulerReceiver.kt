package com.varunkumar.tasks.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.varunkumar.tasks.R
import com.varunkumar.tasks.notification.TaskSchedulerService.Companion.CHANNEL_ID

class TaskSchedulerReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val taskTitle = intent.getStringExtra("task_title") ?: return
        val taskDescription = intent.getStringExtra("task_description") ?: return

        val taskSchedulerService = TaskSchedulerService(context)
        taskSchedulerService.showNotification(taskTitle, taskDescription)
    }
}