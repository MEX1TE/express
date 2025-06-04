package com.example.express

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.express.databinding.ActivityLoginBinding
// import com.example.express.model.Credentials // Старый импорт
import com.example.express.network.Credentials // Новый импорт для логина
import com.example.express.network.ApiClient
import com.example.express.ui.RestaurantListActivity // Импорт для RestaurantListActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginButton.setOnClickListener {
            val username = binding.usernameEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()

            if (username.isBlank() || password.isBlank()) {
                binding.statusTextView.text = "Заполните все поля"
                return@setOnClickListener
            }

            // Используем com.example.express.network.Credentials для логина
            val credentials = Credentials(
                username = username,
                password = password
                // phone, email, name здесь не нужны
            )
            loginUser(credentials)
        }

        binding.registerButton.setOnClickListener {
            // Для регистрации, возможно, понадобится com.example.express.model.Credentials
            // Убедитесь, что RegisterActivity использует правильную модель
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun loginUser(credentials: Credentials) { // credentials теперь com.example.express.network.Credentials
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Используем ApiClient.instance напрямую
                val response = ApiClient.instance.login(credentials)
                withContext(Dispatchers.Main) {
                    binding.statusTextView.text = "Вход успешен"
                    Log.d("LoginActivity", "Login successful: ${response["access_token"]}")
                    // Переход на RestaurantListActivity после успешного логина
                    val intent = Intent(this@LoginActivity, RestaurantListActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    binding.statusTextView.text = "Ошибка входа: ${e.message}"
                    Log.e("LoginActivity", "Login error: ${e.message}", e)
                }
            }
        }
    }
}