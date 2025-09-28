package com.example.mydictionary.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mydictionary.model.DictionaryResponse
import com.example.mydictionary.repository.DictionaryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class DictionaryViewModel @Inject constructor(
    private val repository: DictionaryRepository
) : ViewModel() {

    private val _searchResult = MutableStateFlow<SearchResult>(SearchResult.Initial)
    val searchResult: StateFlow<SearchResult> = _searchResult

    fun searchWord(word: String) {
        viewModelScope.launch {
            _searchResult.value = SearchResult.Loading
            try {
                val response = repository.getWordDefinition(word)
                if (response.isSuccessful) {
                    response.body()?.let { definitions ->
                        _searchResult.value = SearchResult.Success(definitions)
                    } ?: run {
                        _searchResult.value = SearchResult.Error("Response body is empty")
                    }
                } else {
                    _searchResult.value = SearchResult.Error("Error: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                _searchResult.value = SearchResult.Error(e.message ?: "An error occurred")
            }
        }
    }
}

sealed class SearchResult {
    object Initial : SearchResult()
    object Loading : SearchResult()
    data class Success(val data: List<DictionaryResponse>) : SearchResult()
    data class Error(val message: String) : SearchResult()
} 