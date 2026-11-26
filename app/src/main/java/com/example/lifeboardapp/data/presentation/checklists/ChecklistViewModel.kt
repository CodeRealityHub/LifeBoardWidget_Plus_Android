package com.example.lifeboardapp.data.presentation.checklists

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.lifeboardapp.LifeBoardApp
import com.example.lifeboardapp.data.local.entity.ChecklistEntity
import com.example.lifeboardapp.data.local.entity.ChecklistItemEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ChecklistViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val checklistRepository =
        (application as LifeBoardApp).container.checklistRepository

    private val selectedChecklistId = MutableStateFlow<Int?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<ChecklistUiState> =
        combine(
            checklistRepository.getAllChecklists(),
            selectedChecklistId
        ) { checklists, selectedId ->
            checklists to selectedId
        }.flatMapLatest { (checklists, selectedId) ->

            val selectedChecklist =
                checklists.firstOrNull { it.id == selectedId }
                    ?: checklists.firstOrNull()

            if (selectedChecklist == null) {
                flowOf(
                    ChecklistUiState(
                        checklists = checklists,
                        isLoading = false
                    )
                )
            } else {
                combine(
                    checklistRepository.getChecklistItems(
                        selectedChecklist.id
                    ),
                    checklistRepository.getCompletedItemCount(
                        selectedChecklist.id
                    ),
                    checklistRepository.getTotalItemCount(
                        selectedChecklist.id
                    )
                ) { items, completed, total ->

                    ChecklistUiState(
                        checklists = checklists,
                        selectedChecklist = selectedChecklist,
                        items = items,
                        completedItems = completed,
                        totalItems = total,
                        isLoading = false
                    )
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ChecklistUiState()
        )

    // SELECT CHECKLIST
    fun selectChecklist(id: Int) {
        selectedChecklistId.value = id
    }

    // CREATE CHECKLIST
    fun addChecklist(
        title: String,
        description: String
    ) {
        viewModelScope.launch {
            val id = checklistRepository.createChecklist(
                ChecklistEntity(
                    title = title,
                    description = description
                )
            )

            selectedChecklistId.value = id.toInt()
        }
    }

    // UPDATE CHECKLIST
    fun updateChecklist(
        id: Int,
        title: String,
        description: String
    ) {
        viewModelScope.launch {
            val existing = uiState.value.checklists
                .firstOrNull { it.id == id }
                ?: return@launch

            checklistRepository.updateChecklist(
                existing.copy(
                    title = title,
                    description = description
                )
            )
        }
    }

    // DELETE CHECKLIST
    fun deleteChecklist(id: Int) {
        viewModelScope.launch {
            checklistRepository.deleteChecklist(id)

            if (selectedChecklistId.value == id) {
                selectedChecklistId.value = null
            }
        }
    }

    // CREATE ITEM
    fun addItem(title: String) {
        viewModelScope.launch {
            val checklistId =
                uiState.value.selectedChecklist?.id ?: return@launch

            checklistRepository.createItem(
                ChecklistItemEntity(
                    checklistId = checklistId,
                    title = title,
                    position = uiState.value.items.size
                )
            )
        }
    }

    // UPDATE ITEM
    fun updateItem(
        itemId: Int,
        title: String
    ) {
        viewModelScope.launch {
            val existing = uiState.value.items
                .firstOrNull { it.id == itemId }
                ?: return@launch

            checklistRepository.updateItem(
                existing.copy(title = title)
            )
        }
    }

    // TOGGLE ITEM
    fun toggleItem(
        itemId: Int,
        completed: Boolean
    ) {
        viewModelScope.launch {
            checklistRepository.toggleItem(
                itemId = itemId,
                completed = completed
            )
        }
    }

    // DELETE ITEM
    fun deleteItem(itemId: Int) {
        viewModelScope.launch {
            checklistRepository.deleteItem(itemId)
        }
    }
}
