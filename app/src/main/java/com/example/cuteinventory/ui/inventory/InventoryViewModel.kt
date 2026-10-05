package com.example.cuteinventory.ui.inventory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cuteinventory.domain.model.Product
import com.example.cuteinventory.domain.repository.InventoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// ViewModel managing product catalog CRUD and search filtering
class InventoryViewModel(
    private val repository: InventoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<InventoryUiState>(InventoryUiState.Loading)
    val uiState: StateFlow<InventoryUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private var allProducts: List<Product> = emptyList()

    init {
        loadProducts()
    }

    // Fetch product catalog from repository
    fun loadProducts() {
        viewModelScope.launch {
            _uiState.value = InventoryUiState.Loading
            try {
                allProducts = repository.getProducts()
                applyFilter()
            } catch (e: Exception) {
                _uiState.value = InventoryUiState.Error(e.message ?: "Error loading products")
            }
        }
    }

    // Filter products based on search query
    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        applyFilter()
    }

    private fun applyFilter() {
        val query = _searchQuery.value.trim().lowercase()
        val filteredList = if (query.isEmpty()) {
            allProducts
        } else {
            allProducts.filter { it.name.lowercase().contains(query) }
        }
        _uiState.value = InventoryUiState.Success(filteredList)
    }

    // Add new product
    fun addProduct(name: String, price: Double, stock: Int) {
        viewModelScope.launch {
            try {
                val newProduct = Product(name = name, price = price, stock = stock)
                repository.insertProduct(newProduct)
                loadProducts()
            } catch (e: Exception) {
                _uiState.value = InventoryUiState.Error(e.message ?: "Error adding product")
            }
        }
    }

    // Update existing product
    fun updateProduct(product: Product) {
        viewModelScope.launch {
            try {
                repository.updateProduct(product)
                loadProducts()
            } catch (e: Exception) {
                _uiState.value = InventoryUiState.Error(e.message ?: "Error updating product")
            }
        }
    }

    // Delete product
    fun deleteProduct(productId: String) {
        viewModelScope.launch {
            try {
                repository.deleteProduct(productId)
                loadProducts()
            } catch (e: Exception) {
                _uiState.value = InventoryUiState.Error(e.message ?: "Error deleting product")
            }
        }
    }
}