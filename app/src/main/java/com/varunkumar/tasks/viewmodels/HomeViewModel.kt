package com.varunkumar.tasks.viewmodels

import androidx.lifecycle.ViewModel
import com.varunkumar.tasks.models.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(): ViewModel(){
    private val _task = MutableStateFlow(Task())
    val task = _task.asStateFlow()

    val tasks = listOf(
        Task(title = "Task 1", description = "Description 1"),
        Task(title = "Task 2", description = "Description 2"),
        Task(title = "Task 3", description = "Description 3"),
        Task(title = "Task 4", description = "Description 4")
    )

    fun addTask(newTask: Task) {
        _task.update { newTask }
    }

    fun updateTitle(newTitle: String) {
        _task.update { it.copy(title = newTitle) }
    }

    fun updateDescription(newDescription: String) {
        _task.update { it.copy(description = newDescription) }
    }
}


//data class AddTaskState(
//    val title: String = "",
//    val description: String? = "",
//    val imageUri: String? = null,
//    val dateTime: Long = System.currentTimeMillis(),
//    val isCompleted: Boolean = false,
//)

//data class HomeState(
////    val selectedTask
//)