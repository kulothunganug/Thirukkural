package com.kulothunganug.thirukkural.viewmodels

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.getAppWidgetState
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kulothunganug.thirukkural.widget.ThirukkuralWidget
import com.kulothunganug.thirukkural.widget.ThirukkuralWidgetKeys
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class SettingsUiState(
    val bgColor: String = "#FFFFFF",
    val textColor: String = "#000000",
    val contentOrder: String = "PAAL,IYAL,ADHIGARAM,KURAL,TRANSLITERATION",
    val showPaal: Boolean = false,
    val paalSize: Int = 12,
    val paalAlign: String = "CENTER",
    val paalBold: Boolean = false,
    val showIyal: Boolean = false,
    val iyalSize: Int = 12,
    val iyalAlign: String = "CENTER",
    val iyalBold: Boolean = false,
    val showAdhigaram: Boolean = true,
    val adhigaramSize: Int = 15,
    val adhigaramAlign: String = "CENTER",
    val adhigaramBold: Boolean = true,
    val showKural: Boolean = true,
    val kuralSize: Int = 14,
    val kuralBold: Boolean = false,
)

class WidgetConfigurationViewModel(
    private val context: Context,
    private val appWidgetId: Int
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    private var glanceId: GlanceId? = null

    init {
        viewModelScope.launch {
            glanceId = GlanceAppWidgetManager(context).getGlanceIdBy(appWidgetId)
            glanceId?.let { id ->
                val prefs = getAppWidgetState(context, PreferencesGlanceStateDefinition, id)
                _uiState.value = SettingsUiState(
                    bgColor = prefs[ThirukkuralWidgetKeys.BG_COLOR] ?: ThirukkuralWidgetKeys.DEFAULT_BG_COLOR,
                    textColor = prefs[ThirukkuralWidgetKeys.TEXT_COLOR] ?: ThirukkuralWidgetKeys.DEFAULT_TEXT_COLOR,
                    contentOrder = prefs[ThirukkuralWidgetKeys.CONTENT_ORDER] ?: ThirukkuralWidgetKeys.DEFAULT_CONTENT_ORDER,
                    showPaal = prefs[ThirukkuralWidgetKeys.SHOW_PAAL] ?: false,
                    paalSize = prefs[ThirukkuralWidgetKeys.PAAL_SIZE] ?: 12,
                    paalAlign = prefs[ThirukkuralWidgetKeys.PAAL_ALIGN] ?: "CENTER",
                    paalBold = prefs[ThirukkuralWidgetKeys.PAAL_BOLD] ?: false,
                    showIyal = prefs[ThirukkuralWidgetKeys.SHOW_IYAL] ?: false,
                    iyalSize = prefs[ThirukkuralWidgetKeys.IYAL_SIZE] ?: 12,
                    iyalAlign = prefs[ThirukkuralWidgetKeys.IYAL_ALIGN] ?: "CENTER",
                    iyalBold = prefs[ThirukkuralWidgetKeys.IYAL_BOLD] ?: false,
                    showAdhigaram = prefs[ThirukkuralWidgetKeys.SHOW_ADHIGARAM] ?: true,
                    adhigaramSize = prefs[ThirukkuralWidgetKeys.ADHIGARAM_SIZE] ?: 15,
                    adhigaramAlign = prefs[ThirukkuralWidgetKeys.ADHIGARAM_ALIGN] ?: "CENTER",
                    adhigaramBold = prefs[ThirukkuralWidgetKeys.ADHIGARAM_BOLD] ?: true,
                    showKural = prefs[ThirukkuralWidgetKeys.SHOW_KURAL] ?: true,
                    kuralSize = prefs[ThirukkuralWidgetKeys.KURAL_SIZE] ?: 14,
                    kuralBold = prefs[ThirukkuralWidgetKeys.KURAL_BOLD] ?: false,
                )
            }
        }
    }

    fun updateBgColor(color: String) {
        _uiState.update { it.copy(bgColor = color) }
    }

    fun updateTextColor(color: String) {
        _uiState.update { it.copy(textColor = color) }
    }

    fun updateContentOrder(order: String) {
        _uiState.update { it.copy(contentOrder = order) }
    }

    fun updatePaalSettings(show: Boolean? = null, size: Int? = null, align: String? = null, bold: Boolean? = null) {
        _uiState.update { current ->
            current.copy(
                showPaal = show ?: current.showPaal,
                paalSize = size ?: current.paalSize,
                paalAlign = align ?: current.paalAlign,
                paalBold = bold ?: current.paalBold
            )
        }
    }

    fun updateIyalSettings(show: Boolean? = null, size: Int? = null, align: String? = null, bold: Boolean? = null) {
        _uiState.update { current ->
            current.copy(
                showIyal = show ?: current.showIyal,
                iyalSize = size ?: current.iyalSize,
                iyalAlign = align ?: current.iyalAlign,
                iyalBold = bold ?: current.iyalBold
            )
        }
    }

    fun updateAdhigaramSettings(show: Boolean? = null, size: Int? = null, align: String? = null, bold: Boolean? = null) {
        _uiState.update { current ->
            current.copy(
                showAdhigaram = show ?: current.showAdhigaram,
                adhigaramSize = size ?: current.adhigaramSize,
                adhigaramAlign = align ?: current.adhigaramAlign,
                adhigaramBold = bold ?: current.adhigaramBold
            )
        }
    }

    fun updateKuralSettings(show: Boolean? = null, size: Int? = null, align: String? = null, bold: Boolean? = null) {
        _uiState.update { current ->
            current.copy(
                showKural = show ?: current.showKural,
                kuralSize = size ?: current.kuralSize,
                kuralBold = bold ?: current.kuralBold
            )
        }
    }


    fun saveSettings() = viewModelScope.launch {
        glanceId?.let { id ->
            updateAppWidgetState(context, PreferencesGlanceStateDefinition, id) { prefs ->
                prefs.toMutablePreferences().apply {
                    val s = uiState.value
                    this[ThirukkuralWidgetKeys.BG_COLOR] = s.bgColor
                    this[ThirukkuralWidgetKeys.TEXT_COLOR] = s.textColor
                    this[ThirukkuralWidgetKeys.CONTENT_ORDER] = s.contentOrder
                    this[ThirukkuralWidgetKeys.SHOW_PAAL] = s.showPaal
                    this[ThirukkuralWidgetKeys.PAAL_SIZE] = s.paalSize
                    this[ThirukkuralWidgetKeys.PAAL_ALIGN] = s.paalAlign
                    this[ThirukkuralWidgetKeys.PAAL_BOLD] = s.paalBold
                    this[ThirukkuralWidgetKeys.SHOW_IYAL] = s.showIyal
                    this[ThirukkuralWidgetKeys.IYAL_SIZE] = s.iyalSize
                    this[ThirukkuralWidgetKeys.IYAL_ALIGN] = s.iyalAlign
                    this[ThirukkuralWidgetKeys.IYAL_BOLD] = s.iyalBold
                    this[ThirukkuralWidgetKeys.SHOW_ADHIGARAM] = s.showAdhigaram
                    this[ThirukkuralWidgetKeys.ADHIGARAM_SIZE] = s.adhigaramSize
                    this[ThirukkuralWidgetKeys.ADHIGARAM_ALIGN] = s.adhigaramAlign
                    this[ThirukkuralWidgetKeys.ADHIGARAM_BOLD] = s.adhigaramBold
                    this[ThirukkuralWidgetKeys.SHOW_KURAL] = s.showKural
                    this[ThirukkuralWidgetKeys.KURAL_SIZE] = s.kuralSize
                    this[ThirukkuralWidgetKeys.KURAL_BOLD] = s.kuralBold
                }
            }
            ThirukkuralWidget().update(context, id)
        }
    }
}
