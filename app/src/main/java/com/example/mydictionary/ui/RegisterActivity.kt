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

class RegisterActivity : AppCompatActivity() {
    private lateinit var viewModel: UserViewModel
    private val TAG = "RegisterActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        try {
            val userDao = DictionaryDatabase.getDatabase(this).userDao()
            val repository = UserRepository(userDao)
            viewModel = ViewModelProvider(this, UserViewModelFactory(repository))[UserViewModel::class.java]

            findViewById<android.widget.Button>(R.id.registerButton).setOnClickListener {
                val username = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.usernameEditText).text?.toString() ?: ""
                val email = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.emailEditText).text?.toString() ?: ""
                val password = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.passwordEditText).text?.toString() ?: ""
                val confirmPassword = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.confirmPasswordEditText).text?.toString() ?: ""

                if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (password != confirmPassword) {
                    Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                lifecycleScope.launch {
                    try {
                        val usernameExists = viewModel.checkUsernameExists(username)
                        val emailExists = viewModel.checkEmailExists(email)

                        when {
                            usernameExists -> {
                                Toast.makeText(this@RegisterActivity, "Username already exists", Toast.LENGTH_SHORT).show()
                            }
                            emailExists -> {
                                Toast.makeText(this@RegisterActivity, "Email already exists", Toast.LENGTH_SHORT).show()
                            }
                            else -> {
                                try {
                                    viewModel.registerUser(username, password, email)
                                    Toast.makeText(this@RegisterActivity, "Registration successful", Toast.LENGTH_SHORT).show()
                                    startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                                    finish()
                                } catch (e: IllegalArgumentException) {
                                    Toast.makeText(this@RegisterActivity, e.message, Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Registration error", e)
                        Toast.makeText(this@RegisterActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }

            findViewById<android.widget.TextView>(R.id.loginTextView).setOnClickListener {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing activity", e)
            Toast.makeText(this, "Error initializing app: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
} 