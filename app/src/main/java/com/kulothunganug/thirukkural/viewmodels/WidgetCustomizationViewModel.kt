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
import com.kulothunganug.thirukkural.datastore.FavouritesSettings
import com.kulothunganug.thirukkural.repository.ThirukkuralRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import com.kulothunganug.thirukkural.widget.ContentType
import com.kulothunganug.thirukkural.widget.MAX_AUTO_REFRESH_INTERVAL_MINUTES
import com.kulothunganug.thirukkural.widget.MIN_AUTO_REFRESH_INTERVAL_MINUTES
import com.kulothunganug.thirukkural.widget.RefreshSource
import com.kulothunganug.thirukkural.widget.SectionConfig
import com.kulothunganug.thirukkural.widget.ThirukkuralWidget
import com.kulothunganug.thirukkural.widget.ThirukkuralWidgetKeys
import com.kulothunganug.thirukkural.widget.WIDGET_CONFIG
import com.kulothunganug.thirukkural.widget.WidgetConfig
import com.kulothunganug.thirukkural.widget.WidgetRefreshScheduler
import com.kulothunganug.thirukkural.widget.WidgetTextAlign
import com.kulothunganug.thirukkural.widget.pickKuralId
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

private const val TAG = "WidgetCustomizationVM"

class WidgetCustomizationViewModel(
    private val context: Context,
    private val appWidgetId: Int,
    private val repository: ThirukkuralRepository,
    private val favouritesSettings: FavouritesSettings
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

    val pals: StateFlow<List<String>> = repository.getPals()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Cascading options for the category refresh-source filter — mirrors BrowseViewModel's
    // Paal → Iyal → Adhigaram narrowing, but keyed off the selections already living in
    // _uiState.refreshCategory* rather than a separate set of selection flows.
    @OptIn(ExperimentalCoroutinesApi::class)
    val categoryIyals: StateFlow<List<String>> = _uiState
        .map { it.refreshCategoryPals }
        .distinctUntilChanged()
        .flatMapLatest { pals ->
            if (pals.isNotEmpty()) repository.getIyals(pals) else flowOf(emptyList())
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val categoryAdikarams: StateFlow<List<String>> = _uiState
        .map { it.refreshCategoryPals to it.refreshCategoryIyals }
        .distinctUntilChanged()
        .flatMapLatest { (pals, iyals) ->
            if (pals.isNotEmpty() && iyals.isNotEmpty()) repository.getAdikarams(pals, iyals)
            else flowOf(emptyList())
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

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

    fun updateAutoRefreshEnabled(enabled: Boolean) {
        _uiState.update { it.copy(autoRefreshEnabled = enabled) }
    }

    fun updateAutoRefreshInterval(minutes: Int) {
        _uiState.update {
            it.copy(
                autoRefreshIntervalMinutes = minutes.coerceIn(
                    MIN_AUTO_REFRESH_INTERVAL_MINUTES,
                    MAX_AUTO_REFRESH_INTERVAL_MINUTES
                )
            )
        }
    }

    fun updateRefreshSource(source: RefreshSource) {
        _uiState.update { it.copy(refreshSource = source) }
    }

    fun toggleRefreshCategoryPal(pal: String) {
        _uiState.update { current ->
            val updated = if (current.refreshCategoryPals.contains(pal)) {
                current.refreshCategoryPals - pal
            } else {
                current.refreshCategoryPals + pal
            }
            // Reset dependent selections, same as BrowseViewModel.togglePal.
            current.copy(
                refreshCategoryPals = updated,
                refreshCategoryIyals = emptyList(),
                refreshCategoryAdikarams = emptyList()
            )
        }
    }

    fun toggleRefreshCategoryIyal(iyal: String) {
        _uiState.update { current ->
            val updated = if (current.refreshCategoryIyals.contains(iyal)) {
                current.refreshCategoryIyals - iyal
            } else {
                current.refreshCategoryIyals + iyal
            }
            current.copy(refreshCategoryIyals = updated, refreshCategoryAdikarams = emptyList())
        }
    }

    fun toggleRefreshCategoryAdikaram(adikaram: String) {
        _uiState.update { current ->
            val updated = if (current.refreshCategoryAdikarams.contains(adikaram)) {
                current.refreshCategoryAdikarams - adikaram
            } else {
                current.refreshCategoryAdikarams + adikaram
            }
            current.copy(refreshCategoryAdikarams = updated)
        }
    }

    /** Returns true if the config was actually persisted, false if there was nothing to save to. */
    suspend fun saveSettings(): Boolean {
        val id = glanceId ?: return false
        val config = uiState.value
        updateAppWidgetState(context, PreferencesGlanceStateDefinition, id) { prefs ->
            // refresh kural widget on saving
            val currentId = prefs[ThirukkuralWidgetKeys.KURAL_ID]
            val newId = pickKuralId(config, repository, favouritesSettings, currentId)
            prefs.toMutablePreferences().apply {
                this[WIDGET_CONFIG] = Json.encodeToString(config)
                this[ThirukkuralWidgetKeys.KURAL_ID] = newId
            }
        }
        if (config.autoRefreshEnabled) {
            WidgetRefreshScheduler.schedule(context, appWidgetId, config.autoRefreshIntervalMinutes)
        } else {
            WidgetRefreshScheduler.cancel(context, appWidgetId)
        }
        ThirukkuralWidget().update(context, id)
        return true
    }
}
