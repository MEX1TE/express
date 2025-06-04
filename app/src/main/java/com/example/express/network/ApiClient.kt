package com.example.express.network

import android.content.Context // Раскомментировано для старого метода
// import android.content.Context // Контекст может быть не нужен для базовой конфигурации
import okhttp3.OkHttpClient
// import okhttp3.logging.HttpLoggingInterceptor // Зависимость для логгера нужно будет добавить в build.gradle
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    // Убедитесь, что этот URL корректен. Если пути в ApiService НЕ включают "api/", то этот URL должен.
    // Если пути в ApiService УЖЕ включают "api/" (напр. "api/restaurants"), то здесь "api/" быть не должно.
    // Судя по вашему серверному коду, эндпоинты FastAPI типа "/api/restaurants".
    // А в ApiService.kt у вас сейчас, например, @GET("restaurants").
    // Это значит, что BASE_URL ДОЛЖЕН содержать "/api/"
    private const val BASE_URL = "http://10.0.2.2:5002/api/"

    val instance: ApiService by lazy {
        // Для добавления логгирования раскомментируйте и добавьте зависимость:
        // implementation("com.squareup.okhttp3:logging-interceptor:4.x.x")
        // val loggingInterceptor = HttpLoggingInterceptor().apply {
        //     level = HttpLoggingInterceptor.Level.BODY
        // }

        val okHttpClient = OkHttpClient.Builder()
            // .addInterceptor(loggingInterceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(ApiService::class.java)
    }

    // Метод getApiService(context: Context) полностью удален
}