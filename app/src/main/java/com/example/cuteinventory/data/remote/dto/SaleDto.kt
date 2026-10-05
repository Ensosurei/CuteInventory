package com.example.cuteinventory.data.remote.dto

import com.example.cuteinventory.domain.model.Sale
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SaleDto(
    @SerialName("id")
    val id: String? = null,
    @SerialName("product_id")
    val productId: String,
    @SerialName("product_name")
    val productName: String,
    @SerialName("quantity")
    val quantity: Int,
    @SerialName("unit_price")
    val unitPrice: Double,
    @SerialName("total_price")
    val totalPrice: Double,
    @SerialName("sold_at")
    val soldAt: String? = null
)

// Extension functions to map between DTO and Domain Model
fun SaleDto.toDomain(): Sale {
    return Sale(
        id = id.orEmpty(),
        productId = productId,
        productName = productName,
        quantity = quantity,
        unitPrice = unitPrice,
        totalPrice = totalPrice,
        soldAt = soldAt.orEmpty()
    )
}

fun Sale.toDto(): SaleDto {
    return SaleDto(
        id = id.ifBlank { null },
        productId = productId,
        productName = productName,
        quantity = quantity,
        unitPrice = unitPrice,
        totalPrice = totalPrice,
        soldAt = soldAt.ifBlank { null }
    )
}