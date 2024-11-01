package com.varunkumar.tasks.viewmodels

import android.util.Log
import androidx.compose.animation.core.snap
import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import com.google.firebase.firestore.toObject
import com.varunkumar.tasks.models.Task
import com.varunkumar.tasks.models.TaskCategory
import com.varunkumar.tasks.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
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
    private val _categoryState = MutableStateFlow(CategoryState())
    private val user = getCurrentUser()!!
    private val _tasks = MutableStateFlow(emptyList<Task>())

    init {
        getTasks()
    }

    val categoryState = _categoryState.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val homeState = _homeState.flatMapLatest { state ->
        when (state.showBottomSheet) {
            false -> _homeState.update { it.copy(task = Task()) }
            else -> {}
        }

        _homeState
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), HomeState())

    val tasks = combine(_categoryState, _tasks) { state, tasks ->
        tasks.filter {
            true
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    // local task update functions
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
            it.copy(
                task = it.task.copy(taskCategory = TaskCategory(newCategory))
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
                _homeState.update {
                    it.copy(result = Result.Loading())
                }

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

                getTasks()

                _homeState.update {
                    it.copy(
                        result = Result.Success(true),
                        showBottomSheet = false
                    )
                }
            }
        } catch (e: Exception) {
            _homeState.update {
                it.copy(
                    result = Result.Error(e.message),
                    showBottomSheet = false
                )
            }
        }
    }

    fun deleteTaskFirebase() {
        try {
            viewModelScope.launch {
                _homeState.update {
                    it.copy(result = Result.Loading())
                }

                firestore
                    .collection(user.uid)
                    .document(_homeState.value.task.creationTime.toString())
                    .delete()
                    .await()

                getTasks()

                _homeState.update {
                    it.copy(
                        result = Result.Success(true),
                        showBottomSheet = false
                    )
                }
            }
        } catch (e: Exception) {
            _homeState.update {
                it.copy(
                    result = Result.Error(e.message),
                    showBottomSheet = false
                )
            }
        }
    }

    fun updateTaskStatusFirebase(task: Task) {
        try {
            viewModelScope.launch {
                _homeState.update { it.copy(result = Result.Loading()) }

                firestore
                    .collection(user.uid)
                    .document(task.creationTime.toString())
                    .update("isCompleted", !task.isCompleted)
                    .await()

                getTasks()

                _homeState.update { it.copy(result = Result.Success(true)) }
            }
        } catch (e: Exception) {
            _homeState.update { it.copy(result = Result.Error(e.message)) }
        }
    }

    private fun resetTask() {
        _homeState.update { it.copy(task = Task()) }
    }

    private fun getTasks() {
        try {
            viewModelScope.launch {
                _homeState.update { it.copy(result = Result.Loading()) }

                firestore
                    .collection(user.uid)
                    .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                        if (firebaseFirestoreException != null) {
                            _homeState.update {
                                it.copy(
                                    result = Result.Error(
                                        firebaseFirestoreException.message
                                    )
                                )
                            }
                            return@addSnapshotListener
                        }

                        Log.d("change firestore", "aksldfh aklshd fkjalshdlf kasd")

                        if (querySnapshot != null && !querySnapshot.isEmpty) {
                            _tasks.update {
                                querySnapshot.documents.mapNotNull { snapshot ->
                                    val task = snapshot.toObject(Task::class.java)

                                    task?.taskCategory?.let {

                                    }

                                    task
                                }
                            }
                        }
                    }
            }
        } catch (e: Exception) {
            _homeState.update { it.copy(result = Result.Loading()) }
        }
    }

    private fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }
}

data class HomeState(
    val result: Result<Boolean> = Result.Success(true),
    val isUpdatingTask: Boolean = false,
    val showBottomSheet: Boolean = false,
    val task: Task = Task(),
    val tasks: MutableList<Task> = mutableListOf()
)

data class CategoryState(
    val selectedCategory: TaskCategory = TaskCategory("All"),
    val isUpdatingTask: Boolean = false,
    val categories: MutableSet<TaskCategory> = mutableSetOf()
)