package com.varunkumar.tasks.notification

import com.varunkumar.tasks.models.Task

interface TaskScheduler {
    fun schedule(item: Task)
    fun cancel(item: Task)
}