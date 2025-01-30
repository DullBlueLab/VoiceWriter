package com.dullbluelab.voicewriter3.data

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class UserPreferencesRepository(
    private val dataStore: DataStore<Preferences>
) {
    private companion object {
        val KEY_CURSOR = stringPreferencesKey("key_cursor_char")
        val KEY_SYMBOL_PAD_PRIORITY = booleanPreferencesKey("key_symbol_pad_priority")

        private const val DEFAULT_CURSOR_CHAR = "_"
        private const val DEFAULT_SYMBOL_PAD_PRIORITY = false

        private const val TAG = "UserPreferencesRepository"
    }

    data class State(
        var cursorChar: String = DEFAULT_CURSOR_CHAR,
        var symbolPadPriority: Boolean = DEFAULT_SYMBOL_PAD_PRIORITY,
    )

    suspend fun save(setting: State) {
        dataStore.edit { preferences ->
            preferences[KEY_CURSOR] = setting.cursorChar
            preferences[KEY_SYMBOL_PAD_PRIORITY] = setting.symbolPadPriority
        }
    }

    suspend fun updateCursorChar(chars: String) {
        dataStore.edit { preferences ->
            preferences[KEY_CURSOR] = chars
        }
    }

    suspend fun updateSymbolPadPriority(flag: Boolean) {
        dataStore.edit { preferences ->
            preferences[KEY_SYMBOL_PAD_PRIORITY] = flag
        }
    }

    val setting: Flow<State> = dataStore.data
        .catch {
            if (it is IOException) {
                Log.e(TAG, "Error reading preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preference ->
            State(
                cursorChar = preference[KEY_CURSOR] ?: DEFAULT_CURSOR_CHAR,
                symbolPadPriority = preference[KEY_SYMBOL_PAD_PRIORITY] ?: DEFAULT_SYMBOL_PAD_PRIORITY
            )
        }

}
