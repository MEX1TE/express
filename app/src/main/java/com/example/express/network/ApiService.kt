package com.example.express.network

import com.example.express.model.Credentials
import com.example.express.model.Order
import com.example.express.model.Product
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @GET("products")
    suspend fun getProducts(): List<Product>

    @POST("order")
    suspend fun placeOrder(@Body order: Order): Order

    @POST("register")
    suspend fun register(@Body credentials: com.example.express.model.Credentials): Map<String, String>

    @POST("login")
    suspend fun login(@Body credentials: Credentials): Map<String, String>
}

data class Credentials(
    val username: String,
    val password: String
)