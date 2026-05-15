package com.kulothunganug.thirukkural.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kulothunganug.thirukkural.models.ThirukkuralModel
import com.kulothunganug.thirukkural.repository.ThirukkuralRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

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

            val randomId = Random.nextInt(1, 1331)

            val kural = repository.getById(randomId)

            _uiState.value = HomeUiState(
                randomKural = kural
            )
        }
    }
}
