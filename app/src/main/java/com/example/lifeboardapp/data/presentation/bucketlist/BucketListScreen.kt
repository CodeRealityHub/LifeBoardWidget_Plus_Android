package com.example.lifeboardapp.data.presentation.bucketlist

import android.os.Build
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
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.lifeboardapp.data.local.entity.CollectionItemEntity
import com.example.lifeboardapp.data.presentation.bucketlist.BucketListUiState
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private enum class BucketListFilter(val label: String) {
    ALL("All"),
    TO_DO("To do"),
    COMPLETED("Completed")
}

@Composable
fun BucketListScreen(
    state: BucketListUiState,
    onAddItem: (
        String, String, String?, Long?, Double?, String?
    ) -> Unit,
    onUpdateItem: (
        Int, String, String, String?, Long?, Double?, String?
    ) -> Unit,
    onDeleteItem: (Int) -> Unit,
    onToggleCompleted: (Int, Boolean) -> Unit
) {
    var selectedFilter by remember {
        mutableStateOf(BucketListFilter.ALL)
    }

    var showForm by remember { mutableStateOf(false) }

    var editingItem by remember {
        mutableStateOf<CollectionItemEntity?>(null)
    }

    var itemToDelete by remember {
        mutableStateOf<CollectionItemEntity?>(null)
    }

    val completedCount = state.items.count { it.isCompleted }
    val pendingCount = state.items.count { !it.isCompleted }

    val filteredItems = when (selectedFilter) {
        BucketListFilter.ALL -> state.items
        BucketListFilter.TO_DO ->
            state.items.filter { !it.isCompleted }

        BucketListFilter.COMPLETED ->
            state.items.filter { it.isCompleted }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    editingItem = null
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

            // Header
            item {
                Column {
                    Text(
                        text = "My Bucket List",
                        style = MaterialTheme.typography.headlineLarge
                    )

                    Spacer(Modifier.height(6.dp))

                    Text(
                        text = "Keep track of experiences and goals you want to achieve.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Summary
            item {
                BucketListSummaryCard(
                    total = state.items.size,
                    pending = pendingCount,
                    completed = completedCount
                )
            }

            // Filters
            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    BucketListFilter.values().forEach { filter ->
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

            // Loading and empty states
            if (state.isLoading) {
                item {
                    Text("Loading bucket list...")
                }
            } else if (filteredItems.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor =
                                MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement =
                                Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = when (selectedFilter) {
                                    BucketListFilter.ALL ->
                                        "Your bucket list is empty."

                                    BucketListFilter.TO_DO ->
                                        "No pending goals in this filter."

                                    BucketListFilter.COMPLETED ->
                                        "No completed goals yet."
                                },
                                style = MaterialTheme.typography.bodyLarge
                            )

                            Button(
                                onClick = {
                                    editingItem = null
                                    showForm = true
                                }
                            ) {
                                Text("Add bucket list item")
                            }
                        }
                    }
                }
            } else {
                items(
                    items = filteredItems,
                    key = { it.id }
                ) { item ->

                    BucketListItemCard(
                        item = item,
                        onToggle = { completed ->
                            onToggleCompleted(item.id, completed)
                        },
                        onEdit = {
                            editingItem = item
                            showForm = true
                        },
                        onDelete = {
                            itemToDelete = item
                        }
                    )
                }
            }
        }
    }

    // Add/Edit dialog
    if (showForm) {
        BucketListItemFormDialog(
            item = editingItem,
            onDismiss = {
                showForm = false
                editingItem = null
            },
            onSave = {
                    title,
                    description,
                    imageUri,
                    targetDate,
                    targetPrice,
                    externalUrl ->

                val existing = editingItem

                if (existing == null) {
                    onAddItem(
                        title,
                        description,
                        imageUri,
                        targetDate,
                        targetPrice,
                        externalUrl
                    )
                } else {
                    onUpdateItem(
                        existing.id,
                        title,
                        description,
                        imageUri,
                        targetDate,
                        targetPrice,
                        externalUrl
                    )
                }

                showForm = false
                editingItem = null
            }
        )
    }

    // Delete confirmation
    itemToDelete?.let { item ->
        AlertDialog(
            onDismissRequest = {
                itemToDelete = null
            },
            title = {
                Text("Delete bucket list item?")
            },
            text = {
                Text("Are you sure you want to delete \"${item.title}\"?")
            },
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
                    onClick = {
                        itemToDelete = null
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun BucketListSummaryCard(
    total: Int,
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
            SummaryValue(
                value = total,
                label = "Total"
            )

            SummaryValue(
                value = pending,
                label = "To do"
            )

            SummaryValue(
                value = completed,
                label = "Completed"
            )
        }
    }
}

@Composable
private fun SummaryValue(
    value: Int,
    label: String
) {
    Column {
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )

        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
private fun BucketListItemCard(
    item: CollectionItemEntity,
    onToggle: (Boolean) -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val targetDateText = item.targetDate?.let {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDate.ofEpochDay(it).format(
                DateTimeFormatter.ofPattern("dd MMM yyyy")
            )
        } else {
            TODO("VERSION.SDK_INT < O")
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleMedium
                    )

                    if (item.description.isNotBlank()) {
                        Text(
                            text = item.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Text(
                    text = if (item.isCompleted) {
                        "Completed"
                    } else {
                        "To do"
                    },
                    color = if (item.isCompleted) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    style = MaterialTheme.typography.labelMedium
                )
            }

            item.targetPrice?.let { price ->
                Text(
                    text = "Estimated cost: ₹${"%.2f".format(price)}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            targetDateText?.let { date ->
                Text(
                    text = "Target date: $date",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (!item.imageUri.isNullOrBlank()) {
                Text(
                    text = "Image attached",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            if (!item.externalUrl.isNullOrBlank()) {
                Text(
                    text = "Reference link saved",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = {
                        onToggle(!item.isCompleted)
                    }
                ) {
                    Text(
                        if (item.isCompleted) {
                            "Undo completion"
                        } else {
                            "Mark completed"
                        }
                    )
                }

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
private fun BucketListItemFormDialog(
    item: CollectionItemEntity?,
    onDismiss: () -> Unit,
    onSave: (
        String, String, String?, Long?, Double?, String?
    ) -> Unit
) {
    var title by remember(item?.id) {
        mutableStateOf(item?.title ?: "")
    }

    var description by remember(item?.id) {
        mutableStateOf(item?.description ?: "")
    }

    var imageUri by remember(item?.id) {
        mutableStateOf(item?.imageUri ?: "")
    }

    var targetDate by remember(item?.id) {
        mutableStateOf(
            item?.targetDate?.let {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    LocalDate.ofEpochDay(it).toString()
                } else {
                    TODO("VERSION.SDK_INT < O")
                }
            } ?: ""
        )
    }

    var targetPrice by remember(item?.id) {
        mutableStateOf(item?.targetPrice?.toString() ?: "")
    }

    var externalUrl by remember(item?.id) {
        mutableStateOf(item?.externalUrl ?: "")
    }

    val parsedDate = if (targetDate.isBlank()) {
        null
    } else {
        runCatching {
            LocalDate.parse(targetDate).toEpochDay()
        }.getOrNull()
    }

    val parsedPrice = if (targetPrice.isBlank()) {
        null
    } else {
        targetPrice.toDoubleOrNull()
    }

    val dateIsValid = targetDate.isBlank() || parsedDate != null

    val priceIsValid = targetPrice.isBlank() ||
            (parsedPrice != null && parsedPrice >= 0.0)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                if (item == null) {
                    "Add bucket list item"
                } else {
                    "Edit bucket list item"
                }
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Goal or experience") },
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
                    value = targetPrice,
                    onValueChange = { targetPrice = it },
                    label = { Text("Estimated cost (₹)") },
                    singleLine = true,
                    isError = !priceIsValid,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = targetDate,
                    onValueChange = { targetDate = it },
                    label = { Text("Target date (YYYY-MM-DD)") },
                    placeholder = { Text("2026-12-31") },
                    singleLine = true,
                    isError = !dateIsValid,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = imageUri,
                    onValueChange = { imageUri = it },
                    label = { Text("Image URI (optional)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = externalUrl,
                    onValueChange = { externalUrl = it },
                    label = { Text("Reference URL (optional)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                enabled = title.isNotBlank() &&
                        dateIsValid &&
                        priceIsValid,
                onClick = {
                    onSave(
                        title.trim(),
                        description.trim(),
                        imageUri.trim().ifBlank { null },
                        parsedDate,
                        parsedPrice,
                        externalUrl.trim().ifBlank { null }
                    )
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
