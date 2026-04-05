package com.kulothunganug.thirukkural.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kulothunganug.thirukkural.datastore.SettingsDatastore
import com.kulothunganug.thirukkural.widget.ThirukkuralWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
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

    val uiState: StateFlow<SettingsUiState> = combine(
        globalStateFlow,
        paalStateFlow,
        iyalStateFlow,
        adhigaramStateFlow,
        kuralStateFlow,
        translitStateFlow
    ) { args: Array<Any?> ->
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
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsUiState()
    )

    fun updateBgColor(color: String) = viewModelScope.launch {
        datastore.saveWidgetBgColor(color)
        syncWidget()
    }

    fun updateTextColor(color: String) = viewModelScope.launch {
        datastore.saveWidgetTextColor(color)
        syncWidget()
    }

    fun updateContentOrder(order: String) = viewModelScope.launch {
        datastore.saveWidgetContentOrder(order)
        syncWidget()
    }

    fun updatePaalSettings(show: Boolean? = null, size: Int? = null, align: String? = null, bold: Boolean? = null) = viewModelScope.launch {
        show?.let { datastore.saveShowPaal(it) }
        size?.let { datastore.savePaalFontSize(it) }
        align?.let { datastore.savePaalAlignment(it) }
        bold?.let { datastore.savePaalIsBold(it) }
        syncWidget()
    }

    fun updateIyalSettings(show: Boolean? = null, size: Int? = null, align: String? = null, bold: Boolean? = null) = viewModelScope.launch {
        show?.let { datastore.saveShowIyal(it) }
        size?.let { datastore.saveIyalFontSize(it) }
        align?.let { datastore.saveIyalAlignment(it) }
        bold?.let { datastore.saveIyalIsBold(it) }
        syncWidget()
    }

    fun updateAdhigaramSettings(show: Boolean? = null, size: Int? = null, align: String? = null, bold: Boolean? = null) = viewModelScope.launch {
        show?.let { datastore.saveShowAdhigaram(it) }
        size?.let { datastore.saveAdhigaramFontSize(it) }
        align?.let { datastore.saveAdhigaramAlignment(it) }
        bold?.let { datastore.saveAdhigaramIsBold(it) }
        syncWidget()
    }

    fun updateKuralSettings(show: Boolean? = null, size: Int? = null, align: String? = null, bold: Boolean? = null) = viewModelScope.launch {
        show?.let { datastore.saveShowKural(it) }
        size?.let { datastore.saveKuralFontSize(it) }
        align?.let { datastore.saveKuralAlignment(it) }
        bold?.let { datastore.saveKuralIsBold(it) }
        syncWidget()
    }

    fun updateTranslitSettings(show: Boolean? = null, size: Int? = null, align: String? = null, bold: Boolean? = null) = viewModelScope.launch {
        show?.let { datastore.saveShowTransliteration(it) }
        size?.let { datastore.saveTransliterationFontSize(it) }
        align?.let { datastore.saveTransliterationAlignment(it) }
        bold?.let { datastore.saveTransliterationIsBold(it) }
        syncWidget()
    }

    private suspend fun syncWidget() {
        val manager = GlanceAppWidgetManager(context)
        val ids = manager.getGlanceIds(ThirukkuralWidget::class.java)
        ids.forEach { id ->
            ThirukkuralWidget().update(context, id)
        }
    }
}
