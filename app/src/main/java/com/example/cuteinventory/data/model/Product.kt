package com.example.cuteinventory.data.model

// Domain model for product
data class Product(
    val id: String = "",
    val name: String,
    val price: Double,
    val stock: Int = 0,
    val createdAt: String = ""
)