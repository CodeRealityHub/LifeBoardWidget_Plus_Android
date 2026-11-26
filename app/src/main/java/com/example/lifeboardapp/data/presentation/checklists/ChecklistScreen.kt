package com.example.lifeboardapp.data.presentation.checklists


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import com.example.lifeboardapp.data.local.entity.ChecklistEntity
import com.example.lifeboardapp.data.local.entity.ChecklistItemEntity

data class ChecklistUiState(
    val checklists: List<ChecklistEntity> = emptyList(),
    val selectedChecklist: ChecklistEntity? = null,
    val items: List<ChecklistItemEntity> = emptyList(),
    val completedItems: Int = 0,
    val totalItems: Int = 0,
    val isLoading: Boolean = true
)

@Composable
fun ChecklistScreen(
    state: ChecklistUiState,
    onSelectChecklist: (Int) -> Unit,
    onAddChecklist: (String, String) -> Unit,
    onUpdateChecklist: (Int, String, String) -> Unit,
    onDeleteChecklist: (Int) -> Unit,
    onAddItem: (String) -> Unit,
    onUpdateItem: (Int, String) -> Unit,
    onToggleItem: (Int, Boolean) -> Unit,
    onDeleteItem: (Int) -> Unit
) {
    var showChecklistDialog by remember { mutableStateOf(false) }
    var showItemDialog by remember { mutableStateOf(false) }

    var editingChecklist by remember {
        mutableStateOf<ChecklistEntity?>(null)
    }

    var editingItem by remember {
        mutableStateOf<ChecklistItemEntity?>(null)
    }

    var checklistToDelete by remember {
        mutableStateOf<ChecklistEntity?>(null)
    }

    var itemToDelete by remember {
        mutableStateOf<ChecklistItemEntity?>(null)
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    editingItem = null
                    showItemDialog = true
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Text("+")
            }
        }
    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Column {
                    Text(
                        text = "Checklists",
                        style = MaterialTheme.typography.headlineLarge
                    )

                    Spacer(Modifier.height(6.dp))

                    Text(
                        text = "Organize everything, one item at a time.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            item {
                Button(
                    onClick = {
                        editingChecklist = null
                        showChecklistDialog = true
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Create checklist")
                }
            }

            if (state.checklists.isNotEmpty()) {
                item {
                    Text(
                        text = "Your lists",
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                item {
                    androidx.compose.foundation.lazy.LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(
                            items = state.checklists,
                            key = { it.id }
                        ) { checklist ->

                            FilterChip(
                                selected = state.selectedChecklist?.id == checklist.id,
                                onClick = {
                                    onSelectChecklist(checklist.id)
                                },
                                label = {
                                    Text(checklist.title)
                                }
                            )
                        }
                    }
                }
            }

            val selected = state.selectedChecklist

            if (selected != null) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = selected.title,
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )

                            if (selected.description.isNotBlank()) {
                                Text(
                                    text = selected.description,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }

                            Text(
                                text = "${state.completedItems} of ${state.totalItems} completed",
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )

                            LinearProgressIndicator(
                                progress = {
                                    if (state.totalItems == 0) 0f
                                    else state.completedItems.toFloat() /
                                            state.totalItems
                                },
                                modifier = Modifier.fillMaxWidth(),
                                color = MaterialTheme.colorScheme.primary
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(
                                    onClick = {
                                        editingChecklist = selected
                                        showChecklistDialog = true
                                    }
                                ) {
                                    Text("Edit list")
                                }

                                TextButton(
                                    onClick = {
                                        checklistToDelete = selected
                                    }
                                ) {
                                    Text(
                                        "Delete list",
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    Text(
                        text = "Checklist items",
                        style = MaterialTheme.typography.titleLarge
                    )
                }

                if (state.items.isEmpty()) {
                    item {
                        Text(
                            text = "No items yet. Add your first item.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    items(
                        items = state.items,
                        key = { it.id }
                    ) { checklistItem ->

                        ChecklistItemCard(
                            item = checklistItem,
                            onToggle = { checked ->
                                onToggleItem(checklistItem.id, checked)
                            },
                            onEdit = {
                                editingItem = checklistItem
                                showItemDialog = true
                            },
                            onDelete = {
                                itemToDelete = checklistItem
                            }
                        )
                    }
                }
            } else if (!state.isLoading) {
                item {
                    Text(
                        text = "Create your first checklist to get started.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }

    if (showChecklistDialog) {
        ChecklistFormDialog(
            checklist = editingChecklist,
            onDismiss = {
                showChecklistDialog = false
                editingChecklist = null
            },
            onSave = { title, description ->
                val existing = editingChecklist

                if (existing == null) {
                    onAddChecklist(title, description)
                } else {
                    onUpdateChecklist(
                        existing.id,
                        title,
                        description
                    )
                }

                showChecklistDialog = false
                editingChecklist = null
            }
        )
    }

    if (showItemDialog) {
        ChecklistItemFormDialog(
            item = editingItem,
            onDismiss = {
                showItemDialog = false
                editingItem = null
            },
            onSave = { title ->
                val existing = editingItem

                if (existing == null) {
                    onAddItem(title)
                } else {
                    onUpdateItem(existing.id, title)
                }

                showItemDialog = false
                editingItem = null
            }
        )
    }

    checklistToDelete?.let { checklist ->
        AlertDialog(
            onDismissRequest = {
                checklistToDelete = null
            },
            title = { Text("Delete checklist?") },
            text = {
                Text("All items in \"${checklist.title}\" will also be deleted.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteChecklist(checklist.id)
                        checklistToDelete = null
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
                    onClick = { checklistToDelete = null }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    itemToDelete?.let { item ->
        AlertDialog(
            onDismissRequest = {
                itemToDelete = null
            },
            title = { Text("Delete item?") },
            text = { Text("Delete \"${item.title}\"?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteItem(item.id)
                        itemToDelete = null
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
                    onClick = { itemToDelete = null }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun ChecklistItemCard(
    item: ChecklistItemEntity,
    onToggle: (Boolean) -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = item.isCompleted,
                onCheckedChange = onToggle
            )

            Text(
                text = item.title,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge,
                color = if (item.isCompleted) {
                    MaterialTheme.colorScheme.onSurfaceVariant
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )

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

@Composable
private fun ChecklistFormDialog(
    checklist: ChecklistEntity?,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var title by remember(checklist?.id) {
        mutableStateOf(checklist?.title ?: "")
    }

    var description by remember(checklist?.id) {
        mutableStateOf(checklist?.description ?: "")
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(if (checklist == null) "New checklist" else "Edit checklist")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Checklist name") },
                    singleLine = true
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") }
                )
            }
        },
        confirmButton = {
            Button(
                enabled = title.isNotBlank(),
                onClick = {
                    onSave(title.trim(), description.trim())
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun ChecklistItemFormDialog(
    item: ChecklistItemEntity?,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var title by remember(item?.id) {
        mutableStateOf(item?.title ?: "")
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(if (item == null) "Add item" else "Edit item")
        },
        text = {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Item name") },
                singleLine = true
            )
        },
        confirmButton = {
            Button(
                enabled = title.isNotBlank(),
                onClick = {
                    onSave(title.trim())
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
