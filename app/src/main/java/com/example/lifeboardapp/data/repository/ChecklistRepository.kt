package com.example.lifeboardapp.data.repository

import com.example.lifeboardapp.data.local.dao.ChecklistDao
import com.example.lifeboardapp.data.local.entity.ChecklistEntity
import com.example.lifeboardapp.data.local.entity.ChecklistItemEntity
import kotlinx.coroutines.flow.Flow

class ChecklistRepository(
    private val checklistDao: ChecklistDao
) {

    // Checklist operations

    fun getAllChecklists(): Flow<List<ChecklistEntity>> =
        checklistDao.getAllChecklists()

    fun getChecklistById(id: Int): Flow<ChecklistEntity?> =
        checklistDao.getChecklistById(id)

    suspend fun createChecklist(
        checklist: ChecklistEntity
    ): Long =
        checklistDao.insertChecklist(checklist)

    suspend fun updateChecklist(
        checklist: ChecklistEntity
    ) {
        checklistDao.updateChecklist(
            checklist.copy(updatedAt = System.currentTimeMillis())
        )
    }

    suspend fun deleteChecklist(id: Int) {
        checklistDao.deleteChecklistById(id)
    }

    // Item operations

    suspend fun createItem(
        item: ChecklistItemEntity
    ): Long =
        checklistDao.insertItem(item)

    fun getChecklistItems(
        checklistId: Int
    ): Flow<List<ChecklistItemEntity>> =
        checklistDao.getChecklistItems(checklistId)

    fun getCompletedItemCount(
        checklistId: Int
    ): Flow<Int> =
        checklistDao.getCompletedItemCount(checklistId)

    fun getTotalItemCount(
        checklistId: Int
    ): Flow<Int> =
        checklistDao.getTotalItemCount(checklistId)

    suspend fun updateItem(item: ChecklistItemEntity) {
        checklistDao.updateItem(item)
    }

    suspend fun toggleItem(
        itemId: Int,
        completed: Boolean
    ) {
        checklistDao.updateItemStatus(itemId, completed)
    }

    suspend fun deleteItem(itemId: Int) {
        checklistDao.deleteItemById(itemId)
    }

    suspend fun deleteAllItems(checklistId: Int) {
        checklistDao.deleteAllItems(checklistId)
    }


}