package com.example.express

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.express.model.Address
import com.example.express.model.CartItem
import com.example.express.model.Product
import com.example.express.model.OrderHistoryEntry
import com.example.express.model.OrderCreateRequest
import com.example.express.model.CartItemRequest
import com.example.express.network.ApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.util.Log // Для логирования

class MainViewModel : ViewModel() {
    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> = _products

    private val _cart = MutableLiveData<List<CartItem>>()
    val cart: LiveData<List<CartItem>> = _cart

    private val _status = MutableLiveData<String>()
    val status: LiveData<String> = _status

    private val _orderHistory = MutableLiveData<List<OrderHistoryEntry>>()
    val orderHistory: LiveData<List<OrderHistoryEntry>> = _orderHistory

    private val _orderHistoryError = MutableLiveData<String?>() // Сделаем nullable для сброса ошибки
    val orderHistoryError: LiveData<String?> = _orderHistoryError

    init {
        _cart.value = emptyList()
    }

    // Функция getUserAuthToken больше не нужна для этих операций
    /*
    private fun getUserAuthToken(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("AUTH_TOKEN", null)
        return if (token != null) "Bearer $token" else null
    }
    */

    fun fetchOrderHistory(/*context: Context*/) {
        CoroutineScope(Dispatchers.IO).launch {
            // val token = getUserAuthToken(context) // Токен больше не нужен
            // if (token == null) { // Проверка токена больше не нужна
            //     _orderHistoryError.postValue("Токен авторизации не найден.")
            //     return@launch
            // }

            try {
                // Вызываем getOrderHistory без токена, используя ApiClient.instance
                val history = ApiClient.instance.getOrderHistory()
                _orderHistory.postValue(history)
                _orderHistoryError.postValue(null) // Сбрасываем ошибку при успехе
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error fetching order history", e)
                _orderHistoryError.postValue("Ошибка загрузки истории заказов: ${e.message}")
            }
        }
    }

    fun loadProducts(/*context: Context*/) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Получаем список продуктов от ApiService, используя ApiClient.instance
                val productListResponse = ApiClient.instance.getProducts()

                // Логируем полученный список продуктов
                Log.d("MainViewModel", "Products received from API: $productListResponse")

                // Постим результат в LiveData
                _products.postValue(productListResponse)

            } catch (e: Exception) {
                Log.e("MainViewModel", "Error loading products", e)
                _status.postValue("Ошибка загрузки продуктов: ${e.message}")
            }
        }
    }

    fun addToCart(product: Product) {
        val currentCart = _cart.value?.toMutableList() ?: mutableListOf()
        val existingItem = currentCart.find { it.product.id == product.id }
        if (existingItem != null) {
            existingItem.quantity += 1
        } else {
            currentCart.add(CartItem(product, 1))
        }
        _cart.value = currentCart
    }

    fun placeOrder(/*context: Context,*/ address: Address) {
        // val token = getUserAuthToken(context) // Токен больше не нужен
        // if (token == null) { // Проверка токена больше не нужна
        //     _status.postValue("Ошибка авторизации: не удалось получить токен.")
        //     return
        // }

        val currentCartItems = _cart.value ?: return
        if (currentCartItems.isEmpty()) {
            _status.postValue("Корзина пуста")
            return
        }

        val orderItemsRequest = currentCartItems.map {
            CartItemRequest(productId = it.product.id, quantity = it.quantity)
        }
        val total = currentCartItems.sumOf { it.product.price * it.quantity }
        val orderRequest = OrderCreateRequest(
            address = address.toFormattedString(),
            total = total,
            items = orderItemsRequest
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Вызываем placeOrder без токена, используя ApiClient.instance
                ApiClient.instance.placeOrder(orderRequest)
                _cart.postValue(emptyList())
                _status.postValue("Заказ успешно оформлен")
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error placing order", e)
                _status.postValue("Ошибка при оформлении заказа: ${e.message}")
            }
        }
    }
}