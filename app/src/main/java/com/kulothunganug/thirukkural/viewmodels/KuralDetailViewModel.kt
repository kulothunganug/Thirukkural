package com.kulothunganug.thirukkural.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kulothunganug.thirukkural.models.ThirukkuralModel
import com.kulothunganug.thirukkural.repository.ThirukkuralRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class KuralDetailViewModel(
    private val repository: ThirukkuralRepository
) : ViewModel() {

    private val _kural = MutableStateFlow<ThirukkuralModel?>(null)
    val kural: StateFlow<ThirukkuralModel?> = _kural

    fun loadKural(id: Int) {
        viewModelScope.launch {
            _kural.value = repository.getById(id)
        }
    }
}