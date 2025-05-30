package com.example.express

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.express.databinding.ActivityCartBinding
import com.example.express.model.Address
import com.example.express.viewmodel.CartViewModel
import com.example.express.viewmodel.CartViewModelFactory

class CartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCartBinding
    private lateinit var viewModel: CartViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Получение ViewModel из Application
        val app = application as ExpressApplication
        viewModel = ViewModelProvider(app, CartViewModelFactory(applicationContext))
            .get(CartViewModel::class.java)

        // Настройка RecyclerView для корзины
        val cartAdapter = CartAdapter(emptyList())
        binding.cartRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.cartRecyclerView.adapter = cartAdapter

        // Наблюдение за корзиной
        viewModel.cartItems.observe(this) { cartItems ->
            Log.d("CartActivity", "Received cart items: $cartItems")
            cartAdapter.updateCart(cartItems ?: emptyList())
            binding.placeOrderButton.isEnabled = cartItems.isNotEmpty()
            if (cartItems.isEmpty()) {
                binding.statusTextView.text = "Корзина пуста"
            } else {
                binding.statusTextView.text = ""
            }
        }

        // Наблюдение за статусом
        viewModel.orderStatus.observe(this) { status ->
            binding.statusTextView.text = status
        }

        // Обработка заказа
        binding.placeOrderButton.setOnClickListener {
            val city = binding.cityEditText.text.toString().trim()
            val street = binding.streetEditText.text.toString().trim()
            val house = binding.houseEditText.text.toString().trim()
            val apartment = binding.apartmentEditText.text.toString().trim()

            if (city.isBlank() || street.isBlank() || house.isBlank()) {
                binding.statusTextView.text = "Заполните обязательные поля адреса"
            } else {
                val address = Address(city, street, house, if (apartment.isBlank()) null else apartment)
                val addressString = address.toFormattedString()
                Log.d("CartActivity", "Formatted address: $addressString")
                viewModel.placeOrder(addressString)
                finish()
            }
        }
    }
}