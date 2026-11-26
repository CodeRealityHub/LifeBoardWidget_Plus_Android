
package com.example.lifeboardapp.data.repository

import com.example.lifeboardapp.data.local.dao.CollectionDao
import com.example.lifeboardapp.data.local.entity.CollectionEntity
import com.example.lifeboardapp.data.local.entity.CollectionItemEntity
import kotlinx.coroutines.flow.Flow

class CollectionRepository(
    private val collectionDao: CollectionDao
) {

    // Collection operations

    fun getCollectionByType(
        type: String
    ): Flow<List<CollectionEntity>> =
        collectionDao.getCollectionsByType(
            type.uppercase()
        )

    fun getAllCollections(): Flow<List<CollectionEntity>> =
        collectionDao.getAllCollections()

    fun getWishlist(): Flow<List<CollectionEntity>> =
        collectionDao.getCollectionsByType(
            "WISHLIST"
        )

    fun getBucketList(): Flow<List<CollectionEntity>> =
        collectionDao.getCollectionsByType(
            "BUCKET_LIST"
        )

    fun getCollectionById(
        id: Int
    ): Flow<CollectionEntity?> =
        collectionDao.getCollectionById(id)

    suspend fun createCollection(
        collection: CollectionEntity
    ): Long =
        collectionDao.insertCollection(collection)

    suspend fun updateCollection(
        collection: CollectionEntity
    ) {
        collectionDao.updateCollection(
            collection.copy(
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun deleteCollection(id: Int) {
        collectionDao.deleteCollectionById(id)
    }

    // Collection item operations

    suspend fun createItem(
        item: CollectionItemEntity
    ): Long =
        collectionDao.insertCollectionItem(item)

    fun getCollectionItems(
        collectionId: Int
    ): Flow<List<CollectionItemEntity>> =
        collectionDao.getCollectionItems(collectionId)

    fun getCollectionItemById(
        itemId: Int
    ): Flow<CollectionItemEntity?> =
        collectionDao.getCollectionItemById(itemId)

    suspend fun updateItem(
        item: CollectionItemEntity
    ) {
        collectionDao.updateCollectionItem(
            item.copy(
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun toggleItem(
        itemId: Int,
        completed: Boolean
    ) {
        collectionDao.updateItemStatus(
            itemId = itemId,
            completed = completed,
            updatedAt = System.currentTimeMillis()
        )
    }

    suspend fun deleteItem(itemId: Int) {
        collectionDao.deleteCollectionItem(itemId)
    }

    fun getCompletedItemCount(
        collectionId: Int
    ): Flow<Int> =
        collectionDao.getCompletedItemCount(collectionId)
}
