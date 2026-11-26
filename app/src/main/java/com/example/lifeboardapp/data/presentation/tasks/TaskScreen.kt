package com.example.lifeboardapp.data.presentation.tasks


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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
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
import com.example.lifeboardapp.data.local.entity.TaskEntity
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class TaskUiState(
    val tasks: List<TaskEntity> = emptyList(),
    val isLoading: Boolean = true
)

private enum class TaskFilter(val label: String) {
    ALL("All"),
    PENDING("Pending"),
    COMPLETED("Completed")
}

@Composable
fun TaskScreen(
    state: TaskUiState,
    onAddTask: (String, String, Int, Long?) -> Unit,
    onUpdateTask: (Int, String, String, Int, Long?) -> Unit,
    onDeleteTask: (Int) -> Unit,
    onToggleTask: (Int, Boolean) -> Unit
) {
    var selectedFilter by remember {
        mutableStateOf(TaskFilter.ALL)
    }

    var showForm by remember {
        mutableStateOf(false)
    }

    var editingTask by remember {
        mutableStateOf<TaskEntity?>(null)
    }

    var taskToDelete by remember {
        mutableStateOf<TaskEntity?>(null)
    }

    val filteredTasks = when (selectedFilter) {
        TaskFilter.ALL -> state.tasks
        TaskFilter.PENDING -> state.tasks.filter { !it.isCompleted }
        TaskFilter.COMPLETED -> state.tasks.filter { it.isCompleted }
    }

    val pendingCount = state.tasks.count { !it.isCompleted }
    val completedCount = state.tasks.count { it.isCompleted }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    editingTask = null
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
                        text = "My Tasks",
                        style = MaterialTheme.typography.headlineLarge
                    )

                    Spacer(Modifier.height(6.dp))

                    Text(
                        text = "Plan your work and get things done.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            item {
                TaskSummaryCard(
                    pending = pendingCount,
                    completed = completedCount
                )
            }

            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TaskFilter.entries.forEach { filter ->
                        FilterChip(
                            selected = selectedFilter == filter,
                            onClick = {
                                selectedFilter = filter
                            },
                            label = {
                                Text(filter.label)
                            }
                        )
                    }
                }
            }

            if (state.isLoading) {
                item {
                    Text("Loading tasks...")
                }
            } else if (filteredTasks.isEmpty()) {
                item {
                    EmptyTaskCard(
                        message = when (selectedFilter) {
                            TaskFilter.ALL ->
                                "No tasks yet. Create your first task."

                            TaskFilter.PENDING ->
                                "You're all caught up! No pending tasks."

                            TaskFilter.COMPLETED ->
                                "No completed tasks yet."
                        },
                        onAddTask = {
                            editingTask = null
                            showForm = true
                        }
                    )
                }
            } else {
                items(
                    items = filteredTasks,
                    key = { it.id }
                ) { task ->

                    TaskCard(
                        task = task,
                        onToggle = { completed ->
                            onToggleTask(task.id, completed)
                        },
                        onEdit = {
                            editingTask = task
                            showForm = true
                        },
                        onDelete = {
                            taskToDelete = task
                        }
                    )
                }
            }
        }
    }

    if (showForm) {
        TaskFormDialog(
            task = editingTask,
            onDismiss = {
                showForm = false
                editingTask = null
            },
            onSave = { title, description, priority, dueDate ->

                val existingTask = editingTask

                if (existingTask == null) {
                    onAddTask(
                        title,
                        description,
                        priority,
                        dueDate
                    )
                } else {
                    onUpdateTask(
                        existingTask.id,
                        title,
                        description,
                        priority,
                        dueDate
                    )
                }

                showForm = false
                editingTask = null
            }
        )
    }

    taskToDelete?.let { task ->
        AlertDialog(
            onDismissRequest = {
                taskToDelete = null
            },
            title = {
                Text("Delete task?")
            },
            text = {
                Text("Are you sure you want to delete \"${task.title}\"?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteTask(task.id)
                        taskToDelete = null
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
                        taskToDelete = null
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun TaskSummaryCard(
    pending: Int,
    completed: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Column {
                Text(
                    text = pending.toString(),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "Pending",
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Column {
                Text(
                    text = completed.toString(),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "Completed",
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Composable
private fun TaskCard(
    task: TaskEntity,
    onToggle: (Boolean) -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val today = LocalDate.now().toEpochDay()

    val isOverdue = !task.isCompleted &&
            task.dueDateEpochDay != null &&
            task.dueDateEpochDay < today

    val dueDateText = task.dueDateEpochDay?.let {
        LocalDate.ofEpochDay(it).format(
            DateTimeFormatter.ofPattern("dd MMM yyyy")
        )
    }

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
                androidx.compose.material3.Checkbox(
                    checked = task.isCompleted,
                    onCheckedChange = onToggle
                )

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.titleMedium
                    )

                    if (task.description.isNotBlank()) {
                        Text(
                            text = task.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = when (task.priority) {
                        3 -> "High priority"
                        2 -> "Medium priority"
                        else -> "Low priority"
                    },
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelMedium
                )

                if (dueDateText != null) {
                    Text(
                        text = "Due: $dueDateText",
                        color = if (isOverdue) {
                            MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }

            if (isOverdue) {
                Text(
                    text = "Overdue",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall
                )
            }

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
private fun TaskFormDialog(
    task: TaskEntity?,
    onDismiss: () -> Unit,
    onSave: (String, String, Int, Long?) -> Unit
) {
    var title by remember(task?.id) {
        mutableStateOf(task?.title ?: "")
    }

    var description by remember(task?.id) {
        mutableStateOf(task?.description ?: "")
    }

    var priority by remember(task?.id) {
        mutableStateOf(task?.priority ?: 1)
    }

    var dueDate by remember(task?.id) {
        mutableStateOf(
            task?.dueDateEpochDay?.let {
                LocalDate.ofEpochDay(it).toString()
            } ?: ""
        )
    }

    var priorityMenuExpanded by remember {
        mutableStateOf(false)
    }

    val parsedDate = if (dueDate.isBlank()) {
        null
    } else {
        runCatching {
            LocalDate.parse(dueDate).toEpochDay()
        }.getOrNull()
    }

    val dateIsValid = dueDate.isBlank() || parsedDate != null

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(if (task == null) "Create task" else "Edit task")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Task title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )

                Column {
                    OutlinedButton(
                        onClick = {
                            priorityMenuExpanded = true
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "Priority: " + when (priority) {
                                3 -> "High"
                                2 -> "Medium"
                                else -> "Low"
                            }
                        )
                    }

                    DropdownMenu(
                        expanded = priorityMenuExpanded,
                        onDismissRequest = {
                            priorityMenuExpanded = false
                        }
                    ) {
                        DropdownMenuItem(
                            text = { Text("High") },
                            onClick = {
                                priority = 3
                                priorityMenuExpanded = false
                            }
                        )

                        DropdownMenuItem(
                            text = { Text("Medium") },
                            onClick = {
                                priority = 2
                                priorityMenuExpanded = false
                            }
                        )

                        DropdownMenuItem(
                            text = { Text("Low") },
                            onClick = {
                                priority = 1
                                priorityMenuExpanded = false
                            }
                        )
                    }
                }

                OutlinedTextField(
                    value = dueDate,
                    onValueChange = { dueDate = it },
                    label = { Text("Due date (YYYY-MM-DD)") },
                    placeholder = { Text("2026-12-31") },
                    singleLine = true,
                    isError = !dateIsValid,
                    supportingText = {
                        Text(
                            if (!dateIsValid) {
                                "Enter a valid date in YYYY-MM-DD format."
                            } else {
                                "Optional. Leave blank if there is no due date."
                            }
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                enabled = title.isNotBlank() && dateIsValid,
                onClick = {
                    onSave(
                        title.trim(),
                        description.trim(),
                        priority,
                        parsedDate
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

@Composable
private fun EmptyTaskCard(
    message: String,
    onAddTask: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium
            )

            Button(onClick = onAddTask) {
                Text("Add task")
            }
        }
    }
}
