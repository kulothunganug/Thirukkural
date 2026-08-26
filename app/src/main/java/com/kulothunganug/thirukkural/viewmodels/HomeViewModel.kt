package com.kulothunganug.thirukkural.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kulothunganug.thirukkural.models.ThirukkuralModel
import com.kulothunganug.thirukkural.models.randomKuralId
import com.kulothunganug.thirukkural.repository.ThirukkuralRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val randomKural: ThirukkuralModel? = null,
)

class HomeViewModel(
    private val repository: ThirukkuralRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        loadRandomKural()
    }

    private fun loadRandomKural() {
        viewModelScope.launch {
            // getById can legitimately return null if the id doesn't exist; retry once with a
            // fresh id instead of leaving the home screen stuck with no kural to show.
            val kural = repository.getById(randomKuralId()) ?: repository.getById(randomKuralId())

            _uiState.value = HomeUiState(
                randomKural = kural
            )
        }
    }
}
