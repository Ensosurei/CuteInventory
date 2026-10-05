package com.example.cuteinventory.ui.inventory

import com.example.cuteinventory.domain.model.Product

// UI state holder for Catalog screen
sealed interface InventoryUiState {
    object Loading : InventoryUiState
    data class Success(val products: List<Product>) : InventoryUiState
    data class Error(val message: String) : InventoryUiState
}