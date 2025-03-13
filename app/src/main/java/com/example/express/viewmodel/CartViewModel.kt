package com.example.express.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.express.model.CartItem
import com.example.express.model.Order
import com.example.express.model.Product
import com.example.express.network.ApiClient
import kotlinx.coroutines.launch


class CartViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CartViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CartViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class CartViewModel(private val context: Context) : ViewModel() {
    private val _cartItems = MutableLiveData<List<CartItem>>(emptyList())
    val cartItems: LiveData<List<CartItem>> = _cartItems

    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> = _products

    private val _orderStatus = MutableLiveData<String>()
    val orderStatus: LiveData<String> = _orderStatus

    init {
        loadProducts()
    }


    class CartViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CartViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return CartViewModel(context) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }


    private fun loadProducts() {
        viewModelScope.launch {
            try {
                _products.value = ApiClient.getApiService(context).getProducts()
            } catch (e: Exception) {
                _orderStatus.value = "Ошибка загрузки продуктов: ${e.message}"
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
    }

    fun placeOrder(address: String) {
        viewModelScope.launch {
            try {
                val items = _cartItems.value ?: emptyList()
                val total = items.sumOf { it.product.price * it.quantity }
                val order = Order(items, address, total)
                ApiClient.getApiService(context).placeOrder(order)
                _orderStatus.value = "Заказ успешно размещен!"
                _cartItems.value = emptyList()
            } catch (e: Exception) {
                _orderStatus.value = "Ошибка при оформлении заказа: ${e.message}"
            }
        }
    }
}