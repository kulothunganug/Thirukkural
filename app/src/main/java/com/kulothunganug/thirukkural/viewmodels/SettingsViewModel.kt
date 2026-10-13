package com.kulothunganug.thirukkural.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kulothunganug.thirukkural.datastore.AppTheme
import com.kulothunganug.thirukkural.datastore.LanguageSettings
import com.kulothunganug.thirukkural.datastore.SecondaryScript
import com.kulothunganug.thirukkural.datastore.ThemeSettings
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val themeSettings: ThemeSettings,
    private val languageSettings: LanguageSettings
) : ViewModel() {
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

    val secondaryScript: StateFlow<SecondaryScript> = languageSettings.secondaryStream
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SecondaryScript.TRANSLITERATION
        )

    fun setSecondaryScript(script: SecondaryScript) {
        viewModelScope.launch {
            languageSettings.setSecondary(script)
        }
    }
}
