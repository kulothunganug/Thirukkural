package com.kulothunganug.thirukkural.widget

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.ActionParameters
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.kulothunganug.thirukkural.ThirukkuralDatabase
import com.kulothunganug.thirukkural.datastore.SettingsDatastore
import com.kulothunganug.thirukkural.viewmodels.SettingsUiState
import kotlinx.coroutines.flow.first
import kotlin.random.Random
import androidx.core.graphics.toColorInt

object ThirukkuralWidgetKeys {
    val KURAL_TEXT = stringPreferencesKey("kural_text")
    val KURAL_ID = intPreferencesKey("kural_id")
    val PAAL = stringPreferencesKey("paal")
    val IYAL = stringPreferencesKey("iyal")
    val ADHIGARAM = stringPreferencesKey("adhigaram")
    val TRANSLITERATION = stringPreferencesKey("transliteration")
}

class ThirukkuralWidget : GlanceAppWidget() {
    override val stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val datastore = SettingsDatastore(context)
        
        // Read all settings from SettingsDatastore
        val settings = SettingsUiState(
            bgColor = datastore.widgetBgColor.first(),
            textColor = datastore.widgetTextColor.first(),
            contentOrder = datastore.widgetContentOrder.first(),
            showPaal = datastore.showPaal.first(),
            paalSize = datastore.paalFontSize.first(),
            paalAlign = datastore.paalAlignment.first(),
            paalBold = datastore.paalIsBold.first(),
            showIyal = datastore.showIyal.first(),
            iyalSize = datastore.iyalFontSize.first(),
            iyalAlign = datastore.iyalAlignment.first(),
            iyalBold = datastore.iyalIsBold.first(),
            showAdhigaram = datastore.showAdhigaram.first(),
            adhigaramSize = datastore.adhigaramFontSize.first(),
            adhigaramAlign = datastore.adhigaramAlignment.first(),
            adhigaramBold = datastore.adhigaramIsBold.first(),
            showKural = datastore.showKural.first(),
            kuralSize = datastore.kuralFontSize.first(),
            kuralAlign = datastore.kuralAlignment.first(),
            kuralBold = datastore.kuralIsBold.first(),
            showTranslit = datastore.showTransliteration.first(),
            translitSize = datastore.transliterationFontSize.first(),
            translitAlign = datastore.transliterationAlignment.first(),
            translitBold = datastore.transliterationIsBold.first()
        )

        provideContent {
            GlanceTheme {
                val prefs = currentState<Preferences>()
                val kuralText = prefs[ThirukkuralWidgetKeys.KURAL_TEXT] ?: "Tap to load Kural"
                val kuralId = prefs[ThirukkuralWidgetKeys.KURAL_ID] ?: 0
                val paal = prefs[ThirukkuralWidgetKeys.PAAL] ?: ""
                val iyal = prefs[ThirukkuralWidgetKeys.IYAL] ?: ""
                val adhigaram = prefs[ThirukkuralWidgetKeys.ADHIGARAM] ?: ""
                val transliteration = prefs[ThirukkuralWidgetKeys.TRANSLITERATION] ?: ""

                WidgetContent(
                    kuralId, paal, iyal, adhigaram, kuralText, transliteration,
                    settings
                )
            }
        }
    }

    @Composable
    private fun WidgetContent(
        id: Int, paal: String, iyal: String, adhigaram: String, text: String, transliteration: String,
        settings: SettingsUiState
    ) {
        val backgroundColor = Color(settings.bgColor.toColorInt())
        val contentColor = Color(settings.textColor.toColorInt())

        Box(
            modifier = GlanceModifier.fillMaxSize().background(backgroundColor).padding(12.dp)
                .clickable(actionRunCallback<RefreshKuralAction>()),
            contentAlignment = Alignment.Center
        ) {
            Column(modifier = GlanceModifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalAlignment = Alignment.CenterVertically) {
                settings.contentOrder.split(",").forEach { item ->
                    when (item) {
                        "PAAL" -> if (settings.showPaal && paal.isNotEmpty()) {
                            RenderText(paal, contentColor, settings.paalSize, settings.paalAlign, settings.paalBold)
                        }
                        "IYAL" -> if (settings.showIyal && iyal.isNotEmpty()) {
                            RenderText(iyal, contentColor, settings.iyalSize, settings.iyalAlign, settings.iyalBold)
                        }
                        "ADHIGARAM" -> if (settings.showAdhigaram && id != 0) {
                            RenderText("$id - $adhigaram", contentColor, settings.adhigaramSize, settings.adhigaramAlign, settings.adhigaramBold)
                        }
                        "KURAL" -> if (settings.showKural && text.isNotEmpty()) {
                            RenderText(text.replace("<br />", "\n"), contentColor, settings.kuralSize, settings.kuralAlign, settings.kuralBold)
                        }
                        "TRANSLITERATION" -> if (settings.showTranslit && transliteration.isNotEmpty()) {
                            RenderText(transliteration, contentColor, settings.translitSize, settings.translitAlign, settings.translitBold)
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("RestrictedApi")
    @Composable
    private fun RenderText(text: String, color: Color, fontSize: Int, alignment: String, isBold: Boolean) {
        Text(
            text = text,
            style = TextStyle(
                color = ColorProvider(color),
                fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
                fontSize = fontSize.sp,
                textAlign = when (alignment) {
                    "LEFT" -> TextAlign.Start
                    "RIGHT" -> TextAlign.End
                    else -> TextAlign.Center
                }
            ),
            modifier = GlanceModifier.fillMaxWidth().padding(top = 2.dp)
        )
    }
}

class RefreshKuralAction : ActionCallback {
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        val db = ThirukkuralDatabase.get(context)
        val randomId = Random.nextInt(1, 1331)
        val kural = db.dao().getById(randomId)

        updateAppWidgetState(context, PreferencesGlanceStateDefinition, glanceId) { prefs ->
            prefs.toMutablePreferences().apply {
                this[ThirukkuralWidgetKeys.KURAL_TEXT] = kural.kural
                this[ThirukkuralWidgetKeys.KURAL_ID] = kural.id
                this[ThirukkuralWidgetKeys.PAAL] = kural.paal
                this[ThirukkuralWidgetKeys.IYAL] = kural.iyal
                this[ThirukkuralWidgetKeys.ADHIGARAM] = kural.adhigaram
                this[ThirukkuralWidgetKeys.TRANSLITERATION] = kural.transliteration
            }
        }
        ThirukkuralWidget().update(context, glanceId)
    }
}

class ThirukkuralWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = ThirukkuralWidget()
}
