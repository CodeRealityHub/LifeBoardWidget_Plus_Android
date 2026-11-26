package com.example.lifeboardapp.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "checklist_items",

    foreignKeys = [
        ForeignKey(
            entity = ChecklistEntity::class,
            parentColumns = ["id"],
            childColumns = ["checklistId"],
            onDelete = ForeignKey.CASCADE
        )
    ],

    indices = [
        Index("checklistId")
    ]
)
data class ChecklistItemEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val checklistId: Int,

    val title: String,

    val isCompleted: Boolean = false,

    val position: Int = 0,

    val createdAt: Long = System.currentTimeMillis()
)