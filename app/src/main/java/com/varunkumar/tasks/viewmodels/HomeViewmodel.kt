package com.varunkumar.tasks.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewmodel @Inject constructor(): ViewModel() {
    private val _addTaskState = MutableStateFlow(AddTaskState())
    val addTaskState     = _addTaskState.asStateFlow()

    private val tasks = Unit
}

data class AddTaskState(
    val title: String = "",
    val description: String? = "",
    val imageUri: String? = null,
    val dateTime: Long = System.currentTimeMillis(),
    val isCompleted: Boolean = false,
)

//data class HomeState(
////    val selectedTask
//)