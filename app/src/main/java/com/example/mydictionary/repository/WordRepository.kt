package com.example.mydictionary.repository

import com.example.mydictionary.data.Word
import com.example.mydictionary.data.WordDao
import kotlinx.coroutines.flow.Flow

class WordRepository(private val wordDao: WordDao) {
    suspend fun insertWord(word: Word) = wordDao.insertWord(word)

    suspend fun getWord(word: String) = wordDao.getWord(word)

    fun getOfflineWords(): Flow<List<Word>> = wordDao.getOfflineWords()

    suspend fun markAsOffline(word: String) = wordDao.markAsOffline(word)

    fun searchWords(query: String): Flow<List<Word>> = wordDao.searchWords(query)
} 