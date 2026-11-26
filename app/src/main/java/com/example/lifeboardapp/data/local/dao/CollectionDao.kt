package com.example.lifeboardapp.data.local.dao

import androidx.room.*
import com.example.lifeboardapp.data.local.entity.CollectionEntity
import com.example.lifeboardapp.data.local.entity.CollectionItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CollectionDao {

    // COLLECTION CRUD

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertCollection(
        collection: CollectionEntity
    ): Long

    @Query("""
        SELECT * FROM collections
        ORDER BY updatedAt DESC
    """)
    fun getAllCollections(): Flow<List<CollectionEntity>>

    @Query("""
    SELECT * FROM collections
    WHERE type = :type
    ORDER BY createdAt DESC
""")
    fun getCollectionsByType(
        type: String
    ): Flow<List<CollectionEntity>>
    @Query("""
        SELECT * FROM collections
        WHERE type = 'WISHLIST'
        ORDER BY createdAt DESC
    """)
    fun getWishlistCollections(): Flow<List<CollectionEntity>>

    @Query("""
        SELECT * FROM collections
        WHERE type = 'BUCKET_LIST'
        ORDER BY createdAt DESC
    """)
    fun getBucketListCollections(): Flow<List<CollectionEntity>>

    @Query("""
        SELECT * FROM collections
        WHERE id = :collectionId
        LIMIT 1
    """)
    fun getCollectionById(
        collectionId: Int
    ): Flow<CollectionEntity?>

    @Update
    suspend fun updateCollection(
        collection: CollectionEntity
    )

    @Query("""
        DELETE FROM collections
        WHERE id = :collectionId
    """)
    suspend fun deleteCollectionById(
        collectionId: Int
    )

    // COLLECTION ITEM CRUD

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertCollectionItem(
        item: CollectionItemEntity
    ): Long

    @Query("""
        SELECT * FROM collection_items
        WHERE collectionId = :collectionId
        ORDER BY createdAt DESC
    """)
    fun getCollectionItems(
        collectionId: Int
    ): Flow<List<CollectionItemEntity>>

    @Query("""
        SELECT * FROM collection_items
        WHERE id = :itemId
        LIMIT 1
    """)
    fun getCollectionItemById(
        itemId: Int
    ): Flow<CollectionItemEntity?>

    @Update
    suspend fun updateCollectionItem(
        item: CollectionItemEntity
    )

    @Query("""
        UPDATE collection_items
        SET isCompleted = :completed,
            updatedAt = :updatedAt
        WHERE id = :itemId
    """)
    suspend fun updateItemStatus(
        itemId: Int,
        completed: Boolean,
        updatedAt: Long = System.currentTimeMillis()
    )

    @Query("""
        DELETE FROM collection_items
        WHERE id = :itemId
    """)
    suspend fun deleteCollectionItem(
        itemId: Int
    )

    @Query("""
        SELECT COUNT(*) FROM collection_items
        WHERE collectionId = :collectionId
        AND isCompleted = 1
    """)
    fun getCompletedItemCount(
        collectionId: Int
    ): Flow<Int>

}