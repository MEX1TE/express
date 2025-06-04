package com.example.express.model

import com.google.gson.annotations.SerializedName

data class Product(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("price") val price: Double,
    @SerializedName("description") val description: String?,
    @SerializedName("imageUrl") val imageUrl: String?
)