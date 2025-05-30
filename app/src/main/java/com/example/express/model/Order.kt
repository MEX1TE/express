package com.example.express.model

import com.google.gson.annotations.SerializedName

data class Address(
    val city: String,
    val street: String,
    val house: String,
    val apartment: String? = null
) {
    fun toFormattedString(): String {
        val parts = listOfNotNull(
            city,
            "ул. $street",
            "д. $house",
            apartment?.let { "кв. $it" }
        )
        return parts.joinToString(", ")
    }
}

data class Order(
    val items: List<CartItem>,
    val address: String, // Изменено на String
    @SerializedName("total") val total: Double
)