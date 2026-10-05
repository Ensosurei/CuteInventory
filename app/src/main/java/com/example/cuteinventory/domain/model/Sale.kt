package com.example.cuteinventory.domain.model

// Domain model for sale
data class Sale(
    val id: String = "",
    val productId: String,
    val productName: String,
    val quantity: Int,
    val unitPrice: Double,
    val totalPrice: Double,
    val soldAt: String = ""
)