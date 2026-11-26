
package com.example.lifeboardapp.data.presentation.wishlist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.lifeboardapp.LifeBoardApp
import com.example.lifeboardapp.data.local.entity.CollectionEntity
import com.example.lifeboardapp.data.local.entity.CollectionItemEntity

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class WishlistViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository =
        (application as LifeBoardApp).container.collectionRepository

    private var wishlistCollectionId: Int? = null

    private val _uiState = MutableStateFlow(
        WishlistUiState(isLoading = true)
    )

    val uiState: StateFlow<WishlistUiState> =
        _uiState.asStateFlow()

    init {
        loadWishlist()
    }

    // Load or create default wishlist

    private fun loadWishlist() {
        viewModelScope.launch {
            try {
                var collection = repository
                    .getCollectionByType("WISHLIST")
                    .firstOrNull()
                    ?.firstOrNull()

                if (collection == null) {
                    val now = System.currentTimeMillis()

                    repository.createCollection(
                        CollectionEntity(
                            name = "Wishlist",
                            title = "Wishlist",
                            description = "Things I want to buy",
                            type = "WISHLIST",
                            createdAt = now,
                            updatedAt = now
                        )
                    )

                    collection = repository
                        .getCollectionByType("WISHLIST")
                        .firstOrNull()
                        ?.firstOrNull()
                }

                if (collection == null) {
                    _uiState.value = WishlistUiState(
                        isLoading = false
                    )
                    return@launch
                }

                wishlistCollectionId = collection.id

                // Observe wishlist items

                repository.getCollectionItems(collection.id).collect { items ->
                    _uiState.value = WishlistUiState(
                        items = items,
                        isLoading = false
                    )
                }

            } catch (exception: Exception) {
                exception.printStackTrace()

                _uiState.value = WishlistUiState(
                    isLoading = false
                )
            }
        }
    }

    // Add wishlist item

    fun addItem(
        title: String,
        description: String,
        imageUri: String?,
        targetDate: Long?,
        targetPrice: Double?,
        externalUrl: String?
    ) {
        viewModelScope.launch {
            val collectionId = wishlistCollectionId ?: return@launch
            val now = System.currentTimeMillis()

            repository.createItem(
                CollectionItemEntity(
                    collectionId = collectionId,
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

    // Update wishlist item

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
            val existingItem = repository
                .getCollectionItemById(id)
                .firstOrNull() ?: return@launch

            repository.updateItem(
                existingItem.copy(
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

    // Delete wishlist item

    fun deleteItem(id: Int) {
        viewModelScope.launch {
            repository.deleteItem(id)
        }
    }

    // Mark item as purchased or unpurchased

    fun togglePurchased(
        id: Int,
        purchased: Boolean
    ) {
        viewModelScope.launch {
            val existingItem = repository
                .getCollectionItemById(id)
                .firstOrNull() ?: return@launch

            repository.updateItem(
                existingItem.copy(
                    isCompleted = purchased,
                    updatedAt = System.currentTimeMillis()
                )
            )
        }
    }
}
