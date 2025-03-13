package com.example.express

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.express.databinding.ActivityMainBinding
import com.example.express.network.ApiClient
import com.example.express.viewmodel.CartViewModel
import com.example.express.viewmodel.CartViewModelFactory

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: CartViewModel by viewModels { CartViewModelFactory(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        val sharedPreferences = getSharedPreferences("auth", MODE_PRIVATE)
        val token = sharedPreferences.getString("access_token", null)
        if (token == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        setContentView(binding.root)
        setupRecyclerView()
        setupObservers()
        setupOrderButton()
    }

    private fun setupRecyclerView() {
        binding.productsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.cartRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun setupObservers() {
        viewModel.products.observe(this) { products ->
            binding.productsRecyclerView.adapter = ProductAdapter(products) { product ->
                viewModel.addToCart(product)
            }
        }

        viewModel.cartItems.observe(this) { items ->
            binding.cartRecyclerView.adapter = CartAdapter(items)
        }

        viewModel.orderStatus.observe(this) { status ->
            binding.statusTextView.text = status
        }
    }

    private fun setupOrderButton() {
        binding.placeOrderButton.setOnClickListener {
            val address = binding.addressEditText.text.toString()
            if (address.isNotEmpty()) {
                viewModel.placeOrder(address)
            }
        }
    }
}