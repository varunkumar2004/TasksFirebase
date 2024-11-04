package com.varunkumar.tasks.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import com.varunkumar.tasks.models.Task
import com.varunkumar.tasks.models.TaskCategory
import com.varunkumar.tasks.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
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
    private val firestore: FirebaseFirestore
) : ViewModel() {
    private val _homeState = MutableStateFlow(HomeState())
    private val _resultState = MutableStateFlow<Result<Boolean>>(Result.Success(true))
    private val _categoryState = MutableStateFlow(CategoryState())
    private val user = getCurrentUser()!!
    var searchQuery = MutableStateFlow("")
        private set
    private val _tasks = firestore.collection(user.uid).snapshots()

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

            tk?.taskCategory?.let { category ->
                _categoryState.update {
                    it.copy(categories = it.categories.apply { add(category) })
                }
            }

            tk
        }

        when (state.selectedCategory.category) {
            "All" -> allTasks
            else -> allTasks.filter {
                it.taskCategory?.category == state.selectedCategory.category && it.title.contains(query)
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

    fun updateImageUri(newImageUri: String?) {
        _homeState.update {
            it.copy(
                task = it.task.copy(imageUri = newImageUri)
            )
        }
    }

    fun updateTaskCategory(newCategory: String) {
        _homeState.update {
            val category = newCategory.trim()

            it.copy(
                task = it.task.copy(taskCategory = TaskCategory(category))
            )
        }
    }

    fun updateTimeStamps(startTime: String) {
        _homeState.update {
            it.copy(
                task = it.task.copy(startTaskTime = startTime)
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

                firestore
                    .collection(user.uid)
                    .document(dateTime.toString())
                    .set(_homeState.value.task)
                    .await()

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

                firestore
                    .collection(user.uid)
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
                val newTask = task.copy(completed = !task.completed)

                firestore
                    .collection(user.uid)
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

    private fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
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