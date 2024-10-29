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
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
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
    val task = _task.asStateFlow()

    private val _tasks: Flow<List<Task>> = callbackFlow {
        user?.let {
            val reference = firebaseDatabase.getReference(it.uid)

            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val dataList = mutableListOf<Task>()
                    snapshot.children.forEach { child ->
                        val data = child.getValue(Task::class.java)
                        data?.let {
                            dataList.add(it)
                        }
                    }

                    trySend(dataList).isSuccess // Send the updated data
                }

                override fun onCancelled(error: DatabaseError) {
                    close(error.toException())
                }
            }

            reference.addValueEventListener(listener)

            awaitClose { reference.removeEventListener(listener) }
        } ?: emptyFlow<List<Task>>()
    }

    val tasks = _tasks

    private val user = getUser()

    fun addTask(newTask: Task) {
        _task.update { newTask }
    }

    fun updateTitle(newTitle: String) {
        _task.update { it.copy(title = newTitle) }
    }

    fun updateDescription(newDescription: String) {
        _task.update { it.copy(description = newDescription) }
    }

    fun addTask() {
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
        }
        
        resetTask()
    }

    private fun getUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

    private fun resetTask() {
        _task.update { Task() }
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