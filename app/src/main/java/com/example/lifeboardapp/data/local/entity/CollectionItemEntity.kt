package com.example.lifeboardapp.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "collection_items",

    foreignKeys = [
        ForeignKey(
            entity = CollectionEntity::class,
            parentColumns = ["id"],
            childColumns = ["collectionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],

    indices = [
        Index("collectionId")
    ]
)
data class CollectionItemEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val collectionId: Int,

    val title: String,

    val description: String = "",

    val imageUri: String? = null,

    val targetDate: Long? = null,

    val targetPrice: Double? = null,

    val externalUrl: String? = null,

    // For wishlist: purchased
    // For bucket list: completed
    val isCompleted: Boolean = false,

    val createdAt: Long = System.currentTimeMillis(),

    val updatedAt: Long = System.currentTimeMillis()
)