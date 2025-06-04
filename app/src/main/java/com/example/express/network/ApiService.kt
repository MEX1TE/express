package com.example.express.network

import com.example.express.model.Credentials
import com.example.express.model.Order
import com.example.express.model.Product
import com.example.express.model.OrderHistoryEntry
import com.example.express.model.OrderCreateRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @GET("products")
    suspend fun getProducts(): List<Product>

    @POST("orders")
    suspend fun placeOrder(@Body order: OrderCreateRequest): OrderHistoryEntry

    @POST("register")
    suspend fun register(@Body credentials: com.example.express.model.Credentials): Map<String, String>

    @POST("token")
    suspend fun login(@Body credentials: Credentials): Map<String, String>

    @GET("orders")
    suspend fun getOrderHistory(): List<OrderHistoryEntry>
}

data class Credentials(
    val username: String,
    val password: String
)