package com.kulothunganug.thirukkural.viewmodels

import android.content.Context
import android.util.Log
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
import com.kulothunganug.thirukkural.widget.WIDGET_CONFIG
import com.kulothunganug.thirukkural.widget.WidgetConfig
import com.kulothunganug.thirukkural.widget.WidgetTextAlign
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

private const val TAG = "WidgetCustomizationVM"

class WidgetCustomizationViewModel(
    private val context: Context,
    private val appWidgetId: Int
) : ViewModel() {

    private val _uiState = MutableStateFlow(WidgetConfig())
    val uiState: StateFlow<WidgetConfig> = _uiState.asStateFlow()

    // Loading is true until the existing widget config has been read from disk. The view uses
    // this to keep editing (and saving) disabled until then, so a fast tap on "save" can never
    // race the initial load and silently no-op, and the initial load can never silently
    // overwrite edits the user already made while it was still in flight.
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _loadFailed = MutableStateFlow(false)
    val loadFailed: StateFlow<Boolean> = _loadFailed.asStateFlow()

    private val _openBgColorChooser = MutableStateFlow(false)
    val openBgColorChooser: StateFlow<Boolean> = _openBgColorChooser.asStateFlow()

    private val _openRefreshColorChooser = MutableStateFlow(false)
    val openRefreshColorChooser: StateFlow<Boolean> = _openRefreshColorChooser.asStateFlow()

    private var glanceId: GlanceId? = null

    init {
        viewModelScope.launch {
            try {
                val id = GlanceAppWidgetManager(context).getGlanceIdBy(appWidgetId)
                glanceId = id
                val prefs = getAppWidgetState(context, PreferencesGlanceStateDefinition, id)
                val json = prefs[WIDGET_CONFIG]
                val config = json?.let {
                    Json.decodeFromString<WidgetConfig>(it)
                } ?: WidgetConfig()
                _uiState.value = config
            } catch (e: Exception) {
                Log.w(TAG, "failed to load existing config for widget $appWidgetId", e)
                _loadFailed.value = true
            } finally {
                _isLoading.value = false
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

    /** Returns true if the config was actually persisted, false if there was nothing to save to. */
    suspend fun saveSettings(): Boolean {
        val id = glanceId ?: return false
        updateAppWidgetState(context, PreferencesGlanceStateDefinition, id) { prefs ->
            prefs.toMutablePreferences().apply {
                val config = uiState.value
                this[WIDGET_CONFIG] = Json.encodeToString(config)
            }
        }
        ThirukkuralWidget().update(context, id)
        return true
    }
}
