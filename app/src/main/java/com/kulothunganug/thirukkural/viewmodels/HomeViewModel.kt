package com.kulothunganug.thirukkural.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kulothunganug.thirukkural.models.ThirukkuralModel
import com.kulothunganug.thirukkural.repository.ThirukkuralRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class HomeUiState(
    val adhigaramId: Int = 1,
    val kurals: List<ThirukkuralModel> = emptyList(),
    val isLoading: Boolean = false
)

class HomeViewModel(
    private val repository: ThirukkuralRepository
) : ViewModel() {

    private val _adhigaramId = MutableStateFlow(1)

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState = _adhigaramId
        .flatMapLatest { id ->
            repository.getByAdhigaramId(id).map { kurals ->
                HomeUiState(adhigaramId = id, kurals = kurals, isLoading = false)
            }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            HomeUiState(isLoading = true)
        )

    fun updateAdhigaram(id: Int) {
        if (id in 1..133) {
            _adhigaramId.value = id
        }
    }
}
