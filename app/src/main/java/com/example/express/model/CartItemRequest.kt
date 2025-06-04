package com.example.express.model

// Этот класс будет использоваться для отправки данных о товаре в заказе на сервер
data class CartItemRequest(
    val productId: Int,
    val quantity: Int
) 