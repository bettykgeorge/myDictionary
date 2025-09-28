package com.example.mydictionary.api

import com.example.mydictionary.model.DictionaryResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface DictionaryApi {
    @GET("entries/en/{word}")
    suspend fun getWordMeaning(@Path("word") word: String): Response<List<DictionaryResponse>>
} 