package com.example.cuteinventory.ui.inventory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.cuteinventory.data.remote.SupabaseClientProvider
import com.example.cuteinventory.data.repository.InventoryRepositoryImpl

// Factory to build InventoryViewModel with required dependencies
class InventoryViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InventoryViewModel::class.java)) {
            val repository = InventoryRepositoryImpl(SupabaseClientProvider.client)
            return InventoryViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}