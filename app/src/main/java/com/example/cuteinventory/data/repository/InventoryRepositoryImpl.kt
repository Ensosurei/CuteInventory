package com.example.cuteinventory.data.repository

import com.example.cuteinventory.data.remote.dto.ProductDto
import com.example.cuteinventory.data.remote.dto.SaleDto
import com.example.cuteinventory.data.remote.dto.toDomain
import com.example.cuteinventory.data.remote.dto.toDto
import com.example.cuteinventory.domain.model.Product
import com.example.cuteinventory.domain.model.Sale
import com.example.cuteinventory.domain.repository.InventoryRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest

// Repository implementation bridging Supabase DTOs and domain models
class InventoryRepositoryImpl(
    private val supabase: SupabaseClient
) : InventoryRepository {

    override suspend fun getProducts(): List<Product> {
        return supabase.postgrest["products"]
            .select()
            .decodeList<ProductDto>()
            .map { it.toDomain() }
    }

    override suspend fun insertProduct(product: Product) {
        supabase.postgrest["products"]
            .insert(product.toDto())
    }

    override suspend fun updateProduct(product: Product) {
        val productId = product.id.ifBlank { return }
        supabase.postgrest["products"]
            .update(product.toDto()) {
                filter {
                    eq("id", productId)
                }
            }
    }

    override suspend fun deleteProduct(productId: String) {
        supabase.postgrest["products"]
            .delete {
                filter {
                    eq("id", productId)
                }
            }
    }

    override suspend fun registerSale(sale: Sale) {
        supabase.postgrest["sales"]
            .insert(sale.toDto())
    }

    override suspend fun getSales(): List<Sale> {
        return supabase.postgrest["sales"]
            .select()
            .decodeList<SaleDto>()
            .map { it.toDomain() }
    }

    override suspend fun getSalesByMonth(year: Int, month: Int): List<Sale> {
        // Format ISO date bounds for month range query
        val startDate = "%04d-%02d-01T00:00:00Z".format(year, month)
        val endDate = if (month == 12) {
            "%04d-01-01T00:00:00Z".format(year + 1)
        } else {
            "%04d-%02d-01T00:00:00Z".format(year, month + 1)
        }

        return supabase.postgrest["sales"]
            .select {
                filter {
                    gte("sold_at", startDate)
                    lt("sold_at", endDate)
                }
            }
            .decodeList<SaleDto>()
            .map { it.toDomain() }
    }
}