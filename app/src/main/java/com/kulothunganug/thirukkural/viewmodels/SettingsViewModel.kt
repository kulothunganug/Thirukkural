package com.kulothunganug.thirukkural.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kulothunganug.thirukkural.datastore.SettingsDatastore
import com.kulothunganug.thirukkural.widget.ThirukkuralWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.updateAll
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class SettingsUiState(
    val bgColor: String = "#FFFFFF",
    val textColor: String = "#000000",
    val contentOrder: String = "PAAL,IYAL,ADHIGARAM,KURAL,TRANSLITERATION",
    val showPaal: Boolean = false,
    val paalSize: Int = 12,
    val paalAlign: String = "CENTER",
    val paalBold: Boolean = false,
    val showIyal: Boolean = false,
    val iyalSize: Int = 12,
    val iyalAlign: String = "CENTER",
    val iyalBold: Boolean = false,
    val showAdhigaram: Boolean = true,
    val adhigaramSize: Int = 15,
    val adhigaramAlign: String = "CENTER",
    val adhigaramBold: Boolean = true,
    val showKural: Boolean = true,
    val kuralSize: Int = 14,
    val kuralAlign: String = "CENTER",
    val kuralBold: Boolean = false,
    val showTranslit: Boolean = false,
    val translitSize: Int = 13,
    val translitAlign: String = "CENTER",
    val translitBold: Boolean = false
)

class SettingsViewModel(
    private val datastore: SettingsDatastore,
    private val context: Context
) : ViewModel() {

    private data class ElementState(val show: Boolean, val size: Int, val align: String, val bold: Boolean)
    private data class GlobalState(val bgColor: String, val textColor: String, val contentOrder: String)

    private val globalStateFlow = combine(
        datastore.widgetBgColor,
        datastore.widgetTextColor,
        datastore.widgetContentOrder
    ) { bg, text, order -> GlobalState(bg, text, order) }

    private val paalStateFlow = combine(
        datastore.showPaal, datastore.paalFontSize, datastore.paalAlignment, datastore.paalIsBold
    ) { show, size, align, bold -> ElementState(show, size, align, bold) }

    private val iyalStateFlow = combine(
        datastore.showIyal, datastore.iyalFontSize, datastore.iyalAlignment, datastore.iyalIsBold
    ) { show, size, align, bold -> ElementState(show, size, align, bold) }

    private val adhigaramStateFlow = combine(
        datastore.showAdhigaram, datastore.adhigaramFontSize, datastore.adhigaramAlignment, datastore.adhigaramIsBold
    ) { show, size, align, bold -> ElementState(show, size, align, bold) }

    private val kuralStateFlow = combine(
        datastore.showKural, datastore.kuralFontSize, datastore.kuralAlignment, datastore.kuralIsBold
    ) { show, size, align, bold -> ElementState(show, size, align, bold) }

    private val translitStateFlow = combine(
        datastore.showTransliteration, datastore.transliterationFontSize, datastore.transliterationAlignment, datastore.transliterationIsBold
    ) { show, size, align, bold -> ElementState(show, size, align, bold) }

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                globalStateFlow,
                paalStateFlow,
                iyalStateFlow,
                adhigaramStateFlow,
                kuralStateFlow,
                translitStateFlow
            ) { args ->
                val global = args[0] as GlobalState
                val paal = args[1] as ElementState
                val iyal = args[2] as ElementState
                val adhigaram = args[3] as ElementState
                val kural = args[4] as ElementState
                val translit = args[5] as ElementState

                SettingsUiState(
                    bgColor = global.bgColor,
                    textColor = global.textColor,
                    contentOrder = global.contentOrder,
                    showPaal = paal.show,
                    paalSize = paal.size,
                    paalAlign = paal.align,
                    paalBold = paal.bold,
                    showIyal = iyal.show,
                    iyalSize = iyal.size,
                    iyalAlign = iyal.align,
                    iyalBold = iyal.bold,
                    showAdhigaram = adhigaram.show,
                    adhigaramSize = adhigaram.size,
                    adhigaramAlign = adhigaram.align,
                    adhigaramBold = adhigaram.bold,
                    showKural = kural.show,
                    kuralSize = kural.size,
                    kuralAlign = kural.align,
                    kuralBold = kural.bold,
                    showTranslit = translit.show,
                    translitSize = translit.size,
                    translitAlign = translit.align,
                    translitBold = translit.bold
                )
            }.first().let {
                _uiState.value = it
            }
        }
    }

    fun updateBgColor(color: String) {
        _uiState.update { it.copy(bgColor = color) }
    }

    fun updateTextColor(color: String) {
        _uiState.update { it.copy(textColor = color) }
    }

    fun updateContentOrder(order: String) {
        _uiState.update { it.copy(contentOrder = order) }
    }

    fun updatePaalSettings(show: Boolean? = null, size: Int? = null, align: String? = null, bold: Boolean? = null) {
        _uiState.update { current ->
            current.copy(
                showPaal = show ?: current.showPaal,
                paalSize = size ?: current.paalSize,
                paalAlign = align ?: current.paalAlign,
                paalBold = bold ?: current.paalBold
            )
        }
    }

    fun updateIyalSettings(show: Boolean? = null, size: Int? = null, align: String? = null, bold: Boolean? = null) {
        _uiState.update { current ->
            current.copy(
                showIyal = show ?: current.showIyal,
                iyalSize = size ?: current.iyalSize,
                iyalAlign = align ?: current.iyalAlign,
                iyalBold = bold ?: current.iyalBold
            )
        }
    }

    fun updateAdhigaramSettings(show: Boolean? = null, size: Int? = null, align: String? = null, bold: Boolean? = null) {
        _uiState.update { current ->
            current.copy(
                showAdhigaram = show ?: current.showAdhigaram,
                adhigaramSize = size ?: current.adhigaramSize,
                adhigaramAlign = align ?: current.adhigaramAlign,
                adhigaramBold = bold ?: current.adhigaramBold
            )
        }
    }

    fun updateKuralSettings(show: Boolean? = null, size: Int? = null, align: String? = null, bold: Boolean? = null) {
        _uiState.update { current ->
            current.copy(
                showKural = show ?: current.showKural,
                kuralSize = size ?: current.kuralSize,
                kuralAlign = align ?: current.kuralAlign,
                kuralBold = bold ?: current.kuralBold
            )
        }
    }

    fun updateTranslitSettings(show: Boolean? = null, size: Int? = null, align: String? = null, bold: Boolean? = null) {
        _uiState.update { current ->
            current.copy(
                showTranslit = show ?: current.showTranslit,
                translitSize = size ?: current.translitSize,
                translitAlign = align ?: current.translitAlign,
                translitBold = bold ?: current.translitBold
            )
        }
    }

    fun saveSettings() = viewModelScope.launch {
        datastore.saveAll(uiState.value)
        ThirukkuralWidget().updateAll(context)
    }

}
