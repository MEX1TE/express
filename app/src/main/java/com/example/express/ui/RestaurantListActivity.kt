package com.example.express.ui // Уточните ваш пакет

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.express.MainActivity // Импортируем MainActivity для доступа к константам
import com.example.express.adapters.RestaurantAdapter
import com.example.express.databinding.ActivityRestaurantListBinding
import com.example.express.viewmodels.RestaurantViewModel

class RestaurantListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRestaurantListBinding
    private val restaurantViewModel: RestaurantViewModel by viewModels()
    private lateinit var restaurantAdapter: RestaurantAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRestaurantListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        observeViewModel()

        // Загружаем рестораны при создании Activity
        if (savedInstanceState == null) { // Чтобы не загружать при пересоздании, если данные уже есть
            restaurantViewModel.fetchRestaurants()
        }
    }

    private fun setupRecyclerView() {
        restaurantAdapter = RestaurantAdapter { restaurant ->
            // Обработка нажатия на ресторан:
            // Переходим в MainActivity (или вашу Activity для отображения продуктов),
            // передавая ID выбранного ресторана.
            val intent = Intent(this, MainActivity::class.java)
            // Используем константы из MainActivity
            intent.putExtra(MainActivity.RESTAURANT_ID_EXTRA, restaurant.id)
            intent.putExtra(MainActivity.RESTAURANT_NAME_EXTRA, restaurant.name)
            startActivity(intent)
        }
        binding.recyclerViewRestaurants.apply {
            adapter = restaurantAdapter
            layoutManager = LinearLayoutManager(this@RestaurantListActivity)
        }
    }

    private fun observeViewModel() {
        restaurantViewModel.restaurants.observe(this) { restaurants ->
            restaurants?.let {
                restaurantAdapter.submitList(it)
            }
        }

        restaurantViewModel.isLoading.observe(this) { isLoading ->
            binding.progressBarRestaurants.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        restaurantViewModel.error.observe(this) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                restaurantViewModel.onErrorShown() // Сбрасываем ошибку после показа
            }
        }
    }
} 