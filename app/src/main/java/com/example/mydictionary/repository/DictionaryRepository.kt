package com.example.mydictionary.repository

import com.example.mydictionary.api.DictionaryApi
import com.example.mydictionary.model.DictionaryResponse
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DictionaryRepository @Inject constructor(
    private val dictionaryApi: DictionaryApi
) {
    suspend fun getWordDefinition(word: String): Response<List<DictionaryResponse>> {
        return dictionaryApi.getWordMeaning(word)
    }
} 