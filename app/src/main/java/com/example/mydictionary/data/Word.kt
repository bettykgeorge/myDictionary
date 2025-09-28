package com.example.mydictionary.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "words")
data class Word(
    @PrimaryKey
    val word: String,
    val meaning: String,
    val phonetic: String? = null,
    val example: String? = null,
    val isOffline: Boolean = false
) 