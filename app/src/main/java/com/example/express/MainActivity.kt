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

    companion object {
        const val RESTAURANT_ID_EXTRA = "restaurant_id"
        // Можно добавить и другие константы для Intent, если понадобятся, например, имя ресторана для заголовка
        // const val RESTAURANT_NAME_EXTRA = "restaurant_name"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val app = application as ExpressApplication
        viewModel = ViewModelProvider(app, CartViewModelFactory())
            .get(CartViewModel::class.java)

        val restaurantId = intent.getIntExtra(RESTAURANT_ID_EXTRA, -1)

        if (restaurantId != -1) {
            viewModel.loadProductsForRestaurant(restaurantId)
            // Опционально: можно установить заголовок Activity, если передано имя ресторана
            // val restaurantName = intent.getStringExtra(RESTAURANT_NAME_EXTRA)
            // if (restaurantName != null) {
            //     title = restaurantName
            // }
        } else {
            // Если restaurantId не передан (равен -1), 
            // CartViewModel уже вызвал loadProducts() в своем init блоке для загрузки всех продуктов.
            // Ничего дополнительно делать не нужно.
            // Если в будущем поведение по умолчанию должно быть другим (например, не загружать ничего),
            // нужно будет изменить init блок в CartViewModel.
        }

        val productAdapter = ProductAdapter(emptyList()) { product ->
            viewModel.addToCart(product)
            android.util.Log.d("MainActivity", "Added product: ${product.name}")
        }
        binding.productsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.productsRecyclerView.adapter = productAdapter

        viewModel.products.observe(this) { products ->
            productAdapter.updateProducts(products)
            android.util.Log.d("MainActivity", "Products updated: $products")
        }

        binding.cartButton.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }

        binding.buttonOrderHistory.setOnClickListener {
            val intent = Intent(this, OrderHistoryActivity::class.java)
            startActivity(intent)
        }
    }
}