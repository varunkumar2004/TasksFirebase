package com.varunkumar.tasks.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.varunkumar.tasks.models.Task

class AndroidTaskScheduler(
    private val context: Context
) : TaskScheduler {
    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    override fun schedule(item: Task) {
        val intent = Intent(context, TaskSchedulerReceiver::class.java).apply {
            putExtra("task_title", item.title)
            putExtra("task_description", item.description)
        }

        item.scheduledTime?.let { time ->
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                time,
                PendingIntent.getBroadcast(
                    context,
                    item.hashCode(),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            )
        }
    }

    override fun cancel(item: Task) {
        alarmManager.cancel(
            PendingIntent.getBroadcast(
                context,
                item.hashCode(),
                Intent(context, TaskSchedulerReceiver::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }
}