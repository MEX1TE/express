package com.example.express.network

import android.content.Context
import com.example.express.network.ApiService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.Interceptor

object ApiClient {
    private const val BASE_URL = "http://10.0.2.2:5002/"

    fun getApiService(context: Context): ApiService {
        val sharedPreferences = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("access_token", null)

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val original = chain.request()
                val requestBuilder = original.newBuilder()
                if (token != null) {
                    requestBuilder.header("Authorization", "Bearer $token")
                }
                val request = requestBuilder.build()
                chain.proceed(request)
            }
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}