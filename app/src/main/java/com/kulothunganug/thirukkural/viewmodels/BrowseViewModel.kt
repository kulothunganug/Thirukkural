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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class BrowseUiState(
    val pals: List<String> = emptyList(),
    val iyals: List<String> = emptyList(),
    val adikarams: List<String> = emptyList(),
    val selectedPals: List<String> = emptyList(),
    val selectedIyals: List<String> = emptyList(),
    val selectedAdikarams: List<String> = emptyList(),
    val searchQuery: String = "",
    val kurals: List<ThirukkuralModel> = emptyList()
)

/** Bundles the three selection flows plus the free-text search query for a single combine step. */
private data class Selection(
    val pals: List<String>,
    val iyals: List<String>,
    val adikarams: List<String>,
    val query: String
)

class BrowseViewModel(
    private val repository: ThirukkuralRepository,
) : ViewModel() {


    private val _selectedPals = MutableStateFlow<List<String>>(emptyList())
    private val _selectedIyals = MutableStateFlow<List<String>>(emptyList())
    private val _selectedAdikarams = MutableStateFlow<List<String>>(emptyList())
    private val _searchQuery = MutableStateFlow("")

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

    // Pal/Iyal/Adhigaram-filtered Kurals, before the free-text search is applied on top.
    @OptIn(ExperimentalCoroutinesApi::class)
    private val filteredKurals: StateFlow<List<ThirukkuralModel>> = combine(
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

    val kurals: StateFlow<List<ThirukkuralModel>> = combine(
        filteredKurals, _searchQuery
    ) { list, query ->
        val trimmed = query.trim()
        if (trimmed.isEmpty()) {
            list
        } else {
            list.filter { kural ->
                kural.id.toString() == trimmed ||
                    kural.kuralTa.contains(trimmed, ignoreCase = true) ||
                    kural.kuralTl.contains(trimmed, ignoreCase = true) ||
                    kural.translationEn.contains(trimmed, ignoreCase = true) ||
                    kural.couplet.contains(trimmed, ignoreCase = true)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val uiState: StateFlow<BrowseUiState> = combine(
        pals,
        iyals,
        adikarams,
        combine(_selectedPals, _selectedIyals, _selectedAdikarams, _searchQuery) { sp, si, sa, q ->
            Selection(sp, si, sa, q)
        },
        kurals
    ) { p, i, a, selection, k ->
        BrowseUiState(
            pals = p,
            iyals = i,
            adikarams = a,
            selectedPals = selection.pals,
            selectedIyals = selection.iyals,
            selectedAdikarams = selection.adikarams,
            searchQuery = selection.query,
            kurals = k
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), BrowseUiState())

    init {
        // Default to browsing every Kural rather than making the user pick filters first —
        // selecting everything at every level keeps the filter dialog's own "nothing selected
        // means no restriction" semantics honest (it visibly shows everything as selected,
        // instead of looking like nothing was chosen while secretly matching everything).
        viewModelScope.launch {
            val allPals = repository.getPals().first()
            _selectedPals.value = allPals
            val allIyals = repository.getIyals(allPals).first()
            _selectedIyals.value = allIyals
            val allAdikarams = repository.getAdikarams(allPals, allIyals).first()
            _selectedAdikarams.value = allAdikarams
        }
    }

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

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }
}
