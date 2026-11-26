
package com.example.lifeboardapp.presentation.habits

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.lifeboardapp.LifeBoardApp
import com.example.lifeboardapp.data.local.entity.HabitEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

class HabitViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val habitRepository =
        (application as LifeBoardApp).container.habitRepository

    private val today = LocalDate.now()

    // Observe active habits and today's completion records.
    val uiState: StateFlow<HabitUiState> =
        combine(
            habitRepository.getActiveHabits(),
            habitRepository.getCompletedHabitIdsForDate(today)
        ) { habits, completedIds ->

            HabitUiState(
                habits = habits,
                completedHabitIds = completedIds.toSet()
            )

        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HabitUiState()
        )

    // CREATE
    fun addHabit(
        title: String,
        description: String,
        frequency: String,
        targetPerWeek: Int
    ) {
        viewModelScope.launch {
            habitRepository.createHabit(
                HabitEntity(
                    title = title,
                    description = description,
                    frequency = frequency,
                    targetPerWeek = targetPerWeek
                )
            )
        }
    }

    // UPDATE
    fun updateHabit(
        id: Int,
        title: String,
        description: String,
        frequency: String,
        targetPerWeek: Int
    ) {
        viewModelScope.launch {

            val existingHabit = habitRepository
                .getHabitById(id)
                .firstOrNull() ?: return@launch

            habitRepository.updateHabit(
                existingHabit.copy(
                    title = title,
                    description = description,
                    frequency = frequency,
                    targetPerWeek = targetPerWeek
                )
            )
        }
    }

    // DELETE
    fun deleteHabit(id: Int) {
        viewModelScope.launch {
            habitRepository.deleteHabitById(id)
        }
    }

    // COMPLETE / UNCOMPLETE
    fun toggleHabitCompletion(
        habitId: Int,
        completed: Boolean
    ) {
        viewModelScope.launch {

            if (completed) {
                habitRepository.markHabitCompleted(
                    habitId = habitId,
                    date = today
                )
            } else {
                habitRepository.unmarkHabitCompleted(
                    habitId = habitId,
                    date = today
                )
            }
        }
    }
}
