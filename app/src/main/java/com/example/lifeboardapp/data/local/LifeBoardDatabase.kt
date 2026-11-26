package com.example.lifeboardapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

import com.example.lifeboardapp.data.local.dao.HabitDao
import com.example.lifeboardapp.data.local.dao.TaskDao
import com.example.lifeboardapp.data.local.dao.ChecklistDao
import com.example.lifeboardapp.data.local.dao.CollectionDao
import com.example.lifeboardapp.data.local.dao.JournalDao

import com.example.lifeboardapp.data.local.entity.HabitEntity
import com.example.lifeboardapp.data.local.entity.HabitCompletionEntity
import com.example.lifeboardapp.data.local.entity.TaskEntity
import com.example.lifeboardapp.data.local.entity.ChecklistEntity
import com.example.lifeboardapp.data.local.entity.ChecklistItemEntity
import com.example.lifeboardapp.data.local.entity.CollectionEntity
import com.example.lifeboardapp.data.local.entity.CollectionItemEntity
import com.example.lifeboardapp.data.local.entity.JournalEntity

@Database(
    entities = [
        HabitEntity::class,
        HabitCompletionEntity::class,
        TaskEntity::class,
        ChecklistEntity::class,
        ChecklistItemEntity::class,
        CollectionEntity::class,
        CollectionItemEntity::class,
        JournalEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class LifeBoardDatabase : RoomDatabase() {

    abstract fun habitDao(): HabitDao

    abstract fun taskDao(): TaskDao

    abstract fun checklistDao(): ChecklistDao

    abstract fun collectionDao(): CollectionDao

    abstract fun journalDao(): JournalDao

    companion object {

        @Volatile
        private var INSTANCE: LifeBoardDatabase? = null

        fun getDatabase(context: Context): LifeBoardDatabase {

            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LifeBoardDatabase::class.java,
                    "lifeboard.db"
                )
                    .build()

                INSTANCE = instance

                instance
            }
        }
    }
}
