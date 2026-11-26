package com.example.lifeboardapp.data.presentation.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope

import com.example.lifeboardapp.LifeBoardApp
import com.example.lifeboardapp.data.local.entity.HabitEntity
import com.example.lifeboardapp.data.local.entity.TaskEntity
import com.example.lifeboardapp.data.local.entity.JournalEntity

import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

import java.time.LocalDate

data class DashboardUiState(
    val habits: List<HabitEntity> = emptyList(),
    val tasks: List<TaskEntity> = emptyList(),
    val recentEntries: List<JournalEntity> = emptyList(),
    val completedHabits: Int = 0,
    val totalHabits: Int = 0,
    val progress: Float = 0f,
    val isLoading: Boolean = true
)

class LifeBoardViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val container =
        (application as LifeBoardApp).container

    private val habitRepository = container.habitRepository
    private val taskRepository = container.taskRepository
    private val journalRepository = container.journalRepository

    private val today = LocalDate.now()

    val uiState: StateFlow<DashboardUiState> =
        combine(
            habitRepository.getActiveHabits(),
            taskRepository.getPendingTasks(),
            journalRepository.getAllEntries(),
            habitRepository.getCompletedHabitsForDate(today)
        ) { habits, tasks, entries, completed ->

            val total = habits.size

            DashboardUiState(
                habits = habits,
                tasks = tasks,
                recentEntries = entries.take(3),
                completedHabits = completed,
                totalHabits = total,
                progress = if (total > 0) {
                    completed.toFloat() / total
                } else {
                    0f
                },
                isLoading = false
            )

        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DashboardUiState()
        )

    fun updateTaskStatus(
        taskId: Int,
        completed: Boolean
    ) {
        viewModelScope.launch {
            taskRepository.updateTaskStatus(taskId, completed)
        }
    }

    // HABITS

    fun createHabit(habit: HabitEntity) {
        viewModelScope.launch {
            habitRepository.createHabit(habit)
        }
    }

    fun updateHabit(habit: HabitEntity) {
        viewModelScope.launch {
            habitRepository.updateHabit(habit)
        }
    }

    fun deleteHabit(habit: HabitEntity) {
        viewModelScope.launch {
            habitRepository.deleteHabit(habit)
        }
    }

    fun completeHabit(id: Int) {
        viewModelScope.launch {
            habitRepository.markHabitCompleted(id, today)
        }
    }

    fun uncompleteHabit(id: Int) {
        viewModelScope.launch {
            habitRepository.unmarkHabitCompleted(id, today)
        }
    }

    // TASKS

    fun createTask(task: TaskEntity) {
        viewModelScope.launch {
            taskRepository.createTask(task)
        }
    }

    fun updateTask(task: TaskEntity) {
        viewModelScope.launch {
            taskRepository.updateTask(task)
        }
    }

    fun toggleTask(id: Int, completed: Boolean) {
        viewModelScope.launch {
            taskRepository.updateTaskStatus(id, completed)
        }
    }

    fun deleteTask(id: Int) {
        viewModelScope.launch {
            taskRepository.deleteTaskById(id)
        }
    }
}