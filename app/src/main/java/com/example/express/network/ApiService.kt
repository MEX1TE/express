package com.example.express.network

import com.example.express.model.Product
import com.example.express.model.OrderHistoryEntry
import com.example.express.model.OrderCreateRequest
import com.example.express.data.Restaurant
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @GET("products")
    suspend fun getProducts(): List<Product>

    @POST("orders")
    suspend fun placeOrder(@Body order: OrderCreateRequest): OrderHistoryEntry

    @POST("register")
    suspend fun register(@Body credentials: com.example.express.model.Credentials): Map<String, String>

    @POST("token")
    suspend fun login(@Body credentials: com.example.express.network.Credentials): Map<String, String>

    @GET("orders")
    suspend fun getOrderHistory(): List<OrderHistoryEntry>

    @GET("restaurants")
    suspend fun getRestaurants(): List<Restaurant>

    @GET("restaurants/{restaurant_id}/products")
    suspend fun getProductsByRestaurant(@Path("restaurant_id") restaurantId: Int): List<Product>
}

data class Credentials(
    val username: String,
    val password: String
)