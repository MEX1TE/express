package com.example.express.model

// Этот класс будет использоваться для отправки запроса на создание заказа
data class OrderCreateRequest(
    val address: String,
    val total: Double,
    val items: List<CartItemRequest>
) 