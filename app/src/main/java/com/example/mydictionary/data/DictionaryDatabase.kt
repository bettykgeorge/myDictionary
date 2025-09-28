package com.example.mydictionary.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [User::class, Word::class], version = 2, exportSchema = false)
abstract class DictionaryDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun wordDao(): WordDao

    companion object {
        @Volatile
        private var INSTANCE: DictionaryDatabase? = null

        fun getDatabase(context: Context): DictionaryDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DictionaryDatabase::class.java,
                    "dictionary_database"
                )
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries() // For emulator testing
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Create tables when database is created
                        db.execSQL("CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, username TEXT NOT NULL, password TEXT NOT NULL, email TEXT NOT NULL)")
                        db.execSQL("CREATE TABLE IF NOT EXISTS words (word TEXT PRIMARY KEY NOT NULL, meaning TEXT NOT NULL, phonetic TEXT, example TEXT, isOffline INTEGER NOT NULL)")
                    }

                    override fun onDestructiveMigration(db: SupportSQLiteDatabase) {
                        super.onDestructiveMigration(db)
                        // Recreate tables after destructive migration
                        db.execSQL("CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, username TEXT NOT NULL, password TEXT NOT NULL, email TEXT NOT NULL)")
                        db.execSQL("CREATE TABLE IF NOT EXISTS words (word TEXT PRIMARY KEY NOT NULL, meaning TEXT NOT NULL, phonetic TEXT, example TEXT, isOffline INTEGER NOT NULL)")
                    }
                })
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
} 