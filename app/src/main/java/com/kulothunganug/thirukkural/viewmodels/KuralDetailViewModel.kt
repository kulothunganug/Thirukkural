package com.kulothunganug.thirukkural.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kulothunganug.thirukkural.datastore.FavouritesSettings
import com.kulothunganug.thirukkural.models.ThirukkuralModel
import com.kulothunganug.thirukkural.repository.ThirukkuralRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

sealed interface KuralDetailUiState {
    data object Loading : KuralDetailUiState
    data class Found(val kural: ThirukkuralModel, val isFavourite: Boolean = false) :
        KuralDetailUiState

    data object NotFound : KuralDetailUiState
}

class KuralDetailViewModel(
    private val repository: ThirukkuralRepository,
    private val favouritesSettings: FavouritesSettings
) : ViewModel() {

    private val _uiState = MutableStateFlow<KuralDetailUiState>(KuralDetailUiState.Loading)
    val uiState: StateFlow<KuralDetailUiState> = _uiState

    // Tracks the favourite-status observer for the currently loaded kural so it can be
    // cancelled when a new kural is loaded (e.g. via a fresh deep link) instead of leaking or
    // overwriting state for the wrong id.
    private var favouriteObserverJob: Job? = null

    fun loadKural(id: Int) {
        favouriteObserverJob?.cancel()
        viewModelScope.launch {
            _uiState.value = KuralDetailUiState.Loading
            val kural = repository.getById(id)
            if (kural == null) {
                _uiState.value = KuralDetailUiState.NotFound
                return@launch
            }
            _uiState.value = KuralDetailUiState.Found(kural)
            favouriteObserverJob = favouritesSettings.isFavourite(id)
                .onEach { isFavourite ->
                    val current = _uiState.value
                    if (current is KuralDetailUiState.Found) {
                        _uiState.value = current.copy(isFavourite = isFavourite)
                    }
                }
                .launchIn(viewModelScope)
        }
    }

    fun toggleFavourite() {
        val current = _uiState.value
        if (current is KuralDetailUiState.Found) {
            viewModelScope.launch {
                favouritesSettings.toggleFavourite(current.kural.id)
            }
        }
    }
}
