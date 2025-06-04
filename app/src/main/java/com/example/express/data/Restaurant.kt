package com.example.express.data

import com.google.gson.annotations.SerializedName

data class Restaurant(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("description")
    val description: String?,
    @SerializedName("logoUrl") // Алиас, как приходит от сервера
    val logoUrl: String?,
    @SerializedName("address")
    val address: String?
) 