package com.kulothunganug.thirukkural.widget

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
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
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.random.Random

@Serializable
enum class ContentType { Paal, Iyal, Adhigaram, Kural, Transliteration }

@Serializable
enum class WidgetTextAlign { Start, Center, End }

@Serializable
data class WidgetConfig(
    val bgColor: String = "#FFFFFF",
    val textColor: String = "#000000",
    val contentOrder: List<SectionConfig> = listOf(
        SectionConfig(type = ContentType.Paal, show = false),
        SectionConfig(type = ContentType.Iyal, show = false),
        SectionConfig(type = ContentType.Adhigaram, show = true, size = 15, bold = true),
        SectionConfig(type = ContentType.Kural, show = true, size = 14),
        SectionConfig(type = ContentType.Transliteration, show = false)
    )
)

@Serializable
data class SectionConfig(
    val type: ContentType,
    val show: Boolean = false,
    val size: Int = 12,
    val align: WidgetTextAlign = WidgetTextAlign.Center,
    val bold: Boolean = false
)

val CONFIG = stringPreferencesKey("widget_config")

object ThirukkuralWidgetKeys {
    val KURAL_ID = intPreferencesKey("kural_id")
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
            val json = prefs[CONFIG]
            val config = json?.let {
                Json.decodeFromString<WidgetConfig>(it)
            } ?: WidgetConfig()


            val kural by produceState<ThirukkuralModel?>(initialValue = null, kuralId) {
                value = if (kuralId != 0) {
                    ThirukkuralDatabase.get(context).dao().getById(kuralId)
                } else {
                    null
                }
            }

            WidgetContent(
                kuralId,
                kural,
                config,
            )
        }
    }


    @Composable
    private fun WidgetContent(
        id: Int,
        kural: ThirukkuralModel?,
        config: WidgetConfig,
    ) {
        val backgroundColor = Color(config.bgColor.toColorInt())
        val contentColor = Color(config.textColor.toColorInt())

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
                config.contentOrder.filter { it.show }.forEach { section ->
                    val text = when (section.type) {
                        ContentType.Paal -> kural?.paal ?: ""
                        ContentType.Iyal -> kural?.iyal ?: ""
                        ContentType.Adhigaram -> kural?.adhigaram ?: ""
                        ContentType.Kural -> kural?.kural ?: "Tap to load Kural"
                        ContentType.Transliteration -> kural?.transliteration ?: ""
                    }
                    if (text.isNotEmpty()) {
                        RenderText(text, contentColor, section)
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
        sectionConfig: SectionConfig
    ) {
        Text(
            text = text,
            style = TextStyle(
                color = ColorProvider(color),
                fontWeight = if (sectionConfig.bold) FontWeight.Bold else FontWeight.Normal,
                fontSize = sectionConfig.size.sp,
                textAlign = when (sectionConfig.align) {
                    WidgetTextAlign.Start -> TextAlign.Start
                    WidgetTextAlign.Center -> TextAlign.Center
                    WidgetTextAlign.End -> TextAlign.End
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
