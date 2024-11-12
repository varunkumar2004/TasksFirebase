package com.varunkumar.tasks.notification

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ClipDescription
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.varunkumar.tasks.MainActivity
import com.varunkumar.tasks.R
import com.varunkumar.tasks.models.Task

class TaskSchedulerService(
    private val context: Context
) {
    private val notificationManager: NotificationManagerCompat = NotificationManagerCompat.from(context)

    @SuppressLint("MissingPermission")
    fun showNotification(taskTitle: String, taskDescription: String) {
        val notification = buildNotification(taskTitle, taskDescription)
        notificationManager.notify(taskTitle.hashCode(), notification)
    }

    private fun buildNotification(taskTitle: String, taskDescription: String): Notification {
        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher) // Replace with your own icon
            .setContentTitle(taskTitle)
            .setContentText(taskDescription)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
    }

    companion object {
        const val CHANNEL_ID = "TASK_SCHEDULER_CHANNEL"

        // This method can stay in the companion object
        fun createNotificationChannel(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name = "Task Scheduler Notifications"

                val descriptionText = "Notifications for scheduled tasks"

                val importance = NotificationManager.IMPORTANCE_HIGH

                val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                    description = descriptionText
                }

                val notificationManager: NotificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }
        }
    }
}
