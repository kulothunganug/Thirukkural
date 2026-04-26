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
import com.kulothunganug.thirukkural.widget.ThirukkuralWidgetKeys
import com.kulothunganug.thirukkural.widget.WidgetConfig
import com.kulothunganug.thirukkural.widget.WidgetTextAlign
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

data class SettingsUiState(
    val config: WidgetConfig = WidgetConfig()
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
                val json = prefs[com.kulothunganug.thirukkural.widget.CONFIG]
                val config = json?.let {
                    Json.decodeFromString<WidgetConfig>(it)
                } ?: WidgetConfig()
                _uiState.value = SettingsUiState(config = config)
            }
        }
    }

    fun updateBgColor(color: String) {
        _uiState.update { it.copy(config = it.config.copy(bgColor = color)) }
    }

    fun updateTextColor(color: String) {
        _uiState.update { it.copy(config = it.config.copy(textColor = color)) }
    }

    fun updateContentOrder(order: List<SectionConfig>) {
        _uiState.update { it.copy(config = it.config.copy(contentOrder = order)) }
    }

    fun updateSectionSettings(
        type: ContentType,
        show: Boolean? = null,
        size: Int? = null,
        align: WidgetTextAlign? = null,
        bold: Boolean? = null
    ) {
        _uiState.update { current ->
            val updatedOrder = current.config.contentOrder.map { section ->
                if (section.type == type) {
                    section.copy(
                        show = show ?: section.show,
                        size = size ?: section.size,
                        align = align ?: section.align,
                        bold = bold ?: section.bold
                    )
                } else {
                    section
                }
            }
            current.copy(config = current.config.copy(contentOrder = updatedOrder))
        }
    }


    fun saveSettings() = viewModelScope.launch {
        glanceId?.let { id ->
            updateAppWidgetState(context, PreferencesGlanceStateDefinition, id) { prefs ->
                prefs.toMutablePreferences().apply {
                    val config = uiState.value.config
                    this[com.kulothunganug.thirukkural.widget.CONFIG] = Json.encodeToString(config)
                }
            }
            ThirukkuralWidget().update(context, id)
        }
    }
}
