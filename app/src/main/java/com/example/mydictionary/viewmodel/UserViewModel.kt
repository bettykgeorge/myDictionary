package com.example.mydictionary.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mydictionary.data.User
import com.example.mydictionary.repository.UserRepository
import kotlinx.coroutines.launch

class UserViewModel(private val repository: UserRepository) : ViewModel() {
    fun registerUser(username: String, password: String, email: String) {
        if (!isValidEmail(email)) {
            throw IllegalArgumentException("Invalid email format. Please use a valid email with gmail.com or email.com domain")
        }
        viewModelScope.launch {
            val user = User(username = username, password = password, email = email)
            repository.insertUser(user)
        }
    }

    suspend fun login(username: String, password: String): User? {
        return repository.getUser(username, password)
    }

    suspend fun checkUsernameExists(username: String): Boolean {
        return repository.getUserByUsername(username) != null
    }

    suspend fun checkEmailExists(email: String): Boolean {
        return repository.getUserByEmail(email) != null
    }

    private fun isValidEmail(email: String): Boolean {
        return email.isNotEmpty() && 
               email.contains("@") && 
               (email.endsWith("gmail.com") || email.endsWith("email.com"))
    }
} 