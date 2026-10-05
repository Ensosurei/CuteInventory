package com.example.cuteinventory.data.model

data class Sale(
    val id: String = "",
    val productId: String,
    val productName: String,
    val quantity: Int = 1,
    val unitPrice: Double,
    val totalPrice: Double = quantity * unitPrice,
    val soldAt: String = ""
)