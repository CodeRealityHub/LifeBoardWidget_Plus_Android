package com.example.lifeboardapp.widget

import android.content.Context
import com.example.lifeboardapp.data.local.LifeBoardDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.time.LocalDate

data class LifeBoardWidgetData(
    val activeHabits: Int = 0,
    val completedHabitsToday: Int = 0,
    val pendingTasks: List<String> = emptyList(),
    val checklistSummary: List<String> = emptyList(),
    val wishlistSummary: List<String> = emptyList(),
    val bucketListSummary: List<String> = emptyList(),
    val latestJournalTitle: String = "No journal entries yet",
    val latestJournalPreview: String = "Your recent thoughts will appear here."
)

object LifeBoardWidgetDataReader {

    suspend fun load(context: Context): LifeBoardWidgetData =
        withContext(Dispatchers.IO) {
            val database = LifeBoardDatabase.getDatabase(context)
            val today = LocalDate.now().toEpochDay()

            val habits = database.habitDao()
                .getActiveHabits()
                .first()

            val completedHabitIds = database.habitDao()
                .getCompletedHabitIdsForDate(today)
                .first()
                .toSet()

            val tasks = database.taskDao()
                .getPendingTasks()
                .first()

            val checklists = database.checklistDao()
                .getAllChecklists()
                .first()

            val checklistSummary = checklists.map { checklist ->
                val items = database.checklistDao()
                    .getChecklistItems(checklist.id)
                    .first()

                val completed = items.count { it.isCompleted }

                "${checklist.title}: $completed/${items.size}"
            }

            val wishlist = database.collectionDao()
                .getWishlistCollections()
                .first()

            val wishlistSummary = wishlist.map { collection ->
                val items = database.collectionDao()
                    .getCollectionItems(collection.id)
                    .first()

                val purchased = items.count { it.isCompleted }

                "${collection.title}: ${items.size - purchased} remaining"
            }

            val bucketLists = database.collectionDao()
                .getBucketListCollections()
                .first()

            val bucketListSummary = bucketLists.map { collection ->
                val items = database.collectionDao()
                    .getCollectionItems(collection.id)
                    .first()

                val completed = items.count { it.isCompleted }

                "${collection.title}: $completed/${items.size} completed"
            }

            val latestJournal = database.journalDao()
                .getAllEntries()
                .first()
                .firstOrNull()

            LifeBoardWidgetData(
                activeHabits = habits.size,
                completedHabitsToday = habits.count {
                    it.id in completedHabitIds
                },
                pendingTasks = tasks.take(4).map { it.title },
                checklistSummary = checklistSummary.take(3),
                wishlistSummary = wishlistSummary.take(2),
                bucketListSummary = bucketListSummary.take(2),
                latestJournalTitle = latestJournal
                    ?.title
                    ?.ifBlank { "Untitled entry" }
                    ?: "No journal entries yet",
                latestJournalPreview = latestJournal
                    ?.content
                    ?.take(100)
                    ?: "Your recent thoughts will appear here."
            )
        }
}