package com.kulothunganug.thirukkural.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kulothunganug.thirukkural.datastore.FavouritesSettings
import com.kulothunganug.thirukkural.models.ThirukkuralModel
import com.kulothunganug.thirukkural.repository.ThirukkuralRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class FavouritesUiState(
    val kurals: List<ThirukkuralModel> = emptyList(),
    val isLoading: Boolean = true
)

class FavouritesViewModel(
    private val repository: ThirukkuralRepository,
    private val favouritesSettings: FavouritesSettings
) : ViewModel() {

    val uiState: StateFlow<FavouritesUiState> = favouritesSettings.favouriteIdsStream
        .map { ids ->
            // A single batched query instead of one getById per id — matters once someone has
            // hundreds of favourites, since this re-runs on every add/remove.
            val kurals = if (ids.isEmpty()) emptyList() else repository.getByIds(ids.toList())
            FavouritesUiState(kurals = kurals, isLoading = false)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), FavouritesUiState())

    fun removeFavourite(kuralId: Int) {
        viewModelScope.launch {
            favouritesSettings.toggleFavourite(kuralId)
        }
    }
}
