
package com.example.lifeboardapp.data.presentation.journal

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.lifeboardapp.data.local.entity.JournalEntity
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

data class JournalUiState(
    val entries: List<JournalEntity> = emptyList(),
    val isLoading: Boolean = true
)

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalScreen(
    state: JournalUiState,
    onAddEntry: (String, String, String) -> Unit,
    onUpdateEntry: (Int, String, String, String) -> Unit,
    onDeleteEntry: (Int) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var editingEntry by remember { mutableStateOf<JournalEntity?>(null) }
    var selectedEntry by remember { mutableStateOf<JournalEntity?>(null) }

    var selectedDate by remember {
        mutableStateOf<LocalDate?>(null)
    }

    var currentMonth by remember {
        mutableStateOf(YearMonth.now())
    }

    val filteredEntries = remember(state.entries, selectedDate) {
        selectedDate?.let { date ->
            state.entries.filter {
                it.entryDateEpochDay == date.toEpochDay()
            }
        } ?: state.entries
    }

    val entryDates = remember(state.entries) {
        state.entries.map {
            it.entryDateEpochDay
        }.toSet()
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    editingEntry = null
                    showDialog = true
                }
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add journal entry"
                )
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {

            Spacer(Modifier.height(12.dp))

            Text(
                text = "My Journal",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Capture your thoughts and moments.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(16.dp))

            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 96.dp)
                ) {

                    // Calendar
                    item {
                        JournalCalendar(
                            currentMonth = currentMonth,
                            selectedDate = selectedDate,
                            entryDates = entryDates,
                            onDateSelected = { date ->
                                selectedDate = date
                                currentMonth = YearMonth.from(date)
                            },
                            onPreviousMonth = {
                                currentMonth = currentMonth.minusMonths(1)
                            },
                            onNextMonth = {
                                currentMonth = currentMonth.plusMonths(1)
                            }
                        )
                    }

                    // Entry filter
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (selectedDate == null) {
                                    "All Entries"
                                } else {
                                    selectedDate.toString()
                                },
                                style = MaterialTheme.typography.titleMedium
                            )

                            if (selectedDate != null) {
                                TextButton(
                                    onClick = {
                                        selectedDate = null
                                    }
                                ) {
                                    Text("Clear filter")
                                }
                            }
                        }
                    }

                    // Empty state
                    if (filteredEntries.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = if (selectedDate == null) {
                                            "No journal entries yet"
                                        } else {
                                            "No entries on this date"
                                        },
                                        style = MaterialTheme.typography.titleMedium
                                    )

                                    Spacer(Modifier.height(6.dp))

                                    Text(
                                        text = "Start writing about your day.",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    // Journal entries
                    items(
                        items = filteredEntries.sortedByDescending {
                            it.entryDateEpochDay
                        },
                        key = { it.id }
                    ) { entry ->

                        JournalEntryCard(
                            entry = entry,
                            onClick = {
                                selectedEntry = entry
                            },
                            onEdit = {
                                editingEntry = entry
                                showDialog = true
                            },
                            onDelete = {
                                onDeleteEntry(entry.id)
                            }
                        )
                    }
                }
            }
        }
    }

    // Create / Edit dialog
    if (showDialog) {
        JournalEditorDialog(
            entry = editingEntry,
            onDismiss = {
                showDialog = false
            },
            onSave = { title, content, mood ->

                val existing = editingEntry

                if (existing == null) {
                    onAddEntry(title, content, mood)
                } else {
                    onUpdateEntry(
                        existing.id,
                        title,
                        content,
                        mood
                    )
                }

                showDialog = false
            }
        )
    }

    // Full journal entry preview
    selectedEntry?.let { entry ->
        AlertDialog(
            onDismissRequest = {
                selectedEntry = null
            },
            title = {
                Text(entry.title)
            },
            text = {
                Column {
                    entry.mood?.let {
                        Text(
                            it,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    Text(
                        LocalDate.ofEpochDay(
                            entry.entryDateEpochDay
                        ).toString(),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(Modifier.height(16.dp))

                    Text(entry.content)
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedEntry = null
                    }
                ) {
                    Text("Close")
                }
            }
        )
    }
}

// --------------------------------------------------
// JOURNAL CALENDAR
// --------------------------------------------------

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun JournalCalendar(
    currentMonth: YearMonth,
    selectedDate: LocalDate?,
    entryDates: Set<Long>,
    onDateSelected: (LocalDate) -> Unit,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    val firstDay = currentMonth.atDay(1)

    // Sunday-first calendar
    val leadingDays = firstDay.dayOfWeek.value % 7

    val totalCells =
        ((leadingDays + currentMonth.lengthOfMonth() + 6) / 7) * 7

    val calendarDates = (0 until totalCells).map { index ->
        val day = index - leadingDays + 1

        if (day in 1..currentMonth.lengthOfMonth()) {
            currentMonth.atDay(day)
        } else {
            null
        }
    }

    val weekdays = listOf("S", "M", "T", "W", "T", "F", "S")

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            // Month navigation
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onPreviousMonth) {
                    Text(
                        "‹",
                        style = MaterialTheme.typography.headlineMedium
                    )
                }

                Text(
                    text = currentMonth.month.getDisplayName(
                        TextStyle.FULL,
                        Locale.getDefault()
                    ) + " ${currentMonth.year}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                IconButton(onClick = onNextMonth) {
                    Text(
                        "›",
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // Weekday labels
            Row(modifier = Modifier.fillMaxWidth()) {
                weekdays.forEach { day ->
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = day,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // Calendar dates
            calendarDates.chunked(7).forEach { week ->

                Row(modifier = Modifier.fillMaxWidth()) {

                    week.forEach { date ->

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .padding(2.dp),
                            contentAlignment = Alignment.Center
                        ) {

                            if (date != null) {

                                val isSelected = date == selectedDate
                                val hasEntry =
                                    date.toEpochDay() in entryDates

                                val isToday = date == LocalDate.now()

                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            color = if (isSelected) {
                                                MaterialTheme.colorScheme.primary
                                            } else {
                                                MaterialTheme.colorScheme.surfaceContainer
                                            },
                                            shape = CircleShape
                                        )
                                        .clickable {
                                            onDateSelected(date)
                                        },
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {

                                    Text(
                                        text = date.dayOfMonth.toString(),
                                        color = if (isSelected) {
                                            MaterialTheme.colorScheme.onPrimary
                                        } else {
                                            MaterialTheme.colorScheme.onSurface
                                        },
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = if (isToday) {
                                            FontWeight.Bold
                                        } else {
                                            FontWeight.Normal
                                        }
                                    )

                                    if (hasEntry) {
                                        Box(
                                            modifier = Modifier
                                                .size(4.dp)
                                                .background(
                                                    color = if (isSelected) {
                                                        MaterialTheme.colorScheme.onPrimary
                                                    } else {
                                                        MaterialTheme.colorScheme.primary
                                                    },
                                                    shape = CircleShape
                                                )
                                        )
                                    } else {
                                        Spacer(Modifier.height(4.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(
                                MaterialTheme.colorScheme.primary,
                                CircleShape
                            )
                    )

                    Text(
                        "Journal entries",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                TextButton(
                    onClick = {
                        onDateSelected(LocalDate.now())
                    }
                ) {
                    Text("Today")
                }
            }
        }
    }
}

// --------------------------------------------------
// JOURNAL ENTRY CARD
// --------------------------------------------------

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun JournalEntryCard(
    entry: JournalEntity,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = entry.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        text = LocalDate.ofEpochDay(
                            entry.entryDateEpochDay
                        ).toString(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                entry.mood?.let { Text(it) }

                IconButton(onClick = onEdit) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit entry"
                    )
                }

                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete entry"
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = entry.content,
                maxLines = 3,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// --------------------------------------------------
// JOURNAL EDITOR DIALOG
// --------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun JournalEditorDialog(
    entry: JournalEntity?,
    onDismiss: () -> Unit,
    onSave: (String, String, String) -> Unit
) {
    var title by remember(entry) {
        mutableStateOf(entry?.title ?: "")
    }

    var content by remember(entry) {
        mutableStateOf(entry?.content ?: "")
    }

    var mood by remember(entry) {
        mutableStateOf(entry?.mood ?: "😊 Happy")
    }

    var expanded by remember { mutableStateOf(false) }

    val moods = listOf(
        "😊 Happy",
        "😌 Calm",
        "🔥 Motivated",
        "😔 Sad",
        "😡 Angry",
        "😰 Anxious",
        "❤️ Grateful"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                if (entry == null) {
                    "New Journal Entry"
                } else {
                    "Edit Entry"
                }
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Write your thoughts...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 120.dp),
                    minLines = 4
                )

                Text(
                    "How are you feeling?",
                    style = MaterialTheme.typography.labelLarge
                )

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = {
                        expanded = !expanded
                    }
                ) {

                    OutlinedTextField(
                        value = mood,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Mood") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = expanded
                            )
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = {
                            expanded = false
                        }
                    ) {
                        moods.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    mood = option
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = title.isNotBlank() && content.isNotBlank(),
                onClick = {
                    onSave(
                        title.trim(),
                        content.trim(),
                        mood
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
