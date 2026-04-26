package com.kulothunganug.thirukkural.widget

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.core.net.toUri
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
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
import com.kulothunganug.thirukkural.MainActivity
import com.kulothunganug.thirukkural.R
import com.kulothunganug.thirukkural.ThirukkuralDatabase
import com.kulothunganug.thirukkural.models.ThirukkuralModel
import com.kulothunganug.thirukkural.formatTransliteration
import com.kulothunganug.thirukkural.viewmodels.SettingsUiState
import kotlin.random.Random

object ThirukkuralWidgetKeys {
    val KURAL_ID = intPreferencesKey("kural_id")
    val BG_COLOR = stringPreferencesKey("widget_bg_color")
    val TEXT_COLOR = stringPreferencesKey("widget_text_color")
    val CONTENT_ORDER = stringPreferencesKey("widget_content_order")

    val SHOW_PAAL = booleanPreferencesKey("show_paal")
    val PAAL_SIZE = intPreferencesKey("paal_size")
    val PAAL_ALIGN = stringPreferencesKey("paal_align")
    val PAAL_BOLD = booleanPreferencesKey("paal_bold")

    val SHOW_IYAL = booleanPreferencesKey("show_iyal")
    val IYAL_SIZE = intPreferencesKey("iyal_size")
    val IYAL_ALIGN = stringPreferencesKey("iyal_align")
    val IYAL_BOLD = booleanPreferencesKey("iyal_bold")

    val SHOW_ADHIGARAM = booleanPreferencesKey("show_adhigaram")
    val ADHIGARAM_SIZE = intPreferencesKey("adhigaram_size")
    val ADHIGARAM_ALIGN = stringPreferencesKey("adhigaram_align")
    val ADHIGARAM_BOLD = booleanPreferencesKey("adhigaram_bold")

    val SHOW_KURAL = booleanPreferencesKey("show_kural")
    val KURAL_SIZE = intPreferencesKey("kural_size")
    val KURAL_BOLD = booleanPreferencesKey("kural_bold")

    const val DEFAULT_BG_COLOR = "#FFFFFF"
    const val DEFAULT_TEXT_COLOR = "#000000"
    const val DEFAULT_CONTENT_ORDER = "PAAL,IYAL,ADHIGARAM,KURAL,TRANSLITERATION"
}


class ThirukkuralWidget : GlanceAppWidget() {
    override val stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition

