package com.example.mydictionary.repository

import com.example.mydictionary.data.User
import com.example.mydictionary.data.UserDao
import kotlinx.coroutines.flow.Flow

class UserRepository(private val userDao: UserDao) {
    suspend fun insertUser(user: User) = userDao.insertUser(user)

    suspend fun getUser(username: String, password: String) = userDao.getUser(username, password)

    suspend fun getUserByUsername(username: String) = userDao.getUserByUsername(username)

    suspend fun getUserByEmail(email: String) = userDao.getUserByEmail(email)
} 