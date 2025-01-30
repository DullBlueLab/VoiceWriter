package com.dullbluelab.voicewriter3.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [SymbolTable::class], version = 1, exportSchema = false)
abstract class SymbolDatabase : RoomDatabase() {
    abstract fun symbolDao(): SymbolDao

    companion object {
        @Volatile
        private var Instance: SymbolDatabase? = null

        fun getDatabase(context: Context): SymbolDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context.applicationContext, SymbolDatabase::class.java, "symbol_database")
                    .build()
                    .also { Instance = it }
            }
        }
    }
}