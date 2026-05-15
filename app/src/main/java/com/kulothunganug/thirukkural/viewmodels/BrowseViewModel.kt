package com.kulothunganug.thirukkural.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kulothunganug.thirukkural.models.ThirukkuralModel
import com.kulothunganug.thirukkural.repository.ThirukkuralRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn

data class BrowseUiState(
    val pals: List<String> = emptyList(),
    val iyals: List<String> = emptyList(),
    val adikarams: List<String> = emptyList(),
    val selectedPals: List<String> = emptyList(),
    val selectedIyals: List<String> = emptyList(),
    val selectedAdikarams: List<String> = emptyList(),
    val kurals: List<ThirukkuralModel> = emptyList()
)

class BrowseViewModel(private val repository: ThirukkuralRepository) : ViewModel() {

    private val _selectedPals = MutableStateFlow<List<String>>(emptyList())
    private val _selectedIyals = MutableStateFlow<List<String>>(emptyList())
    private val _selectedAdikarams = MutableStateFlow<List<String>>(emptyList())

    val pals: StateFlow<List<String>> = repository.getPals()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val iyals: StateFlow<List<String>> = _selectedPals
        .flatMapLatest { pals ->
            if (pals.isNotEmpty()) repository.getIyals(pals) else flowOf(emptyList())
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val adikarams: StateFlow<List<String>> = combine(_selectedPals, _selectedIyals) { pals, iyals ->
        pals to iyals
    }.flatMapLatest { (pals, iyals) ->
        if (pals.isNotEmpty() && iyals.isNotEmpty()) repository.getAdikarams(pals, iyals)
        else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val kurals: StateFlow<List<ThirukkuralModel>> = combine(
        _selectedPals, _selectedIyals, _selectedAdikarams
    ) { p, i, a -> Triple(p, i, a) }
        .flatMapLatest { (pals, iyals, adikarams) ->
            if (pals.isEmpty()) {
                flowOf(emptyList())
            } else {
                repository.getFilteredKurals(
                    pals, pals.isNotEmpty(),
                    iyals, iyals.isNotEmpty(),
                    adikarams, adikarams.isNotEmpty()
                )
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val uiState: StateFlow<BrowseUiState> = combine(
        pals,
        iyals,
        adikarams,
        combine(_selectedPals, _selectedIyals, _selectedAdikarams) { sp, si, sa -> Triple(sp, si, sa) },
        kurals
    ) { p, i, a, s, k ->
        BrowseUiState(
            pals = p,
            iyals = i,
            adikarams = a,
            selectedPals = s.first,
            selectedIyals = s.second,
            selectedAdikarams = s.third,
            kurals = k
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), BrowseUiState())

    fun togglePal(pal: String) {
        val current = _selectedPals.value.toMutableList()
        if (current.contains(pal)) {
            current.remove(pal)
        } else {
            current.add(pal)
        }
        _selectedPals.value = current
        // Reset dependent selections
        _selectedIyals.value = emptyList()
        _selectedAdikarams.value = emptyList()
    }

    fun toggleIyal(iyal: String) {
        val current = _selectedIyals.value.toMutableList()
        if (current.contains(iyal)) {
            current.remove(iyal)
        } else {
            current.add(iyal)
        }
        _selectedIyals.value = current
        _selectedAdikarams.value = emptyList()
    }

    fun toggleAdikaram(adikaram: String) {
        val current = _selectedAdikarams.value.toMutableList()
        if (current.contains(adikaram)) {
            current.remove(adikaram)
        } else {
            current.add(adikaram)
        }
        _selectedAdikarams.value = current
    }

    fun onClearPal() {
        _selectedPals.value = emptyList()
        _selectedIyals.value = emptyList()
        _selectedAdikarams.value = emptyList()
    }

    fun onClearIyal() {
        _selectedIyals.value = emptyList()
        _selectedAdikarams.value = emptyList()
    }

    fun onClearAdikaram() {
        _selectedAdikarams.value = emptyList()
    }
}
