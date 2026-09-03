package com.kulothunganug.thirukkural.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kulothunganug.thirukkural.datastore.WidgetHintSettings
import com.kulothunganug.thirukkural.models.ThirukkuralModel
import com.kulothunganug.thirukkural.models.randomKuralId
import com.kulothunganug.thirukkural.repository.ThirukkuralRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val randomKural: ThirukkuralModel? = null,
    val showWidgetHint: Boolean = false,
)

class HomeViewModel(
    private val repository: ThirukkuralRepository,
    private val widgetHintSettings: WidgetHintSettings
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        loadRandomKural()
        checkWidgetHint()
    }

    private fun loadRandomKural() {
        viewModelScope.launch {
            // getById can legitimately return null if the id doesn't exist; retry once with a
            // fresh id instead of leaving the home screen stuck with no kural to show.
            val kural = repository.getById(randomKuralId()) ?: repository.getById(randomKuralId())

            _uiState.update { it.copy(randomKural = kural) }
        }
    }

    private fun checkWidgetHint() {
        viewModelScope.launch {
            val hasShown = widgetHintSettings.hasShownWidgetHintStream.first()
            _uiState.update { it.copy(showWidgetHint = !hasShown) }
        }
    }

    /** Called once the user has dismissed or acted on the widget hint, so it never shows again. */
    fun onWidgetHintShown() {
        _uiState.update { it.copy(showWidgetHint = false) }
        viewModelScope.launch {
            widgetHintSettings.setWidgetHintShown()
        }
    }
}
