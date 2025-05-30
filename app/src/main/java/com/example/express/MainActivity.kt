package com.example.express

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.express.databinding.ActivityMainBinding
import com.example.express.viewmodel.CartViewModel
import com.example.express.viewmodel.CartViewModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: CartViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Получение ViewModel из Application
        val app = application as ExpressApplication
        viewModel = ViewModelProvider(app, CartViewModelFactory(applicationContext))
            .get(CartViewModel::class.java)

        // Настройка RecyclerView для продуктов
        val productAdapter = ProductAdapter(emptyList()) { product ->
            viewModel.addToCart(product)
            android.util.Log.d("MainActivity", "Added product: ${product.name}")
        }
        binding.productsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.productsRecyclerView.adapter = productAdapter

        // Наблюдение за продуктами
        viewModel.products.observe(this) { products ->
            productAdapter.updateProducts(products)
            android.util.Log.d("MainActivity", "Products updated: $products")
        }

        // Переход в корзину
        binding.cartButton.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }
    }
}