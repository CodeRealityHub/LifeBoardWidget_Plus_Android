package com.example.lifeboardapp.data.repository

import com.example.lifeboardapp.data.local.dao.TaskDao
import com.example.lifeboardapp.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

class TaskRepository(
    private val taskDao: TaskDao
) {

    fun getAllTasks(): Flow<List<TaskEntity>> =
        taskDao.getAllTasks()

    fun getPendingTasks(): Flow<List<TaskEntity>> =
        taskDao.getPendingTasks()

    fun getCompletedTasks(): Flow<List<TaskEntity>> =
        taskDao.getCompletedTasks()

    fun getTaskById(id: Int): Flow<TaskEntity?> =
        taskDao.getTaskById(id)

    fun getTasksByDate(date: Long): Flow<List<TaskEntity>> =
        taskDao.getTasksByDate(date)

    suspend fun createTask(task: TaskEntity): Long =
        taskDao.insertTask(task)

    suspend fun updateTask(task: TaskEntity) {
        taskDao.updateTask(task)
    }

    suspend fun updateTaskStatus(
        id: Int,
        completed: Boolean
    ) {
        taskDao.updateTaskStatus(id, completed)
    }

    suspend fun deleteTask(task: TaskEntity) {
        taskDao.deleteTask(task)
    }

    suspend fun deleteTaskById(id: Int) {
        taskDao.deleteTaskById(id)
    }

    suspend fun deleteCompletedTasks() {
        taskDao.deleteCompletedTasks()
    }
}