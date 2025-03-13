package com.example.express.model

data class Product(
    val id: Int,
    val name: String,
    val price: Double,
    val description: String
)

data class CartItem(
    val product: Product,
    var quantity: Int
)

data class Order(
    val items: List<CartItem>,
    val address: String,
    val total: Double
)