package com.example.cuteinventory.data.remote.dto

import com.example.cuteinventory.domain.model.Product
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductDto(
    @SerialName("id")
    val id: String? = null,
    @SerialName("name")
    val name: String,
    @SerialName("price")
    val price: Double,
    @SerialName("stock")
    val stock: Int = 0,
    @SerialName("created_at")
    val createdAt: String? = null
)

// Extension functions to map between DTO and Domain Model
fun ProductDto.toDomain(): Product {
    return Product(
        id = id.orEmpty(),
        name = name,
        price = price,
        stock = stock,
        createdAt = createdAt.orEmpty()
    )
}

fun Product.toDto(): ProductDto {
    return ProductDto(
        id = id.ifBlank { null },
        name = name,
        price = price,
        stock = stock,
        createdAt = createdAt.ifBlank { null }
    )
}