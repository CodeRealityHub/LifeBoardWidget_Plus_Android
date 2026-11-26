package com.example.lifeboardapp.data.presentation.bucketlist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.lifeboardapp.LifeBoardApp
import com.example.lifeboardapp.data.local.entity.CollectionEntity
import com.example.lifeboardapp.data.local.entity.CollectionItemEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class BucketListViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository =
        (application as LifeBoardApp).container.collectionRepository

    private var collectionId: Int? = null

    // UI State
    private val _uiState = MutableStateFlow(
        BucketListUiState(isLoading = true)
    )

    val uiState = _uiState

    init {
        loadBucketList()
    }

    // Load or create default bucket list

    private fun loadBucketList() {
        viewModelScope.launch {
            try {
                var collection = repository
                    .getCollectionByType("BUCKET_LIST")
                    .firstOrNull()
                    ?.firstOrNull()

                if (collection == null) {
                    val now = System.currentTimeMillis()

                    repository.createCollection(
                        CollectionEntity(
                            name = "Bucket List",
                            title = "Bucket List",
                            description = "Experiences and goals I want to achieve",
                            type = "BUCKET_LIST",
                            createdAt = now,
                            updatedAt = now
                        )
                    )

                    collection = repository
                        .getCollectionByType("BUCKET_LIST")
                        .firstOrNull()
                        ?.firstOrNull()
                }

                if (collection == null) {
                    _uiState.value = BucketListUiState(
                        isLoading = false
                    )
                    return@launch
                }

                collectionId = collection.id

                // Observe bucket list items

                repository.getCollectionItems(collection.id).collect {
                        items: List<CollectionItemEntity> ->

                    _uiState.value = BucketListUiState(
                        items = items,
                        isLoading = false
                    )
                }

            } catch (exception: Exception) {
                exception.printStackTrace()

                _uiState.value = BucketListUiState(
                    isLoading = false
                )
            }
        }
    }

    // Add bucket list item

    fun addItem(
        title: String,
        description: String,
        imageUri: String?,
        targetDate: Long?,
        targetPrice: Double?,
        externalUrl: String?
    ) {
        viewModelScope.launch {
            val id = collectionId ?: return@launch
            val now = System.currentTimeMillis()

            repository.createItem(
                CollectionItemEntity(
                    collectionId = id,
                    title = title,
                    description = description,
                    imageUri = imageUri,
                    targetDate = targetDate,
                    targetPrice = targetPrice,
                    externalUrl = externalUrl,
                    isCompleted = false,
                    createdAt = now,
                    updatedAt = now
                )
            )
        }
    }

    // Update bucket list item

    fun updateItem(
        id: Int,
        title: String,
        description: String,
        imageUri: String?,
        targetDate: Long?,
        targetPrice: Double?,
        externalUrl: String?
    ) {
        viewModelScope.launch {
            val item = repository
                .getCollectionItemById(id)
                .firstOrNull() ?: return@launch

            repository.updateItem(
                item.copy(
                    title = title,
                    description = description,
                    imageUri = imageUri,
                    targetDate = targetDate,
                    targetPrice = targetPrice,
                    externalUrl = externalUrl,
                    updatedAt = System.currentTimeMillis()
                )
            )
        }
    }

    // Delete bucket list item

    fun deleteItem(id: Int) {
        viewModelScope.launch {
            repository.deleteItem(id)
        }
    }

    // Toggle completed status

    fun toggleCompleted(
        id: Int,
        completed: Boolean
    ) {
        viewModelScope.launch {
            val item = repository
                .getCollectionItemById(id)
                .firstOrNull() ?: return@launch

            repository.updateItem(
                item.copy(
                    isCompleted = completed,
                    updatedAt = System.currentTimeMillis()
                )
            )
        }
    }
}
