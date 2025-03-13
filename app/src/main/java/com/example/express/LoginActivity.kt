package com.example.express

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.express.databinding.ActivityLoginBinding
import com.example.express.network.ApiClient
import com.example.express.network.Credentials
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

        val apiService = ApiClient.getApiService(this)
        val sharedPreferences = getSharedPreferences("auth", MODE_PRIVATE)

        binding.loginButton.setOnClickListener {
            val username = binding.usernameEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val credentials = Credentials(username, password)
                    val response = apiService.login(credentials)
                    val token = response["access_token"]
                    if (token != null) {
                        sharedPreferences.edit().putString("access_token", token).apply()
                        withContext(Dispatchers.Main) {
                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                            finish()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        binding.statusTextView.text = "Ошибка входа: ${e.message}"
                    }
                }
            }
        }

        binding.registerButton.setOnClickListener {
            val username = binding.usernameEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val credentials = Credentials(username, password)
                    apiService.register(credentials)  // Здесь ошибка, если сигнатура не совпадает
                    withContext(Dispatchers.Main) {
                        binding.statusTextView.text = "Регистрация успешна, теперь войдите"
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        binding.statusTextView.text = "Ошибка регистрации: ${e.message}"
                    }
                }
            }
        }
    }
}