package com.example.lifeboardapp.presentation.habits


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.lifeboardapp.data.local.entity.HabitEntity

data class HabitUiState(
    val habits: List<HabitEntity> = emptyList(),
    val completedHabitIds: Set<Int> = emptySet()
)

@Composable
fun HabitScreen(
    state: HabitUiState,
    onAddHabit: (
        String,
        String,
        String,
        Int
    ) -> Unit,
    onUpdateHabit: (
        Int,
        String,
        String,
        String,
        Int
    ) -> Unit,
    onDeleteHabit: (Int) -> Unit,
    onToggleCompletion: (Int, Boolean) -> Unit
) {
    var showForm by remember { mutableStateOf(false) }
    var editingHabit by remember { mutableStateOf<HabitEntity?>(null) }
    var habitToDelete by remember { mutableStateOf<HabitEntity?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    editingHabit = null
                    showForm = true
                }
            ) {
                Text("+")
            }
        }
    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            item {
                Column {
                    Text(
                        text = "My Habits",
                        style = MaterialTheme.typography.headlineLarge
                    )

                    Spacer(Modifier.height(6.dp))

                    Text(
                        text = "Build consistency, one day at a time.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            item {
                HabitSummaryCard(
                    completed = state.completedHabitIds.size,
                    total = state.habits.size
                )
            }

            if (state.habits.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "No habits yet",
                                style = MaterialTheme.typography.titleMedium
                            )

                            Text(
                                text = "Create your first habit and start building a better routine.",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Button(
                                onClick = { showForm = true }
                            ) {
                                Text("Create habit")
                            }
                        }
                    }
                }
            } else {
                item {
                    Text(
                        text = "Your daily routine",
                        style = MaterialTheme.typography.titleLarge
                    )
                }

                items(
                    items = state.habits,
                    key = { it.id }
                ) { habit ->

                    HabitCard(
                        habit = habit,
                        isCompleted = habit.id in state.completedHabitIds,
                        onToggle = { completed ->
                            onToggleCompletion(habit.id, completed)
                        },
                        onEdit = {
                            editingHabit = habit
                            showForm = true
                        },
                        onDelete = {
                            habitToDelete = habit
                        }
                    )
                }
            }
        }
    }

    if (showForm) {
        HabitFormDialog(
            habit = editingHabit,
            onDismiss = {
                showForm = false
                editingHabit = null
            },
            onSave = { title, description, frequency, target ->

                val existingHabit = editingHabit

                if (existingHabit == null) {
                    onAddHabit(
                        title,
                        description,
                        frequency,
                        target
                    )
                } else {
                    onUpdateHabit(
                        existingHabit.id,
                        title,
                        description,
                        frequency,
                        target
                    )
                }

                showForm = false
                editingHabit = null
            }
        )
    }

    habitToDelete?.let { habit ->
        AlertDialog(
            onDismissRequest = {
                habitToDelete = null
            },
            title = {
                Text("Delete habit?")
            },
            text = {
                Text("Are you sure you want to delete \"${habit.title}\"?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteHabit(habit.id)
                        habitToDelete = null
                    }
                ) {
                    Text(
                        "Delete",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        habitToDelete = null
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun HabitSummaryCard(
    completed: Int,
    total: Int
) {
    val progress = if (total > 0) {
        completed.toFloat() / total
    } else {
        0f
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Today's progress",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Text(
                text = "$completed / $total completed",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Text(
                text = "${(progress * 100).toInt()}% completed",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
private fun HabitCard(
    habit: HabitEntity,
    isCompleted: Boolean,
    onToggle: (Boolean) -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = isCompleted,
                    onCheckedChange = onToggle
                )

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = habit.title,
                        style = MaterialTheme.typography.titleMedium
                    )

                    if (habit.description.isNotBlank()) {
                        Text(
                            text = habit.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Text(
                text = "${habit.frequency} • Target: ${habit.targetPerWeek}/week",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onEdit) {
                    Text("Edit")
                }

                TextButton(onClick = onDelete) {
                    Text(
                        "Delete",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
private fun HabitFormDialog(
    habit: HabitEntity?,
    onDismiss: () -> Unit,
    onSave: (
        String,
        String,
        String,
        Int
    ) -> Unit
) {
    var title by remember(habit?.id) {
        mutableStateOf(habit?.title ?: "")
    }

    var description by remember(habit?.id) {
        mutableStateOf(habit?.description ?: "")
    }

    var frequency by remember(habit?.id) {
        mutableStateOf(habit?.frequency ?: "DAILY")
    }

    var target by remember(habit?.id) {
        mutableStateOf(
            habit?.targetPerWeek?.toString() ?: "7"
        )
    }

    val targetValue = target.toIntOrNull()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                if (habit == null) "Create habit" else "Edit habit"
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Habit name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = frequency,
                    onValueChange = { frequency = it },
                    label = { Text("Frequency (e.g. DAILY)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = target,
                    onValueChange = { target = it },
                    label = { Text("Target per week (1–7)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                enabled = title.isNotBlank() &&
                        frequency.isNotBlank() &&
                        targetValue != null &&
                        targetValue in 1..7,
                onClick = {
                    onSave(
                        title.trim(),
                        description.trim(),
                        frequency.trim().uppercase(),
                        targetValue!!
                    )
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
