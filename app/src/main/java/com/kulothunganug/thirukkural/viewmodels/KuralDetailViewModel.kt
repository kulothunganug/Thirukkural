package com.kulothunganug.thirukkural.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kulothunganug.thirukkural.models.ThirukkuralModel
import com.kulothunganug.thirukkural.repository.ThirukkuralRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed interface KuralDetailUiState {
    data object Loading : KuralDetailUiState
    data class Found(val kural: ThirukkuralModel) : KuralDetailUiState
    data object NotFound : KuralDetailUiState
}

class KuralDetailViewModel(
    private val repository: ThirukkuralRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<KuralDetailUiState>(KuralDetailUiState.Loading)
    val uiState: StateFlow<KuralDetailUiState> = _uiState

    fun loadKural(id: Int) {
        viewModelScope.launch {
            _uiState.value = KuralDetailUiState.Loading
            val kural = repository.getById(id)
            _uiState.value = kural?.let { KuralDetailUiState.Found(it) }
                ?: KuralDetailUiState.NotFound
        }
    }
}
