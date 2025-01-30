package com.dullbluelab.voicewriter3

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.dullbluelab.voicewriter3.data.SymbolDatabase
import com.dullbluelab.voicewriter3.data.UserPreferencesRepository
import com.dullbluelab.voicewriter3.data.WriterRepository

private const val PREFERENCE_NAME = "voice_writer_preferences"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = PREFERENCE_NAME)

class VoiceWriterApplication : Application() {
    lateinit var container: AppContainer
    lateinit var userPreferencesRepository: UserPreferencesRepository

    override fun onCreate() {
        super.onCreate()
        val database = SymbolDatabase.getDatabase(this)
        container = AppDataContainer(database,this)
        userPreferencesRepository = UserPreferencesRepository(dataStore)
    }
}

interface AppContainer {
    val writerRepository: WriterRepository
}

class AppDataContainer(private val database: SymbolDatabase, private val context: Context) : AppContainer {
    override val writerRepository: WriterRepository by lazy {
        WriterRepository(database)
    }
}