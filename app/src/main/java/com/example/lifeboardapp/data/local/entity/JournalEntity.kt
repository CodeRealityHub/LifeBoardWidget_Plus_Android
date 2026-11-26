package com.example.lifeboardapp.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "journal_entries",
    indices = [
        Index("entryDateEpochDay")
    ]
)
data class JournalEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val title: String = "",

    val content: String,

    val mood: String? = null,

    val entryDateEpochDay: Long,

    val createdAt: Long = System.currentTimeMillis(),

    val updatedAt: Long = System.currentTimeMillis()
)