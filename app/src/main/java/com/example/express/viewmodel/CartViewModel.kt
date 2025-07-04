package com.example.express.viewmodel

// import android.content.Context // Больше не используется здесь
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.express.model.CartItem
// import com.example.express.model.Order // Не используется, можно удалить
import com.example.express.model.Product
import com.example.express.model.OrderCreateRequest
import com.example.express.model.CartItemRequest
import com.example.express.network.ApiClient
import com.google.gson.GsonBuilder
import kotlinx.coroutines.launch

// context больше не нужен для CartViewModelFactory
class CartViewModelFactory(/*private val context: Context*/) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CartViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            // Создаем CartViewModel без context
            return CartViewModel(/*context*/) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

// context больше не нужен для CartViewModel
class CartViewModel(/*private val context: Context*/) : ViewModel() {
    private val _cartItems = MutableLiveData<List<CartItem>>(emptyList())
    val cartItems: LiveData<List<CartItem>> = _cartItems

    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> = _products

    private val _orderStatus = MutableLiveData<String>()
    val orderStatus: LiveData<String> = _orderStatus

    init {
        loadProducts() // Загружает все продукты по умолчанию
    }

    private fun loadProducts() {
        viewModelScope.launch {
            Log.d("CartViewModel", "Calling API to get ALL products")
            try {
                val productListFromApi = ApiClient.instance.getProducts()
                Log.d("CartViewModel", "All products received: $productListFromApi")
                _products.postValue(productListFromApi)
            } catch (e: Exception) {
                _orderStatus.postValue("Ошибка загрузки всех продуктов: ${e.message}")
                Log.e("CartViewModel", "Error loading all products: ${e.message}", e)
            }
        }
    }

    fun loadProductsForRestaurant(restaurantId: Int) {
        viewModelScope.launch {
            Log.d("CartViewModel", "Calling API to get products for restaurant ID: $restaurantId")
            try {
                val productListFromApi = ApiClient.instance.getProductsByRestaurant(restaurantId)
                Log.d("CartViewModel", "Products for restaurant $restaurantId received: $productListFromApi")
                _products.postValue(productListFromApi)
            } catch (e: Exception) {
                _orderStatus.postValue("Ошибка загрузки продуктов для ресторана: ${e.message}")
                Log.e("CartViewModel", "Error loading products for restaurant $restaurantId: ${e.message}", e)
            }
        }
    }

    fun addToCart(product: Product) {
        val currentItems = _cartItems.value?.toMutableList() ?: mutableListOf()
        val existingItem = currentItems.find { it.product.id == product.id }

        if (existingItem != null) {
            existingItem.quantity++
        } else {
            currentItems.add(CartItem(product, 1))
        }
        _cartItems.value = currentItems
        Log.d("CartViewModel", "Cart items updated: $currentItems")
    }

    fun placeOrder(address: String) {
        viewModelScope.launch {
            try {
                val currentCartItems = _cartItems.value ?: emptyList()
                if (currentCartItems.isEmpty()) {
                    _orderStatus.postValue("Корзина пуста")
                    Log.d("CartViewModel", "Attempted to place order with empty cart")
                    return@launch
                }

                val orderItemsRequest = currentCartItems.map {
                    CartItemRequest(productId = it.product.id, quantity = it.quantity)
                }
                val total = currentCartItems.sumOf { it.product.price * it.quantity }
                val orderRequest = OrderCreateRequest(
                    address = address,
                    total = total,
                    items = orderItemsRequest
                )

                val gson = GsonBuilder().setPrettyPrinting().create()
                val orderJson = gson.toJson(orderRequest)
                Log.d("CartViewModel", "Sending order JSON: \n$orderJson")

                // Используем ApiClient.instance
                ApiClient.instance.placeOrder(orderRequest)
                _orderStatus.postValue("Заказ успешно размещен!")
                _cartItems.postValue(emptyList())
                Log.d("CartViewModel", "Order placed, cart cleared")
            } catch (e: Exception) {
                _orderStatus.postValue("Ошибка при оформлении заказа: ${e.message}")
                Log.e("CartViewModel", "Error placing order: ${e.message}", e)
            }
        }
    }
}