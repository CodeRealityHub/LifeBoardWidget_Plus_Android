package com.example.lifeboardapp.di

import android.content.Context
import com.example.lifeboardapp.data.local.LifeBoardDatabase
import com.example.lifeboardapp.data.repository.HabitRepository
import com.example.lifeboardapp.data.repository.TaskRepository
import com.example.lifeboardapp.data.repository.ChecklistRepository
import com.example.lifeboardapp.data.repository.CollectionRepository
import com.example.lifeboardapp.data.repository.JournalRepository

class AppContainer(context: Context) {

    // ONE CENTRAL DATABASE
    private val database =
        LifeBoardDatabase.getDatabase(context)

    // Repositories share the same database

    val habitRepository by lazy {
        HabitRepository(database.habitDao())
    }

    val taskRepository by lazy {
        TaskRepository(database.taskDao())
    }

    val checklistRepository by lazy {
        ChecklistRepository(database.checklistDao())
    }

    val collectionRepository by lazy {
        CollectionRepository(database.collectionDao())
    }

    val journalRepository by lazy {
        JournalRepository(database.journalDao())
    }
}