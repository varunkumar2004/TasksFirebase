package com.varunkumar.tasks.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.varunkumar.tasks.models.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseDatabase: FirebaseDatabase
) : ViewModel() {
    private val _task = MutableStateFlow(Task())
    private val _showBottomSheet = MutableStateFlow(false)
    private val _categoryState = MutableStateFlow(CategoryState())
    private val user = getCurrentUser()
    private val _tasks: Flow<List<Task>> = callbackFlow {
        val reference = firebaseDatabase.getReference(user!!.uid)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val dataList = mutableListOf<Task>()

                snapshot.children.forEach { child ->
                    val data = child.getValue(Task::class.java)
                    data?.let { task ->
                        _categoryState.value.categories.add(task.taskCategory ?: "All")
                        dataList.add(task)
                    }
                }

                trySend(dataList).isSuccess
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        reference.addValueEventListener(listener)
        awaitClose { reference.removeEventListener(listener) }
    }

    val task = _task.asStateFlow()

    val categoryState = _categoryState.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val showModelBottomSheet = _showBottomSheet.flatMapLatest { status ->
        when (status) {
            false -> resetTask()
            else -> {}
        }
        _showBottomSheet
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    val tasks = combine(_categoryState, _tasks) { state, tasks ->
        tasks.filter { task ->
            when (state.selectedCategory) {
                "All" -> true
                else -> task.taskCategory == state.selectedCategory
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    fun updateTask(newTask: Task) {
        _task.update { newTask }
        _showBottomSheet.update { true }
    }

    fun updateTitle(newTitle: String) {
        _task.update { it.copy(title = newTitle) }
    }

    fun updateDescription(newDescription: String) {
        _task.update { it.copy(description = newDescription) }
    }

    fun updateTaskStatus(task: Task) {
        viewModelScope.launch {
            val reference = firebaseDatabase.getReference(user!!.uid)
            val newTask = task.copy(isCompleted = !task.isCompleted)

            reference
                .child(task.creationTime.toString())
                .setValue(newTask)
                .await()
        }
    }

    fun updateImageUri(newImageUri: String) {
        _task.update { it.copy(imageUri = newImageUri) }
    }

    fun updateTaskCategory(newCategory: String) {
        _task.update { it.copy(taskCategory = newCategory) }
    }

    fun updateTimeStamps(startTime: String) {
        _task.update { it.copy(startTaskTime = startTime) }
    }

    fun updateBottomSheet(newState: Boolean) {
        _showBottomSheet.update { newState }
    }

    private fun modifyTaskFirebase() {
        viewModelScope.launch {
            user?.let {
                firebaseDatabase
                    .getReference(it.uid)
                    .child(_task.value.creationTime.toString())
                    .setValue(_task.value)
                    .await()
            } ?: return@launch

            _showBottomSheet.update { false }
        }
    }

    private fun insertTaskFirebase() {
        viewModelScope.launch {
            val datetime = System.currentTimeMillis()
            _task.update { it.copy(creationTime = datetime) }

            user?.let {
                firebaseDatabase
                    .getReference(it.uid)
                    .child(datetime.toString())
                    .setValue(_task.value)
                    .await()
            } ?: return@launch

            _showBottomSheet.update { false }
        }
    }

    fun addTask(isUpdatingTask: Boolean) {
        if (isUpdatingTask) modifyTaskFirebase() else insertTaskFirebase()
    }

    fun updateSelectedCategory(newCategory: String) {
        _categoryState.update { it.copy(selectedCategory = newCategory) }
    }

    private fun resetTask() {
        _task.update { Task() }
    }

    private fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }
}

data class CategoryState(
    val selectedCategory: String = "All",
    val categories: MutableSet<String> = mutableSetOf("All")
)