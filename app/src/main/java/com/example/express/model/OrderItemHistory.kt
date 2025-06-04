package com.example.express.model

import com.google.gson.annotations.SerializedName

data class OrderItemHistory(
    @SerializedName("product") val product: Product,
    @SerializedName("quantity") val quantity: Int
) 