    override suspend fun providePreview(context: Context, widgetCategory: Int) {
        provideContent { GlanceContent(context) }
    }

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent { GlanceContent(context) }
    }

    @Composable
    private fun GlanceContent(context: Context) {
        GlanceTheme {
            val prefs = currentState<Preferences>()
            val kuralId = prefs[ThirukkuralWidgetKeys.KURAL_ID] ?: 0

            val settings = SettingsUiState(
                bgColor = prefs[ThirukkuralWidgetKeys.BG_COLOR] ?: ThirukkuralWidgetKeys.DEFAULT_BG_COLOR,
                textColor = prefs[ThirukkuralWidgetKeys.TEXT_COLOR] ?: ThirukkuralWidgetKeys.DEFAULT_TEXT_COLOR,
                contentOrder = prefs[ThirukkuralWidgetKeys.CONTENT_ORDER] ?: ThirukkuralWidgetKeys.DEFAULT_CONTENT_ORDER,
                showPaal = prefs[ThirukkuralWidgetKeys.SHOW_PAAL] ?: false,
                paalSize = prefs[ThirukkuralWidgetKeys.PAAL_SIZE] ?: 12,
                paalAlign = prefs[ThirukkuralWidgetKeys.PAAL_ALIGN] ?: "CENTER",
                paalBold = prefs[ThirukkuralWidgetKeys.PAAL_BOLD] ?: false,
                showIyal = prefs[ThirukkuralWidgetKeys.SHOW_IYAL] ?: false,
                iyalSize = prefs[ThirukkuralWidgetKeys.IYAL_SIZE] ?: 12,
                iyalAlign = prefs[ThirukkuralWidgetKeys.IYAL_ALIGN] ?: "CENTER",
                iyalBold = prefs[ThirukkuralWidgetKeys.IYAL_BOLD] ?: false,
                showAdhigaram = prefs[ThirukkuralWidgetKeys.SHOW_ADHIGARAM] ?: true,
                adhigaramSize = prefs[ThirukkuralWidgetKeys.ADHIGARAM_SIZE] ?: 15,
                adhigaramAlign = prefs[ThirukkuralWidgetKeys.ADHIGARAM_ALIGN] ?: "CENTER",
                adhigaramBold = prefs[ThirukkuralWidgetKeys.ADHIGARAM_BOLD] ?: true,
                showKural = prefs[ThirukkuralWidgetKeys.SHOW_KURAL] ?: true,
                kuralSize = prefs[ThirukkuralWidgetKeys.KURAL_SIZE] ?: 14,
                kuralBold = prefs[ThirukkuralWidgetKeys.KURAL_BOLD] ?: false,
            )

            val kural by produceState<ThirukkuralModel?>(initialValue = null, kuralId) {
                value = if (kuralId != 0) {
                    ThirukkuralDatabase.get(context).dao().getById(kuralId)
                } else {
                    null
                }
            }

            WidgetContent(
                kuralId,
                kural?.paal ?: "",
                kural?.iyal ?: "",
                kural?.adhigaram ?: "",
                kural?.kural ?: "Tap to load Kural",
                settings,
            )
        }
    }


    @Composable
    private fun WidgetContent(
        id: Int,
        paal: String,
        iyal: String,
        adhigaram: String,
        text: String,
        settings: SettingsUiState,
    ) {
        val backgroundColor = Color(settings.bgColor.toColorInt())
        val contentColor = Color(settings.textColor.toColorInt())

        Box(
            modifier = GlanceModifier.fillMaxSize().background(backgroundColor).padding(12.dp)
                .clickable(
                    actionRunCallback<OpenKuralAction>(
                        parameters = actionParametersOf(
                            OpenKuralAction.kuralIdKey to id
                        )
                    )
                ),
            contentAlignment = Alignment.BottomEnd
        ) {
            Image(
                provider = ImageProvider(R.drawable.refresh_24px),
                contentDescription = "Refresh",
                modifier = GlanceModifier.cornerRadius(12.dp).padding(6.dp)
                    .clickable(actionRunCallback<RefreshKuralAction>())
            )
            Column(
                modifier = GlanceModifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalAlignment = Alignment.CenterVertically
            ) {
                settings.contentOrder.split(",").forEach { item ->
                    when (item) {
                        "PAAL" -> if (settings.showPaal && paal.isNotEmpty()) {
                            RenderText(
                                paal,
                                contentColor,
                                settings.paalSize,
                                settings.paalAlign,
                                settings.paalBold
                            )
                        }

                        "IYAL" -> if (settings.showIyal && iyal.isNotEmpty()) {
                            RenderText(
                                iyal,
                                contentColor,
                                settings.iyalSize,
                                settings.iyalAlign,
                                settings.iyalBold
                            )
                        }

                        "ADHIGARAM" -> if (settings.showAdhigaram && id != 0) {
                            RenderText(
                                adhigaram,
                                contentColor,
                                settings.adhigaramSize,
                                settings.adhigaramAlign,
                                settings.adhigaramBold
                            )
                        }

                        "KURAL" -> if (settings.showKural && text.isNotEmpty()) {
                            RenderText(
                                text.replace("<br />", "\n"),
                                contentColor,
                                settings.kuralSize,
                                "LEFT",
                                settings.kuralBold
                            )
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("RestrictedApi")
    @Composable
    private fun RenderText(
        text: String,
        color: Color,
        fontSize: Int,
        alignment: String,
        isBold: Boolean
    ) {
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

class OpenKuralAction : ActionCallback {

    companion object {
        val kuralIdKey = ActionParameters.Key<Int>("id")

    }


    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val kuralId: Int = parameters[kuralIdKey] ?: return
        val uri = "thirukkural_app://kural/$kuralId".toUri()

        val intent = Intent(Intent.ACTION_VIEW, uri, context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        context.startActivity(intent)
    }
}

class RefreshKuralAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val randomId = Random.nextInt(1, 1331)

        updateAppWidgetState(context, PreferencesGlanceStateDefinition, glanceId) { prefs ->
            prefs.toMutablePreferences().apply {
                this[ThirukkuralWidgetKeys.KURAL_ID] = randomId
            }
        }
        ThirukkuralWidget().update(context, glanceId)
    }
}

class ThirukkuralWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = ThirukkuralWidget()
}
