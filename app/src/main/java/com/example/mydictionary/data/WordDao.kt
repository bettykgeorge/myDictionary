package com.example.mydictionary.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.OnConflictStrategy
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWord(word: Word)

    @Query("SELECT * FROM words WHERE word = :word")
    suspend fun getWord(word: String): Word?

    @Query("SELECT * FROM words WHERE isOffline = 1")
    fun getOfflineWords(): Flow<List<Word>>

    @Query("UPDATE words SET isOffline = 1 WHERE word = :word")
    suspend fun markAsOffline(word: String)

    @Query("SELECT * FROM words WHERE word LIKE :query || '%'")
    fun searchWords(query: String): Flow<List<Word>>
} 