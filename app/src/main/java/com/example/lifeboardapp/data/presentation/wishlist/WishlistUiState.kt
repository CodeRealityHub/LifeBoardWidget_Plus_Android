package com.example.lifeboardapp.data.presentation.wishlist

import com.example.lifeboardapp.data.local.entity.CollectionItemEntity

data class WishlistUiState(
    val items: List<CollectionItemEntity> = emptyList(),
    val isLoading: Boolean = false
)
