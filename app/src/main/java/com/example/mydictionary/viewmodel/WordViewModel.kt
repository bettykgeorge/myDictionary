package com.example.mydictionary.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mydictionary.data.Word
import com.example.mydictionary.repository.WordRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class WordViewModel(private val repository: WordRepository) : ViewModel() {
    fun searchWords(query: String): Flow<List<Word>> = repository.searchWords(query)

    suspend fun getWord(word: String): Word? = repository.getWord(word)

    fun getOfflineWords(): Flow<List<Word>> = repository.getOfflineWords()

    fun saveWord(word: Word) {
        viewModelScope.launch {
            repository.insertWord(word)
        }
    }

    fun markAsOffline(word: String) {
        viewModelScope.launch {
            repository.markAsOffline(word)
        }
    }
} 