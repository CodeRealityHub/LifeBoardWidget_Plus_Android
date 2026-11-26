package com.example.lifeboardapp.data.local.dao

import androidx.room.*
import com.example.lifeboardapp.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    // CREATE
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertTask(task: TaskEntity): Long

    // READ
    @Query("SELECT * FROM tasks ORDER BY createdAt DESC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query("""
        SELECT * FROM tasks
        WHERE isCompleted = 0
        ORDER BY priority DESC, createdAt DESC
    """)
    fun getPendingTasks(): Flow<List<TaskEntity>>

    @Query("""
        SELECT * FROM tasks
        WHERE isCompleted = 1
        ORDER BY createdAt DESC
    """)
    fun getCompletedTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id = :taskId LIMIT 1")
    fun getTaskById(taskId: Int): Flow<TaskEntity?>

    @Query("""
        SELECT * FROM tasks
        WHERE dueDateEpochDay = :date
        ORDER BY priority DESC
    """)
    fun getTasksByDate(date: Long): Flow<List<TaskEntity>>

    // UPDATE
    @Update
    suspend fun updateTask(task: TaskEntity)

    @Query("""
        UPDATE tasks
        SET isCompleted = :completed
        WHERE id = :taskId
    """)
    suspend fun updateTaskStatus(
        taskId: Int,
        completed: Boolean
    )

    // DELETE
    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Query("DELETE FROM tasks WHERE id = :taskId")
    suspend fun deleteTaskById(taskId: Int)

    @Query("DELETE FROM tasks WHERE isCompleted = 1")
    suspend fun deleteCompletedTasks()
}