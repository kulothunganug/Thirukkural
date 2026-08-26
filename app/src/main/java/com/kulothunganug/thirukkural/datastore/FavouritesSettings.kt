package com.kulothunganug.thirukkural.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.favouritesDataStore: DataStore<Preferences> by preferencesDataStore(name = "favourites")

class FavouritesSettings(private val context: Context) {
    companion object {
        private val FAVOURITE_KURAL_IDS_KEY = stringSetPreferencesKey("favourite_kural_ids")
    }

    val favouriteIdsStream: Flow<Set<Int>> = context.favouritesDataStore.data
        .catch { error ->
            if (error is IOException) emit(emptyPreferences()) else throw error
        }
        .map { preferences ->
            preferences[FAVOURITE_KURAL_IDS_KEY]
                ?.mapNotNull { it.toIntOrNull() }
                ?.toSet()
                ?: emptySet()
        }

    fun isFavourite(kuralId: Int): Flow<Boolean> =
        favouriteIdsStream.map { it.contains(kuralId) }

    suspend fun toggleFavourite(kuralId: Int) {
        context.favouritesDataStore.edit { preferences ->
            val current = preferences[FAVOURITE_KURAL_IDS_KEY] ?: emptySet()
            val idString = kuralId.toString()
            preferences[FAVOURITE_KURAL_IDS_KEY] =
                if (current.contains(idString)) current - idString else current + idString
        }
    }
}
