package com.example.express

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.express.databinding.ActivityLoginBinding
import com.example.express.model.Credentials
import com.example.express.network.ApiClient
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

            // Для логина отправляем только username и password
            val credentials = Credentials(
                username = username,
                password = password,
                phone = "", // Пустые значения, так как не требуются для логина
                email = "",
                name = ""
            )
            loginUser(credentials)
        }

        binding.registerButton.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun loginUser(credentials: Credentials) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ApiClient.getApiService(this@LoginActivity).login(credentials)
                withContext(Dispatchers.Main) {
                    binding.statusTextView.text = "Вход успешен"
                    Log.d("LoginActivity", "Login successful: ${response["access_token"]}")
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
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