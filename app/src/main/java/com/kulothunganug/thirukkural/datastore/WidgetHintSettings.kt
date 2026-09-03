package com.kulothunganug.thirukkural.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.widgetHintDataStore: DataStore<Preferences> by preferencesDataStore(name = "widget_hint")

/** Tracks whether the user has already been shown the "add this as a widget" hint on the home screen. */
class WidgetHintSettings(private val context: Context) {
    companion object {
        private val HAS_SHOWN_WIDGET_HINT_KEY = booleanPreferencesKey("has_shown_widget_hint")
    }

    val hasShownWidgetHintStream: Flow<Boolean> = context.widgetHintDataStore.data
        .catch { error ->
            if (error is IOException) emit(emptyPreferences()) else throw error
        }
        .map { preferences -> preferences[HAS_SHOWN_WIDGET_HINT_KEY] ?: false }

    suspend fun setWidgetHintShown() {
        context.widgetHintDataStore.edit { preferences ->
            preferences[HAS_SHOWN_WIDGET_HINT_KEY] = true
        }
    }
}
