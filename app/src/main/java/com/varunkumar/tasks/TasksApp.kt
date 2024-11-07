package com.varunkumar.tasks

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import com.varunkumar.tasks.notification.TaskSchedulerService
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TasksApp : Application() {
    override fun onCreate() {
        super.onCreate()

        TaskSchedulerService.createNotificationChannel(this)
    }
}