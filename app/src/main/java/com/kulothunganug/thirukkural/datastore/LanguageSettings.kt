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

/** Which script is shown as the smaller supporting line under the main text. */
enum class SecondaryScript {
    TAMIL, ENGLISH, TRANSLITERATION, NONE
}

/** Scripts offered as secondary — everything except the primary app language, plus none. */
fun availableSecondaries(isTamilPrimary: Boolean): List<SecondaryScript> =
    if (isTamilPrimary) {
        listOf(SecondaryScript.ENGLISH, SecondaryScript.TRANSLITERATION, SecondaryScript.NONE)
    } else {
        listOf(SecondaryScript.TAMIL, SecondaryScript.TRANSLITERATION, SecondaryScript.NONE)
    }

/** True when this script would repeat the primary line. */
fun SecondaryScript.duplicatesPrimary(isTamilPrimary: Boolean): Boolean =
    (this == SecondaryScript.TAMIL && isTamilPrimary) ||
        (this == SecondaryScript.ENGLISH && !isTamilPrimary)

/**
 * Never show a secondary that repeats the primary — fall back to
 * transliteration, so the stored setting needs no migration when the
 * app language changes.
 */
fun SecondaryScript.coerced(isTamilPrimary: Boolean): SecondaryScript =
    if (duplicatesPrimary(isTamilPrimary)) SecondaryScript.TRANSLITERATION else this

/** Pick this script's text from a trilingual triple. NONE yields no text. */
fun SecondaryScript.text(ta: String, en: String, tl: String): String = when (this) {
    SecondaryScript.TAMIL -> ta
    SecondaryScript.ENGLISH -> en
    SecondaryScript.TRANSLITERATION -> tl
    SecondaryScript.NONE -> ""
}

private val Context.languageDataStore: DataStore<Preferences> by preferencesDataStore(name = "language_settings")

class LanguageSettings(private val context: Context) {
    companion object {
        private val SECONDARY_KEY = stringPreferencesKey("secondary_script")
    }

    val secondaryStream: Flow<SecondaryScript> = context.languageDataStore.data
        .catch { error ->
            if (error is IOException) emit(emptyPreferences()) else throw error
        }
        .map { preferences ->
            val name = preferences[SECONDARY_KEY]
            SecondaryScript.entries.firstOrNull { it.name == name }
                ?: SecondaryScript.TRANSLITERATION
        }

    suspend fun setSecondary(script: SecondaryScript) {
        context.languageDataStore.edit { preferences ->
            preferences[SECONDARY_KEY] = script.name
        }
    }
}
