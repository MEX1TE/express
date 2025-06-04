package com.example.express

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.express.databinding.ActivityRegisterBinding
import com.example.express.model.Credentials
import com.example.express.network.ApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.registerButton.setOnClickListener {
            val username = binding.usernameEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()
            val phone = binding.phoneEditText.text.toString().trim()
            val email = binding.emailEditText.text.toString().trim()
            val name = binding.nameEditText.text.toString().trim()

            if (username.isBlank() || password.isBlank() || phone.isBlank() || email.isBlank() || name.isBlank()) {
                binding.statusTextView.text = "Заполните все поля"
                return@setOnClickListener
            }

            val credentials = Credentials(username, password, phone, email, name)
            registerUser(credentials)
        }

        binding.backToLoginButton.setOnClickListener {
            finish()
        }
    }

    private fun registerUser(credentials: Credentials) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ApiClient.instance.register(credentials)
                withContext(Dispatchers.Main) {
                    binding.statusTextView.text = response["message"] ?: ""
                    Log.d("RegisterActivity", "Registration successful: ${response["message"]}")
                    finish() // Возвращаемся к LoginActivity после успешной регистрации
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    binding.statusTextView.text = "Ошибка регистрации: ${e.message}"
                    Log.e("RegisterActivity", "Registration error: ${e.message}", e)
                }
            }
        }
    }
}