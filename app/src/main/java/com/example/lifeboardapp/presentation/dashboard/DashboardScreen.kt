
package com.example.lifeboardapp.presentation.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.lifeboardapp.data.local.entity.JournalEntity
import com.example.lifeboardapp.data.local.entity.TaskEntity
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import com.example.lifeboardapp.data.presentation.dashboard.DashboardUiState
import androidx.compose.foundation.layout.statusBarsPadding

@Composable
fun DashboardScreen(
    state: DashboardUiState,
    onNavigate: (String) -> Unit,
    onTaskToggle: (Int, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val today = LocalDate.now().format(
        DateTimeFormatter.ofPattern(
            "EEEE, dd MMMM",
            Locale.getDefault()
        )
    )

    LazyColumn(
        modifier = modifier.fillMaxSize().statusBarsPadding(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        // Header
        item {
            Column {
                Text(
                    text = "LifeBoard",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "Welcome back 👋",
                    style = MaterialTheme.typography.headlineLarge
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = today,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Habit progress
        item {
            HabitProgressCard(
                completed = state.completedHabits,
                total = state.totalHabits,
                progress = state.progress
            )
        }

        // Quick access
        item {
            SectionHeading(title = "Quick access")

            Spacer(Modifier.height(10.dp))

            QuickAccessRow(
                onNavigate = onNavigate
            )
        }

        // Tasks heading
        item {
            SectionHeading(
                title = "Today's tasks",
                action = "View all",
                onAction = {
                    onNavigate("tasks")
                }
            )
        }

        // Pending tasks
        if (state.tasks.isEmpty()) {
            item {
                EmptyStateCard(
                    message = "No pending tasks. Enjoy your free time!"
                )
            }
        } else {
            items(
                items = state.tasks.take(5),
                key = { it.id }
            ) { task ->

                TaskCard(
                    task = task,
                    onComplete = {
                        onTaskToggle(task.id, true)
                    }
                )
            }
        }

        // Journal heading
        item {
            SectionHeading(
                title = "Recent journal",
                action = "View all",
                onAction = {
                    onNavigate("journal")
                }
            )
        }

        // Recent journal entries
        if (state.recentEntries.isEmpty()) {
            item {
                EmptyStateCard(
                    message = "Your journal is waiting for your first entry."
                )
            }
        } else {
            items(
                items = state.recentEntries.take(3),
                key = { it.id }
            ) { entry ->

                JournalCard(entry = entry)
            }
        }

        item {
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun HabitProgressCard(
    completed: Int,
    total: Int,
    progress: Float
) {
    val safeProgress = progress.coerceIn(0f, 1f)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Daily progress",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "$completed of $total habits completed",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                Text(
                    text = "${(safeProgress * 100).toInt()}%",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            LinearProgressIndicator(
                progress = safeProgress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(9.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surface.copy(
                    alpha = 0.5f
                )
            )

            Text(
                text = when {
                    total == 0 ->
                        "Add a habit to start tracking your day."

                    completed >= total ->
                        "Amazing! All habits completed."

                    else ->
                        "Keep going. Small steps make a difference."
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
private fun QuickAccessRow(
    onNavigate: (String) -> Unit
) {
    val destinations = listOf(
        "Habits" to "habits",
        "Tasks" to "tasks",
        "Checklists" to "checklists",
        "Wishlist" to "wishlist",
        "Bucket list" to "bucketlist",
        "Journal" to "journal"
    )

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(destinations) { (label, route) ->

            AssistChip(
                onClick = {
                    onNavigate(route)
                },
                label = {
                    Text(label)
                }
            )
        }
    }
}

@Composable
private fun SectionHeading(
    title: String,
    action: String? = null,
    onAction: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge
        )

        if (action != null && onAction != null) {
            TextButton(
                onClick = onAction
            ) {
                Text(action)
            }
        }
    }
}

@Composable
private fun TaskCard(
    task: TaskEntity,
    onComplete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium
                )

                if (task.description.isNotBlank()) {
                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = task.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(Modifier.height(6.dp))

                Text(
                    text = when (task.priority) {
                        3 -> "High priority"
                        2 -> "Medium priority"
                        else -> "Low priority"
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(Modifier.width(8.dp))

            OutlinedButton(
                onClick = onComplete
            ) {
                Text("Done")
            }
        }
    }
}

@Composable
private fun JournalCard(
    entry: JournalEntity
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = entry.title.ifBlank {
                    "Untitled entry"
                },
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = entry.content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2
            )

            Text(
                text = LocalDate.ofEpochDay(
                    entry.entryDateEpochDay
                ).format(
                    DateTimeFormatter.ofPattern("dd MMM yyyy")
                ),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun EmptyStateCard(
    message: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Text(
            text = message,
            modifier = Modifier.padding(18.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
