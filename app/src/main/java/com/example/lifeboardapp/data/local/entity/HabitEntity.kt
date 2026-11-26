package com.example.lifeboardapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class HabitEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val title: String,

    val description: String = "",

    val frequency: String = "DAILY",

    val targetPerWeek: Int = 7,

    val createdAt: Long = System.currentTimeMillis(),

    val isActive: Boolean = true
)