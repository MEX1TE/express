package com.example.express.viewmodels // Уточните ваш пакет

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.express.network.ApiClient
import com.example.express.data.Restaurant
import kotlinx.coroutines.launch
import java.io.IOException // Для обработки сетевых ошибок

class RestaurantViewModel : ViewModel() {

    private val _restaurants = MutableLiveData<List<Restaurant>>()
    val restaurants: LiveData<List<Restaurant>> = _restaurants

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    // Получаем экземпляр ApiService
    private val apiService = ApiClient.instance

    fun fetchRestaurants() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null // Сбрасываем предыдущую ошибку
            try {
                val response = apiService.getRestaurants()
                _restaurants.value = response
            } catch (e: IOException) { // Обработка проблем с сетью (нет интернета и т.д.)
                _error.value = "Ошибка сети: ${e.message}"
                // Можно также выводить более специфичные сообщения в зависимости от типа исключения
            } catch (e: Exception) { // Обработка других ошибок (например, HTTP ошибки, если Retrofit их так выбрасывает)
                _error.value = "Не удалось загрузить рестораны: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Метод для сброса сообщения об ошибке, чтобы оно не показывалось снова при пересоздании Activity/Fragment
    fun onErrorShown() {
        _error.value = null
    }
} 