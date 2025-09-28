package com.example.mydictionary.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.mydictionary.R
import com.example.mydictionary.data.DictionaryDatabase
import com.example.mydictionary.repository.UserRepository
import com.example.mydictionary.viewmodel.UserViewModel
import com.example.mydictionary.viewmodel.UserViewModelFactory
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var viewModel: UserViewModel
    private val TAG = "LoginActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        try {
            val userDao = DictionaryDatabase.getDatabase(this).userDao()
            val repository = UserRepository(userDao)
            viewModel = ViewModelProvider(this, UserViewModelFactory(repository))[UserViewModel::class.java]

            findViewById<android.widget.Button>(R.id.loginButton).setOnClickListener {
                val username = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.usernameEditText).text?.toString() ?: ""
                val password = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.passwordEditText).text?.toString() ?: ""

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                lifecycleScope.launch {
                    try {
                        Log.d(TAG, "Attempting login with username: $username")
                        val user = viewModel.login(username, password)
                        if (user != null) {
                            Log.d(TAG, "Login successful")
                            val intent = Intent(this@LoginActivity, DictionaryActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Log.d(TAG, "Login failed - invalid credentials")
                            Toast.makeText(this@LoginActivity, "Invalid credentials", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Login error", e)
                        Toast.makeText(this@LoginActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }

            findViewById<android.widget.TextView>(R.id.registerTextView).setOnClickListener {
                startActivity(Intent(this, RegisterActivity::class.java))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing activity", e)
            Toast.makeText(this, "Error initializing app: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
} 