package com.example.express.model

data class Product(
    val id: Int,
    val name: String,
    val price: Double,
    val description: String? = null
)