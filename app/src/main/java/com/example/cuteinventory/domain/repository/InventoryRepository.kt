package com.example.cuteinventory.domain.repository

import com.example.cuteinventory.domain.model.Product
import com.example.cuteinventory.domain.model.Sale

// Repository contract defining inventory and sales operations
interface InventoryRepository {

    // Products CRUD
    suspend fun getProducts(): List<Product>
    suspend fun insertProduct(product: Product)
    suspend fun updateProduct(product: Product)
    suspend fun deleteProduct(productId: String)

    // Sales operations
    suspend fun registerSale(sale: Sale)
    suspend fun getSales(): List<Sale>
    suspend fun getSalesByMonth(year: Int, month: Int): List<Sale>
}