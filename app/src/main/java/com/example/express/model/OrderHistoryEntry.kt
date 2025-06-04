package com.example.express.model

import com.google.gson.annotations.SerializedName
// import java.util.Date // Раскомментируйте, если хотите парсить строку в Date

data class OrderHistoryEntry(
    @SerializedName("id") val id: Int,
    @SerializedName("userId") val userId: Int?,
    @SerializedName("address") val address: String,
    @SerializedName("total") val total: Double,
    @SerializedName("createdAt") val createdAt: String, // Сервер отдает datetime, GSON по умолчанию преобразует в String
    @SerializedName("items") val items: List<OrderItemHistory>
) 