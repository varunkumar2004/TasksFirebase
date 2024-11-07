package com.varunkumar.tasks.home

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePickerState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import com.varunkumar.tasks.models.Task
import com.varunkumar.tasks.models.TaskCategory
import com.varunkumar.tasks.notification.AndroidTaskScheduler
import com.varunkumar.tasks.notification.TaskSchedulerReceiver
import com.varunkumar.tasks.notification.TaskSchedulerService
import com.varunkumar.tasks.utils.Result
import com.varunkumar.tasks.utils.formatTimePickerStateToLong
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val taskScheduler: AndroidTaskScheduler
) : ViewModel() {
    private val user = getCurrentUser()

    private val firestoreRef = firestore.collection(user.uid)

    private val _homeState = MutableStateFlow(HomeState())

    private val _resultState = MutableStateFlow<Result<Boolean>>(Result.Success(true))

    private val _categoryState = MutableStateFlow(CategoryState())

    var searchQuery = MutableStateFlow("")
        private set

    private val _tasks = firestoreRef.snapshots()

    val resultState = _resultState.asStateFlow()

    val categoryState = _categoryState.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val homeState = _homeState.flatMapLatest { state ->
        when (state.showBottomSheet) {
            false -> resetTask()
            else -> {}
        }

        Log.d("homestate state", state.toString())
        _homeState
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), HomeState())

    val tasks = combine(_categoryState, _tasks, searchQuery) { state, tasks, query ->
        resetCategoriesSet()

        val allTasks = tasks.documents.mapNotNull { doc ->
            val tk = doc.toObject(Task::class.java)

            tk?.category?.let { category ->
                _categoryState.update {
                    it.copy(categories = it.categories.apply { add(category) })
                }
            }

            tk
        }

        when (state.selectedCategory.tag) {
            "All" -> allTasks.filter { it.title.contains(query) }
            else -> allTasks.filter {
                it.category?.tag == state.selectedCategory.tag && it.title.contains(query)
            }
        }

    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    // local task update functions
    fun updateSearchQuery(query: String) {
        searchQuery.update { query }
    }

    fun updateTask(newTask: Task) {
        _homeState.update {
            it.copy(
                task = newTask,
                isUpdatingTask = true,
                showBottomSheet = true
            )
        }
    }

    fun updateTitle(newTitle: String) {
        _homeState.update {
            it.copy(
                task = it.task.copy(title = newTitle)
            )
        }
    }

    fun updateDescription(newDescription: String) {
        _homeState.update {
            it.copy(
                task = it.task.copy(description = newDescription)
            )
        }
    }

    fun updateTaskCategory(newCategory: String) {
        _homeState.update {
            val category = newCategory.trim()

            it.copy(
                task = it.task.copy(category = TaskCategory(category))
            )
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    fun updateTimeStamp(timePickerState: TimePickerState) {
        _homeState.update {
            it.copy(
                task = it.task.copy(scheduledTime = formatTimePickerStateToLong(timePickerState))
            )
        }
    }

    fun updateBottomSheet(newState: Boolean) {
        _homeState.update { it.copy(showBottomSheet = newState) }
    }

    fun updateSelectedCategory(newCategory: TaskCategory) {
        _categoryState.update { it.copy(selectedCategory = newCategory) }
    }

    fun updateIsUpdatingTask(isUpdating: Boolean) {
        _homeState.update { it.copy(isUpdatingTask = isUpdating) }
    }

    // firebase update functions
    fun addTaskFirebase(isUpdatingTask: Boolean) {
        try {
            viewModelScope.launch {
                _resultState.update { Result.Loading() }

                val dateTime =
                    if (!isUpdatingTask) System.currentTimeMillis() else _homeState.value.task.creationTime

                if (!isUpdatingTask) {
                    _homeState.update { it.copy(task = it.task.copy(creationTime = dateTime)) }
                }

                firestoreRef
                    .document(dateTime.toString())
                    .set(_homeState.value.task)
                    .await()

                scheduleTaskReminder()

                _homeState.update { it.copy(showBottomSheet = false) }
                _resultState.update { Result.Success(true) }
            }
        } catch (e: Exception) {
            _resultState.update { Result.Error(e.message) }
            _homeState.update { it.copy(showBottomSheet = false) }
        }
    }

    fun deleteTaskFirebase() {
        try {
            viewModelScope.launch {
                _resultState.update { Result.Loading() }

                firestoreRef
                    .document(_homeState.value.task.creationTime.toString())
                    .delete()
                    .await()

                _homeState.update { it.copy(showBottomSheet = false) }
                _resultState.update {
                    Result.Success(
                        data = true,
                        message = "Task Deleted Successfully."
                    )
                }
            }
        } catch (e: Exception) {
            _resultState.update { Result.Error(e.message) }
            _homeState.update { it.copy(showBottomSheet = false) }
        }
    }

    fun updateTaskStatusFirebase(task: Task) {
        try {
            viewModelScope.launch {
                _resultState.update { Result.Loading() }
                val newTask = task.copy(status = !task.status)

                firestoreRef
                    .document(task.creationTime.toString())
                    .set(newTask)
                    .await()

                _homeState.update { it.copy(showBottomSheet = false) }
                _resultState.update {
                    Result.Success(
                        data = true,
                        message = "Task Deleted Deleted."
                    )
                }
            }
        } catch (e: Exception) {
            _resultState.update { Result.Error(e.message) }
            _homeState.update { it.copy(showBottomSheet = false) }
        }
    }

    private fun resetTask() {
        _homeState.update { it.copy(task = Task()) }
    }

    private fun getCurrentUser(): FirebaseUser {
        return firebaseAuth.currentUser ?: throw Exception("User not logged in")
    }

    private fun resetCategoriesSet() {
        _categoryState.update {
            it.copy(
                categories = it.categories.apply {
                    clear()
                    add(TaskCategory("All"))
                }
            )
        }
    }

//    @SuppressLint("ScheduleExactAlarm")
    private fun scheduleTaskReminder() {
        taskScheduler.schedule(
            item = _homeState.value.task
        )
    }
}

data class HomeState(
    val isUpdatingTask: Boolean = false,
    val showBottomSheet: Boolean = false,
    val searchQuery: String = "",
    val task: Task = Task()
)

data class CategoryState(
    val selectedCategory: TaskCategory = TaskCategory("All"),
    val isUpdatingTask: Boolean = false,
    val categories: MutableSet<TaskCategory> = mutableSetOf(TaskCategory("All"))
)