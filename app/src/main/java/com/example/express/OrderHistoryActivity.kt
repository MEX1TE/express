package com.example.express

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.express.model.OrderHistoryEntry // Убедитесь, что модель импортирована

class OrderHistoryActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    private lateinit var orderHistoryAdapter: OrderHistoryAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var textViewError: TextView
    private lateinit var textViewNoOrders: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_history)

        // Инициализация ViewModel
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        // Настройка RecyclerView
        recyclerView = findViewById(R.id.recyclerViewOrderHistory)
        progressBar = findViewById(R.id.progressBarOrderHistory)
        textViewError = findViewById(R.id.textViewOrderHistoryError)
        textViewNoOrders = findViewById(R.id.textViewNoOrders)

        setupRecyclerView()
        observeViewModel()

        // Загрузка истории заказов
        // Метод fetchOrderHistory() теперь вызывается без аргументов
        viewModel.fetchOrderHistory()
    }

    private fun setupRecyclerView() {
        orderHistoryAdapter = OrderHistoryAdapter(emptyList())
        recyclerView.apply {
            adapter = orderHistoryAdapter
            layoutManager = LinearLayoutManager(this@OrderHistoryActivity)
        }
    }

    private fun observeViewModel() {
        viewModel.orderHistory.observe(this) { orders ->
            progressBar.visibility = View.GONE
            if (orders.isNullOrEmpty()) {
                textViewNoOrders.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            } else {
                textViewNoOrders.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
                orderHistoryAdapter.updateOrders(orders)
            }
        }

        viewModel.orderHistoryError.observe(this) { error ->
            progressBar.visibility = View.GONE
            if (error != null) {
                textViewError.text = error
                textViewError.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
                textViewNoOrders.visibility = View.GONE
                // Опционально: показать Toast с ошибкой
                // Toast.makeText(this, error, Toast.LENGTH_LONG).show()
            }
        }

        // Начальное состояние ProgressBar (пока данные загружаются)
        // Можно также использовать отдельный LiveData для состояния загрузки
        progressBar.visibility = View.VISIBLE
        textViewError.visibility = View.GONE
        textViewNoOrders.visibility = View.GONE
    }

    // Не забудьте добавить эту Activity в ваш AndroidManifest.xml
    // <activity android:name=".OrderHistoryActivity" />
} 