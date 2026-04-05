package com.kulothunganug.thirukkural.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsDatastore(private val context: Context) {
    companion object {
        val WIDGET_BG_COLOR = stringPreferencesKey("widget_bg_color")
        val WIDGET_TEXT_COLOR = stringPreferencesKey("widget_text_color")
        val WIDGET_CONTENT_ORDER = stringPreferencesKey("widget_content_order")

        // Visibility
        val SHOW_PAAL = booleanPreferencesKey("show_paal")
        val SHOW_IYAL = booleanPreferencesKey("show_iyal")
        val SHOW_ADHIGARAM = booleanPreferencesKey("show_adhigaram")
        val SHOW_KURAL = booleanPreferencesKey("show_kural")
        val SHOW_TRANSLITERATION = booleanPreferencesKey("show_transliteration")

        // Styling keys
        val PAAL_FONT_SIZE = intPreferencesKey("paal_font_size")
        val PAAL_ALIGNMENT = stringPreferencesKey("paal_alignment")
        val PAAL_IS_BOLD = booleanPreferencesKey("paal_is_bold")

        val IYAL_FONT_SIZE = intPreferencesKey("iyal_font_size")
        val IYAL_ALIGNMENT = stringPreferencesKey("iyal_alignment")
        val IYAL_IS_BOLD = booleanPreferencesKey("iyal_is_bold")

        val ADHIGARAM_FONT_SIZE = intPreferencesKey("adhigaram_font_size")
        val ADHIGARAM_ALIGNMENT = stringPreferencesKey("adhigaram_alignment")
        val ADHIGARAM_IS_BOLD = booleanPreferencesKey("adhigaram_is_bold")

        val KURAL_FONT_SIZE = intPreferencesKey("kural_font_size")
        val KURAL_ALIGNMENT = stringPreferencesKey("kural_alignment")
        val KURAL_IS_BOLD = booleanPreferencesKey("kural_is_bold")

        val TRANSLITERATION_FONT_SIZE = intPreferencesKey("transliteration_font_size")
        val TRANSLITERATION_ALIGNMENT = stringPreferencesKey("transliteration_alignment")
        val TRANSLITERATION_IS_BOLD = booleanPreferencesKey("transliteration_is_bold")

        const val DEFAULT_BG_COLOR = "#FFFFFF"
        const val DEFAULT_TEXT_COLOR = "#000000"
        const val DEFAULT_CONTENT_ORDER = "PAAL,IYAL,ADHIGARAM,KURAL,TRANSLITERATION"
        const val DEFAULT_ALIGNMENT = "CENTER"
    }

    // Flows
    val widgetBgColor: Flow<String> = context.dataStore.data.map { it[WIDGET_BG_COLOR] ?: DEFAULT_BG_COLOR }
    val widgetTextColor: Flow<String> = context.dataStore.data.map { it[WIDGET_TEXT_COLOR] ?: DEFAULT_TEXT_COLOR }
    val widgetContentOrder: Flow<String> = context.dataStore.data.map { it[WIDGET_CONTENT_ORDER] ?: DEFAULT_CONTENT_ORDER }

    val showPaal: Flow<Boolean> = context.dataStore.data.map { it[SHOW_PAAL] ?: false }
    val showIyal: Flow<Boolean> = context.dataStore.data.map { it[SHOW_IYAL] ?: false }
    val showAdhigaram: Flow<Boolean> = context.dataStore.data.map { it[SHOW_ADHIGARAM] ?: true }
    val showKural: Flow<Boolean> = context.dataStore.data.map { it[SHOW_KURAL] ?: true }
    val showTransliteration: Flow<Boolean> = context.dataStore.data.map { it[SHOW_TRANSLITERATION] ?: false }

    val paalFontSize: Flow<Int> = context.dataStore.data.map { it[PAAL_FONT_SIZE] ?: 12 }
    val paalAlignment: Flow<String> = context.dataStore.data.map { it[PAAL_ALIGNMENT] ?: DEFAULT_ALIGNMENT }
    val paalIsBold: Flow<Boolean> = context.dataStore.data.map { it[PAAL_IS_BOLD] ?: false }

    val iyalFontSize: Flow<Int> = context.dataStore.data.map { it[IYAL_FONT_SIZE] ?: 12 }
    val iyalAlignment: Flow<String> = context.dataStore.data.map { it[IYAL_ALIGNMENT] ?: DEFAULT_ALIGNMENT }
    val iyalIsBold: Flow<Boolean> = context.dataStore.data.map { it[IYAL_IS_BOLD] ?: false }

    val adhigaramFontSize: Flow<Int> = context.dataStore.data.map { it[ADHIGARAM_FONT_SIZE] ?: 15 }
    val adhigaramAlignment: Flow<String> = context.dataStore.data.map { it[ADHIGARAM_ALIGNMENT] ?: DEFAULT_ALIGNMENT }
    val adhigaramIsBold: Flow<Boolean> = context.dataStore.data.map { it[ADHIGARAM_IS_BOLD] ?: true }

    val kuralFontSize: Flow<Int> = context.dataStore.data.map { it[KURAL_FONT_SIZE] ?: 14 }
    val kuralAlignment: Flow<String> = context.dataStore.data.map { it[KURAL_ALIGNMENT] ?: DEFAULT_ALIGNMENT }
    val kuralIsBold: Flow<Boolean> = context.dataStore.data.map { it[KURAL_IS_BOLD] ?: false }

    val transliterationFontSize: Flow<Int> = context.dataStore.data.map { it[TRANSLITERATION_FONT_SIZE] ?: 13 }
    val transliterationAlignment: Flow<String> = context.dataStore.data.map { it[TRANSLITERATION_ALIGNMENT] ?: DEFAULT_ALIGNMENT }
    val transliterationIsBold: Flow<Boolean> = context.dataStore.data.map { it[TRANSLITERATION_IS_BOLD] ?: false }

    // Save functions
    suspend fun saveWidgetBgColor(color: String) = context.dataStore.edit { it[WIDGET_BG_COLOR] = color }
    suspend fun saveWidgetTextColor(color: String) = context.dataStore.edit { it[WIDGET_TEXT_COLOR] = color }
    suspend fun saveWidgetContentOrder(order: String) = context.dataStore.edit { it[WIDGET_CONTENT_ORDER] = order }

    suspend fun saveShowPaal(show: Boolean) = context.dataStore.edit { it[SHOW_PAAL] = show }
    suspend fun saveShowIyal(show: Boolean) = context.dataStore.edit { it[SHOW_IYAL] = show }
    suspend fun saveShowAdhigaram(show: Boolean) = context.dataStore.edit { it[SHOW_ADHIGARAM] = show }
    suspend fun saveShowKural(show: Boolean) = context.dataStore.edit { it[SHOW_KURAL] = show }
    suspend fun saveShowTransliteration(show: Boolean) = context.dataStore.edit { it[SHOW_TRANSLITERATION] = show }

    suspend fun savePaalFontSize(size: Int) = context.dataStore.edit { it[PAAL_FONT_SIZE] = size }
    suspend fun savePaalAlignment(align: String) = context.dataStore.edit { it[PAAL_ALIGNMENT] = align }
    suspend fun savePaalIsBold(bold: Boolean) = context.dataStore.edit { it[PAAL_IS_BOLD] = bold }

    suspend fun saveIyalFontSize(size: Int) = context.dataStore.edit { it[IYAL_FONT_SIZE] = size }
    suspend fun saveIyalAlignment(align: String) = context.dataStore.edit { it[IYAL_ALIGNMENT] = align }
    suspend fun saveIyalIsBold(bold: Boolean) = context.dataStore.edit { it[IYAL_IS_BOLD] = bold }

    suspend fun saveAdhigaramFontSize(size: Int) = context.dataStore.edit { it[ADHIGARAM_FONT_SIZE] = size }
    suspend fun saveAdhigaramAlignment(align: String) = context.dataStore.edit { it[ADHIGARAM_ALIGNMENT] = align }
    suspend fun saveAdhigaramIsBold(bold: Boolean) = context.dataStore.edit { it[ADHIGARAM_IS_BOLD] = bold }

    suspend fun saveKuralFontSize(size: Int) = context.dataStore.edit { it[KURAL_FONT_SIZE] = size }
    suspend fun saveKuralAlignment(align: String) = context.dataStore.edit { it[KURAL_ALIGNMENT] = align }
    suspend fun saveKuralIsBold(bold: Boolean) = context.dataStore.edit { it[KURAL_IS_BOLD] = bold }

    suspend fun saveTransliterationFontSize(size: Int) = context.dataStore.edit { it[TRANSLITERATION_FONT_SIZE] = size }
    suspend fun saveTransliterationAlignment(align: String) = context.dataStore.edit { it[TRANSLITERATION_ALIGNMENT] = align }
    suspend fun saveTransliterationIsBold(bold: Boolean) = context.dataStore.edit { it[TRANSLITERATION_IS_BOLD] = bold }
}
