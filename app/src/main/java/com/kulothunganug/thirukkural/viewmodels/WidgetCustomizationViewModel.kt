package com.kulothunganug.thirukkural.viewmodels

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.getAppWidgetState
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kulothunganug.thirukkural.widget.ContentType
import com.kulothunganug.thirukkural.widget.SectionConfig
import com.kulothunganug.thirukkural.widget.ThirukkuralWidget
import com.kulothunganug.thirukkural.widget.WidgetConfig
import com.kulothunganug.thirukkural.widget.WidgetTextAlign
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class WidgetCustomizationViewModel(
    private val context: Context,
    private val appWidgetId: Int
) : ViewModel() {

    private val _uiState = MutableStateFlow(WidgetConfig())
    val uiState: StateFlow<WidgetConfig> = _uiState.asStateFlow()

    private val _openBgColorChooser = MutableStateFlow(false)
    val openBgColorChooser: StateFlow<Boolean> = _openBgColorChooser.asStateFlow()

    private val _openRefreshColorChooser = MutableStateFlow(false)
    val openRefreshColorChooser: StateFlow<Boolean> = _openRefreshColorChooser.asStateFlow()

    private var glanceId: GlanceId? = null

    init {
        viewModelScope.launch {
            glanceId = GlanceAppWidgetManager(context).getGlanceIdBy(appWidgetId)
            glanceId?.let { id ->
                val prefs = getAppWidgetState(context, PreferencesGlanceStateDefinition, id)
                val json = prefs[com.kulothunganug.thirukkural.widget.CONFIG]
                val config = json?.let {
                    Json.decodeFromString<WidgetConfig>(it)
                } ?: WidgetConfig()
                _uiState.value = config
            }
        }
    }

    fun toggleBgColorChooser(isOpen: Boolean){
        _openBgColorChooser.value = isOpen
    }

    fun toggleRefreshColorChooser(isOpen: Boolean){
        _openRefreshColorChooser.value = isOpen
    }

    fun updateBgColor(color: String) {
        _uiState.update { it.copy(bgColor = color) }
    }

    fun updateRefreshButtonColor(color: String) {
        _uiState.update { it.copy(refreshButtonColor = color) }
    }

    fun updateContentOrder(order: List<SectionConfig>) {
        _uiState.update { it.copy(contentOrder = order) }
    }

    fun updateSectionSettings(
        type: ContentType,
        show: Boolean? = null,
        size: Int? = null,
        align: WidgetTextAlign? = null,
        bold: Boolean? = null,
        textColor: String? = null
    ) {
        _uiState.update { current ->
            val updatedOrder = current.contentOrder.map { section ->
                if (section.type == type) {
                    section.copy(
                        show = show ?: section.show,
                        size = size ?: section.size,
                        align = align ?: section.align,
                        bold = bold ?: section.bold,
                        textColor = textColor ?: section.textColor
                    )
                } else {
                    section
                }
            }
            current.copy(contentOrder = updatedOrder)
        }
    }


    fun saveSettings() = viewModelScope.launch {
        glanceId?.let { id ->
            updateAppWidgetState(context, PreferencesGlanceStateDefinition, id) { prefs ->
                prefs.toMutablePreferences().apply {
                    val config = uiState.value
                    this[com.kulothunganug.thirukkural.widget.CONFIG] = Json.encodeToString(config)
                }
            }
            ThirukkuralWidget().update(context, id)
        }
    }
}
