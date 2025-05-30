package com.example.express

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.express.model.Address
import com.example.express.model.CartItem
import com.example.express.model.Order
import com.example.express.model.Product
import com.example.express.network.ApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> = _products

    private val _cart = MutableLiveData<List<CartItem>>()
    val cart: LiveData<List<CartItem>> = _cart

    private val _status = MutableLiveData<String>()
    val status: LiveData<String> = _status

    init {
        _cart.value = emptyList()
    }

    fun loadProducts(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ApiClient.getApiService(context).getProducts()
                _products.postValue(response)
            } catch (e: Exception) {
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

    fun placeOrder(context: Context, address: Address) {
        val cartItems = _cart.value ?: return
        if (cartItems.isEmpty()) {
            _status.value = "Корзина пуста"
            return
        }
        val total = cartItems.sumOf { it.product.price * it.quantity }
        val order = Order(cartItems, address.toString(), total)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                ApiClient.getApiService(context).placeOrder(order)
                _cart.postValue(emptyList())
                _status.postValue("Заказ успешно оформлен")
            } catch (e: Exception) {
                _status.postValue("Ошибка при оформлении заказа: ${e.message}")
            }
        }
    }
}