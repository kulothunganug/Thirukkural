package com.kulothunganug.thirukkural.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.shareDataStore: DataStore<Preferences> by preferencesDataStore(name = "share_settings")

data class ShareConfig(
    val order: List<String>,
    val enabledSections: Set<String>
)

class ShareSettings(private val context: Context) {
    companion object {
        private val ORDER_KEY = stringPreferencesKey("share_order")
        private val ENABLED_SECTIONS_KEY = stringSetPreferencesKey("share_enabled_sections")

        val DEFAULT_ORDER = listOf("header", "kural", "transliteration", "explanation", "translation")
        val DEFAULT_ENABLED = setOf("header", "kural", "explanation")
    }

    val configStream: Flow<ShareConfig> = context.shareDataStore.data
        .catch { error ->
            if (error is IOException) emit(emptyPreferences()) else throw error
        }
        .map { preferences ->
            val orderStr = preferences[ORDER_KEY]
            val order = if (orderStr.isNullOrEmpty()) {
                DEFAULT_ORDER
            } else {
                orderStr.split(",")
            }

            val enabled = preferences[ENABLED_SECTIONS_KEY] ?: DEFAULT_ENABLED

            ShareConfig(order, enabled)
        }

    suspend fun saveConfig(config: ShareConfig) {
        context.shareDataStore.edit { preferences ->
            preferences[ORDER_KEY] = config.order.joinToString(",")
            preferences[ENABLED_SECTIONS_KEY] = config.enabledSections
        }
    }
}
