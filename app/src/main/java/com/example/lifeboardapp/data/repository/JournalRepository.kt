
package com.example.lifeboardapp.data.repository

import com.example.lifeboardapp.data.local.dao.JournalDao
import com.example.lifeboardapp.data.local.entity.JournalEntity
import kotlinx.coroutines.flow.Flow

class JournalRepository(
    private val journalDao: JournalDao
) {

    // Create journal entry
    suspend fun insertEntry(
        entry: JournalEntity
    ): Long =
        journalDao.insertEntry(entry)

    // Get all journal entries
    fun getAllEntries(): Flow<List<JournalEntity>> =
        journalDao.getAllEntries()

    // Get entry by ID
    fun getEntryById(
        id: Int
    ): Flow<JournalEntity?> =
        journalDao.getEntryById(id)

    // Update journal entry
    suspend fun updateEntry(
        entry: JournalEntity
    ) {
        journalDao.updateEntry(entry)
    }

    // Delete journal entry
    suspend fun deleteEntryById(id: Int) {
        journalDao.deleteEntryById(id)
    }

    // Additional journal queries

    fun getEntriesByDate(
        date: Long
    ): Flow<List<JournalEntity>> =
        journalDao.getEntriesByDate(date)

    fun searchEntries(
        query: String
    ): Flow<List<JournalEntity>> =
        journalDao.searchEntries(query)

    fun getEntriesByMood(
        mood: String
    ): Flow<List<JournalEntity>> =
        journalDao.getEntriesByMood(mood)
}
