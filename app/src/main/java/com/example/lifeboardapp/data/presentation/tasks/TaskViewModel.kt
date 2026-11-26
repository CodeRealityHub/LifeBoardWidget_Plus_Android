package com.example.lifeboardapp.data.presentation.tasks

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.lifeboardapp.LifeBoardApp
import com.example.lifeboardapp.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TaskViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val taskRepository =
        (application as LifeBoardApp).container.taskRepository

    val uiState: StateFlow<TaskUiState> =
        taskRepository.getAllTasks()
            .map { tasks ->
                TaskUiState(
                    tasks = tasks,
                    isLoading = false
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = TaskUiState()
            )

    // CREATE
    fun addTask(
        title: String,
        description: String,
        priority: Int,
        dueDateEpochDay: Long?
    ) {
        viewModelScope.launch {
            taskRepository.createTask(
                TaskEntity(
                    title = title,
                    description = description,
                    priority = priority,
                    dueDateEpochDay = dueDateEpochDay
                )
            )
        }
    }

    // UPDATE
    fun updateTask(
        id: Int,
        title: String,
        description: String,
        priority: Int,
        dueDateEpochDay: Long?
    ) {
        viewModelScope.launch {
            val existingTask = uiState.value.tasks
                .find { it.id == id }
                ?: return@launch

            taskRepository.updateTask(
                existingTask.copy(
                    title = title,
                    description = description,
                    priority = priority,
                    dueDateEpochDay = dueDateEpochDay
                )
            )
        }
    }

    // DELETE
    fun deleteTask(id: Int) {
        viewModelScope.launch {
            taskRepository.deleteTaskById(id)
        }
    }

    // COMPLETE / REOPEN
    fun toggleTask(
        id: Int,
        completed: Boolean
    ) {
        viewModelScope.launch {
            taskRepository.updateTaskStatus(
                id = id,
                completed = completed
            )
        }
    }
}
