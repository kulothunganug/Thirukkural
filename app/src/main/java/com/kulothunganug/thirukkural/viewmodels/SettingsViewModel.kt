package com.kulothunganug.thirukkural.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kulothunganug.thirukkural.datastore.AppTheme
import com.kulothunganug.thirukkural.datastore.ThemeSettings
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(private val themeSettings: ThemeSettings) : ViewModel() {
    val theme: StateFlow<AppTheme> = themeSettings.themeStream
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppTheme.SYSTEM
        )

    fun setTheme(theme: AppTheme) {
        viewModelScope.launch {
            themeSettings.setTheme(theme)
        }
    }
}
