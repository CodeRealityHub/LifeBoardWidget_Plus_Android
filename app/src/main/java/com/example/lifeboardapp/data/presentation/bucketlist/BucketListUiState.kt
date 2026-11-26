package com.example.lifeboardapp.data.presentation.bucketlist

import com.example.lifeboardapp.data.local.entity.CollectionItemEntity

data class BucketListUiState(
    val items: List<CollectionItemEntity> = emptyList(),
    val isLoading: Boolean = true
)
