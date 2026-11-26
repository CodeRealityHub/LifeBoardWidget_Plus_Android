package com.example.lifeboardapp.data.local.dao

import androidx.room.*
import com.example.lifeboardapp.data.local.entity.HabitEntity
import com.example.lifeboardapp.data.local.entity.HabitCompletionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {

    // CREATE
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertHabit(habit: HabitEntity): Long

    // READ
    @Query("SELECT * FROM habits ORDER BY createdAt DESC")
    fun getAllHabits(): Flow<List<HabitEntity>>

    @Query("SELECT * FROM habits WHERE isActive = 1 ORDER BY createdAt DESC")
    fun getActiveHabits(): Flow<List<HabitEntity>>

    @Query("SELECT * FROM habits WHERE id = :habitId LIMIT 1")
    fun getHabitById(habitId: Int): Flow<HabitEntity?>

    // UPDATE
    @Update
    suspend fun updateHabit(habit: HabitEntity)

    // DELETE
    @Delete
    suspend fun deleteHabit(habit: HabitEntity)

    @Query("DELETE FROM habits WHERE id = :habitId")
    suspend fun deleteHabitById(habitId: Int)

    // HABIT COMPLETION
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompletion(
        completion: HabitCompletionEntity
    )

    @Query("""
        SELECT * FROM habit_completions
        WHERE habitId = :habitId
        ORDER BY dateEpochDay DESC
    """)
    fun getHabitHistory(
        habitId: Int
    ): Flow<List<HabitCompletionEntity>>

    @Query("""
        SELECT * FROM habit_completions
        WHERE habitId = :habitId
        AND dateEpochDay = :dateEpochDay
        LIMIT 1
    """)
    suspend fun getCompletion(
        habitId: Int,
        dateEpochDay: Long
    ): HabitCompletionEntity?

    @Query("""
        DELETE FROM habit_completions
        WHERE habitId = :habitId
        AND dateEpochDay = :dateEpochDay
    """)
    suspend fun deleteCompletion(
        habitId: Int,
        dateEpochDay: Long
    )

    @Query("""
        SELECT COUNT(*) FROM habit_completions
        WHERE habitId = :habitId
        AND isCompleted = 1
    """)
    fun getCompletedCount(habitId: Int): Flow<Int>

    @Query("""
    SELECT COUNT(*) FROM habit_completions
    WHERE dateEpochDay = :dateEpochDay
    AND isCompleted = 1
""")
    fun getCompletedHabitsForDate(
        dateEpochDay: Long
    ): Flow<Int>

    @Query("""
    SELECT habitId
    FROM habit_completions
    WHERE dateEpochDay = :dateEpochDay
    AND isCompleted = 1
""")
    fun getCompletedHabitIdsForDate(
        dateEpochDay: Long
    ): Flow<List<Int>>
}