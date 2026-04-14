package com.kulothunganug.thirukkural.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.kulothunganug.thirukkural.viewmodels.SettingsUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
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

    val allSettings: Flow<SettingsUiState> = combine(
        widgetBgColor, widgetTextColor, widgetContentOrder,
        showPaal, paalFontSize, paalAlignment, paalIsBold,
        showIyal, iyalFontSize, iyalAlignment, iyalIsBold,
        showAdhigaram, adhigaramFontSize, adhigaramAlignment, adhigaramIsBold,
        showKural, kuralFontSize, kuralAlignment, kuralIsBold,
        showTransliteration, transliterationFontSize, transliterationAlignment, transliterationIsBold
    ) { args: Array<Any?> ->
        SettingsUiState(
            bgColor = args[0] as String,
            textColor = args[1] as String,
            contentOrder = args[2] as String,
            showPaal = args[3] as Boolean,
            paalSize = args[4] as Int,
            paalAlign = args[5] as String,
            paalBold = args[6] as Boolean,
            showIyal = args[7] as Boolean,
            iyalSize = args[8] as Int,
            iyalAlign = args[9] as String,
            iyalBold = args[10] as Boolean,
            showAdhigaram = args[11] as Boolean,
            adhigaramSize = args[12] as Int,
            adhigaramAlign = args[13] as String,
            adhigaramBold = args[14] as Boolean,
            showKural = args[15] as Boolean,
            kuralSize = args[16] as Int,
            kuralAlign = args[17] as String,
            kuralBold = args[18] as Boolean,
            showTranslit = args[19] as Boolean,
            translitSize = args[20] as Int,
            translitAlign = args[21] as String,
            translitBold = args[22] as Boolean
        )
    }

    suspend fun saveAll(settings: SettingsUiState) {
        context.dataStore.edit { prefs ->
            prefs[WIDGET_BG_COLOR] = settings.bgColor
            prefs[WIDGET_TEXT_COLOR] = settings.textColor
            prefs[WIDGET_CONTENT_ORDER] = settings.contentOrder

            prefs[SHOW_PAAL] = settings.showPaal
            prefs[PAAL_FONT_SIZE] = settings.paalSize
            prefs[PAAL_ALIGNMENT] = settings.paalAlign
            prefs[PAAL_IS_BOLD] = settings.paalBold

            prefs[SHOW_IYAL] = settings.showIyal
            prefs[IYAL_FONT_SIZE] = settings.iyalSize
            prefs[IYAL_ALIGNMENT] = settings.iyalAlign
            prefs[IYAL_IS_BOLD] = settings.iyalBold

            prefs[SHOW_ADHIGARAM] = settings.showAdhigaram
            prefs[ADHIGARAM_FONT_SIZE] = settings.adhigaramSize
            prefs[ADHIGARAM_ALIGNMENT] = settings.adhigaramAlign
            prefs[ADHIGARAM_IS_BOLD] = settings.adhigaramBold

            prefs[SHOW_KURAL] = settings.showKural
            prefs[KURAL_FONT_SIZE] = settings.kuralSize
            prefs[KURAL_ALIGNMENT] = settings.kuralAlign
            prefs[KURAL_IS_BOLD] = settings.kuralBold

            prefs[SHOW_TRANSLITERATION] = settings.showTranslit
            prefs[TRANSLITERATION_FONT_SIZE] = settings.translitSize
            prefs[TRANSLITERATION_ALIGNMENT] = settings.translitAlign
            prefs[TRANSLITERATION_IS_BOLD] = settings.translitBold
        }
    }
}
