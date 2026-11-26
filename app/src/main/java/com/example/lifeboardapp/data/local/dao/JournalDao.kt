package com.example.lifeboardapp.data.local.dao

import androidx.room.*
import com.example.lifeboardapp.data.local.entity.JournalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface JournalDao {

    // CREATE

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertEntry(
        entry: JournalEntity
    ): Long

    // READ

    @Query("""
        SELECT * FROM journal_entries
        ORDER BY entryDateEpochDay DESC, createdAt DESC
    """)
    fun getAllEntries(): Flow<List<JournalEntity>>

    @Query("""
        SELECT * FROM journal_entries
        WHERE id = :entryId
        LIMIT 1
    """)
    fun getEntryById(
        entryId: Int
    ): Flow<JournalEntity?>

    @Query("""
        SELECT * FROM journal_entries
        WHERE entryDateEpochDay = :date
        ORDER BY createdAt DESC
    """)
    fun getEntriesByDate(
        date: Long
    ): Flow<List<JournalEntity>>

    @Query("""
        SELECT * FROM journal_entries
        WHERE title LIKE '%' || :query || '%'
        OR content LIKE '%' || :query || '%'
        ORDER BY entryDateEpochDay DESC
    """)
    fun searchEntries(
        query: String
    ): Flow<List<JournalEntity>>

    @Query("""
        SELECT * FROM journal_entries
        WHERE mood = :mood
        ORDER BY entryDateEpochDay DESC
    """)
    fun getEntriesByMood(
        mood: String
    ): Flow<List<JournalEntity>>

    // UPDATE

    @Update
    suspend fun updateEntry(
        entry: JournalEntity
    )

    // DELETE

    @Delete
    suspend fun deleteEntry(
        entry: JournalEntity
    )

    @Query("""
        DELETE FROM journal_entries
        WHERE id = :entryId
    """)
    suspend fun deleteEntryById(
        entryId: Int
    )
}