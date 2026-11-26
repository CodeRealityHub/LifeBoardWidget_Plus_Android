package com.example.lifeboardapp.data.presentation.journal

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.lifeboardapp.LifeBoardApp
import com.example.lifeboardapp.data.local.entity.JournalEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlinx.coroutines.flow.firstOrNull

class JournalViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository =
        (application as LifeBoardApp).container.journalRepository

    private val _uiState = MutableStateFlow(
        JournalUiState(isLoading = true)
    )

    val uiState = _uiState.asStateFlow()

    init {
        loadEntries()
    }

    private fun loadEntries() {
        viewModelScope.launch {
            repository.getAllEntries().collect { entries ->
                _uiState.value = JournalUiState(
                    entries = entries,
                    isLoading = false
                )
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addEntry(
        title: String,
        content: String,
        mood: String
    ) {
        viewModelScope.launch {
            repository.insertEntry(
                JournalEntity(
                    title = title,
                    content = content,
                    mood = mood,
                    entryDateEpochDay = LocalDate.now().toEpochDay()
                )
            )
        }
    }


    fun updateEntry(
        id: Int,
        title: String,
        content: String,
        mood: String
    ) {
        viewModelScope.launch {

            val existingEntry: JournalEntity =
                repository.getEntryById(id).firstOrNull()
                    ?: return@launch

            val updatedEntry: JournalEntity = existingEntry.copy(
                title = title,
                content = content,
                mood = mood,
                updatedAt = System.currentTimeMillis()
            )

            repository.updateEntry(updatedEntry)
        }
    }

    fun deleteEntry(id: Int) {
        viewModelScope.launch {
            repository.deleteEntryById(id)
        }
    }
}
