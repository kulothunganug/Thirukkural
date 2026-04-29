package com.kulothunganug.thirukkural.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kulothunganug.thirukkural.models.ThirukkuralModel
import com.kulothunganug.thirukkural.repository.ThirukkuralRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn

data class HomeUiState(
    val kurals: List<ThirukkuralModel> = emptyList(),
    val isLoading: Boolean = false
)

class HomeViewModel(
    private val repository: ThirukkuralRepository
) : ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState = repository.getAll()
        .map { kurals ->
            HomeUiState(kurals = kurals, isLoading = false)
        }
        .onStart {
            emit(HomeUiState(isLoading = true))
        }
        .catch {
            emit(HomeUiState(isLoading = false))
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            HomeUiState(isLoading = true)
        )
}
