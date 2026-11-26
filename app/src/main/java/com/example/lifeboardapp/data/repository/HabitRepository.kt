package com.example.lifeboardapp.data.repository

import com.example.lifeboardapp.data.local.dao.HabitDao
import com.example.lifeboardapp.data.local.entity.HabitEntity
import com.example.lifeboardapp.data.local.entity.HabitCompletionEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class HabitRepository(
    private val habitDao: HabitDao
) {

    fun getAllHabits(): Flow<List<HabitEntity>> =
        habitDao.getAllHabits()

    fun getActiveHabits(): Flow<List<HabitEntity>> =
        habitDao.getActiveHabits()

    fun getHabitById(id: Int): Flow<HabitEntity?> =
        habitDao.getHabitById(id)

    fun getCompletedHabitsForDate(
        date: LocalDate = LocalDate.now()
    ): Flow<Int> =
        habitDao.getCompletedHabitsForDate(date.toEpochDay())

    fun getCompletedHabitIdsForDate(
        date: LocalDate = LocalDate.now()
    ): Flow<List<Int>> {
        return habitDao.getCompletedHabitIdsForDate(
            date.toEpochDay()
        )
    }

    suspend fun createHabit(habit: HabitEntity): Long =
        habitDao.insertHabit(habit)

    suspend fun updateHabit(habit: HabitEntity) {
        habitDao.updateHabit(habit)
    }

    suspend fun deleteHabit(habit: HabitEntity) {
        habitDao.deleteHabit(habit)
    }

    suspend fun deleteHabitById(id: Int) {
        habitDao.deleteHabitById(id)
    }

    suspend fun markHabitCompleted(
        habitId: Int,
        date: LocalDate = LocalDate.now()
    ) {
        habitDao.insertCompletion(
            HabitCompletionEntity(
                habitId = habitId,
                dateEpochDay = date.toEpochDay(),
                isCompleted = true
            )
        )
    }

    suspend fun unmarkHabitCompleted(
        habitId: Int,
        date: LocalDate = LocalDate.now()
    ) {
        habitDao.deleteCompletion(
            habitId,
            date.toEpochDay()
        )
    }

    fun getHabitHistory(
        habitId: Int
    ): Flow<List<HabitCompletionEntity>> =
        habitDao.getHabitHistory(habitId)

    fun getCompletedCount(habitId: Int): Flow<Int> =
        habitDao.getCompletedCount(habitId)
}