package com.kulothunganug.thirukkural.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

enum class AppTheme {
    LIGHT, DARK, SYSTEM
}

// A DataStore instance must be a process-wide singleton for a given file, per the official
// DataStore guidance — this is deliberately a top-level property (not nested in a class/companion)
// so it can never accidentally be created more than once and throw
// "There are multiple DataStores active for this file" at runtime.
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class ThemeSettings(private val context: Context) {
    companion object {
        private val THEME_KEY = stringPreferencesKey("app_theme")
    }

    val themeStream: Flow<AppTheme> = context.dataStore.data
        .catch { error ->
            // Treat a corrupted preferences file as "no preference saved yet" rather than
            // crashing every screen that collects the theme.
            if (error is IOException) emit(emptyPreferences()) else throw error
        }
        .map { preferences ->
            val themeName = preferences[THEME_KEY]
            AppTheme.entries.firstOrNull { it.name == themeName } ?: AppTheme.SYSTEM
        }

    suspend fun setTheme(theme: AppTheme) {
        context.dataStore.edit { preferences ->
            preferences[THEME_KEY] = theme.name
        }
    }
}
