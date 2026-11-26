package com.example.lifeboardapp.data.local.dao

import androidx.room.*
import com.example.lifeboardapp.data.local.entity.ChecklistEntity
import com.example.lifeboardapp.data.local.entity.ChecklistItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChecklistDao {

    // CHECKLIST CRUD

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertChecklist(
        checklist: ChecklistEntity
    ): Long

    @Query("""
        SELECT * FROM checklists
        ORDER BY updatedAt DESC
    """)
    fun getAllChecklists(): Flow<List<ChecklistEntity>>

    @Query("""
        SELECT * FROM checklists
        WHERE id = :checklistId
        LIMIT 1
    """)
    fun getChecklistById(
        checklistId: Int
    ): Flow<ChecklistEntity?>

    @Update
    suspend fun updateChecklist(
        checklist: ChecklistEntity
    )

    @Query("""
        DELETE FROM checklists
        WHERE id = :checklistId
    """)
    suspend fun deleteChecklistById(
        checklistId: Int
    )

    // CHECKLIST ITEM CRUD

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertItem(
        item: ChecklistItemEntity
    ): Long

    @Query("""
        SELECT * FROM checklist_items
        WHERE checklistId = :checklistId
        ORDER BY position ASC, id ASC
    """)
    fun getChecklistItems(
        checklistId: Int
    ): Flow<List<ChecklistItemEntity>>

    @Query("""
        SELECT COUNT(*) FROM checklist_items
        WHERE checklistId = :checklistId
        AND isCompleted = 1
    """)
    fun getCompletedItemCount(
        checklistId: Int
    ): Flow<Int>

    @Query("""
        SELECT COUNT(*) FROM checklist_items
        WHERE checklistId = :checklistId
    """)
    fun getTotalItemCount(
        checklistId: Int
    ): Flow<Int>

    @Update
    suspend fun updateItem(
        item: ChecklistItemEntity
    )

    @Query("""
        UPDATE checklist_items
        SET isCompleted = :completed
        WHERE id = :itemId
    """)
    suspend fun updateItemStatus(
        itemId: Int,
        completed: Boolean
    )

    @Query("""
        DELETE FROM checklist_items
        WHERE id = :itemId
    """)
    suspend fun deleteItemById(
        itemId: Int
    )

    @Query("""
        DELETE FROM checklist_items
        WHERE checklistId = :checklistId
    """)
    suspend fun deleteAllItems(
        checklistId: Int
    )
}