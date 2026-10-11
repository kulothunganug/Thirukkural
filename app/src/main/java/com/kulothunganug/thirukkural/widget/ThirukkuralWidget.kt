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
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
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
import androidx.glance.color.ColorProvider
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
import com.kulothunganug.thirukkural.MainActivity
import com.kulothunganug.thirukkural.R
import com.kulothunganug.thirukkural.datastore.FavouritesSettings
import com.kulothunganug.thirukkural.models.ThirukkuralModel
import com.kulothunganug.thirukkural.models.randomKuralId
import com.kulothunganug.thirukkural.repository.ThirukkuralRepository
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Serializable
enum class ContentType { Paal, Iyal, Adhigaram, Kural, Thiruvalluvar }

@Serializable
enum class WidgetTextAlign { Start, Center, End }

@Serializable
enum class RefreshSource { All, Category, Favourites }

@Serializable
data class WidgetConfig(
    val bgColor: String = "#000000",
    val refreshButtonColor: String = "#ffffff",
    val autoRefreshEnabled: Boolean = false,
    val autoRefreshIntervalMinutes: Int = 60,
    val refreshSource: RefreshSource = RefreshSource.All,
    val refreshCategoryPals: List<String> = emptyList(),
    val refreshCategoryIyals: List<String> = emptyList(),
    val refreshCategoryAdikarams: List<String> = emptyList(),
    val contentOrder: List<SectionConfig> = listOf(
        SectionConfig(type = ContentType.Paal, show = false),
        SectionConfig(type = ContentType.Iyal, show = false),
        SectionConfig(type = ContentType.Adhigaram, bold = true),
        SectionConfig(
            type = ContentType.Kural,
            size = 14,
            align = WidgetTextAlign.Start
        ),
        SectionConfig(type = ContentType.Thiruvalluvar, show = false)
    )
)

@Serializable
data class SectionConfig(
    val type: ContentType,
    val show: Boolean = true,
    val size: Int = 12,
    val align: WidgetTextAlign = WidgetTextAlign.Center,
    val bold: Boolean = false,
    val textColor: String = "#ffffff"
)

const val THIRUVALLUVAR_TA = "திருவள்ளுவர்"

val WIDGET_CONFIG = stringPreferencesKey("widget_config")

object ThirukkuralWidgetKeys {
    val KURAL_ID = intPreferencesKey("kural_id")
}

class ThirukkuralWidget : GlanceAppWidget(), KoinComponent {
    override val stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition

    private val repository: ThirukkuralRepository by inject()

    override suspend fun providePreview(context: Context, widgetCategory: Int) {
        provideContent {
            WidgetContent(
                kural = ThirukkuralModel(
                    id = 1,
                    palTa = "அறத்துப்பால்",
                    palEn = "Virtue",
                    palTl = "Araththuppaal",
                    iyalTa = "பாயிரவியல்",
                    iyalEn = "Preface",
                    iyalTl = "Paayiraviyal",
                    adikaramTa = "கடவுள் வாழ்த்து",
                    adikaramEn = "The Praise of God",
                    adikaramTl = "Kadavul Vaazhthu",
                    kuralTa = "அகர முதல எழுத்தெல்லாம் ஆதி\nபகவன் முதற்றே உலகு.",
                    kuralTl = "Akara Mudhala Ezhuththellam Aadhi Bhagavan Mudhatre Ulaku.",
                    translationEn = "A, as its first of letters, every speech maintains; The Primal Deity is First through all the world's domains.",
                    couplet = "A, as its first of letters, every speech maintains; The Primal Deity is First through all the world's domains.",
                    explanationTa = "எழுத்துக்கள் எல்லாம் அகரத்தை அடிப்படையாக கொண்டிருக்கின்றன. அதுபோல உலகம் கடவுளை அடிப்படையாக கொண்டிருக்கிறது.",
                    explanationEn = "As the letter A is the first of all letters, so the eternal God is the first in the world.",
                    commentaryMuVaratharasanar = "",
                    commentaryParimelazhagar = "",
                    commentarySalamanPappaiya = "",
                    commentaryManakudavar = "",
                    commentaryMunusami = "",
                    commentaryKarunanidhi = ""
                ),
                config = WidgetConfig()
            )
        }
    }

    override suspend fun provideGlance(
        context: Context,
        id: GlanceId
    ) {
        updateAppWidgetState(
            context,
            PreferencesGlanceStateDefinition,
            id
        ) { prefs ->

            if (prefs[ThirukkuralWidgetKeys.KURAL_ID] == null) {
                prefs.toMutablePreferences().apply {
                    this[ThirukkuralWidgetKeys.KURAL_ID] =
                        randomKuralId()
                }
            } else {
                prefs
            }
        }

        provideContent {
            GlanceContent()
        }
    }

    @Composable
    private fun GlanceContent() {

        GlanceTheme {
            val context = LocalContext.current
            val prefs = currentState<Preferences>()
            val kuralId =
                prefs[ThirukkuralWidgetKeys.KURAL_ID]!!

            val json = prefs[WIDGET_CONFIG]

            val config = json?.let {
                Json.decodeFromString<WidgetConfig>(it)
            } ?: WidgetConfig()

            val kural by produceState<ThirukkuralModel?>(
                initialValue = null,
                key1 = kuralId
            ) {
                value = repository.getById(kuralId)
            }

            val currentKural = kural
            if (currentKural == null) {
                Text(context.getString(R.string.widget_loading))
                return@GlanceTheme
            }

            WidgetContent(
                kural = currentKural,
                config = config
            )
        }
    }


    @Composable
    private fun WidgetContent(
        kural: ThirukkuralModel,
        config: WidgetConfig,
    ) {
        val backgroundColor = Color(config.bgColor.toColorInt())
        val context = LocalContext.current

        Box(
            modifier = GlanceModifier.fillMaxSize().background(backgroundColor).padding(12.dp)
                .clickable(
                    actionRunCallback<OpenKuralAction>(
                        parameters = actionParametersOf(
                            OpenKuralAction.kuralIdKey to kural.id
                        )
                    )
                ),
            contentAlignment = Alignment.BottomEnd
        ) {
            Column(
                modifier = GlanceModifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalAlignment = Alignment.CenterVertically
            ) {
                config.contentOrder.filter { it.show }.forEach { section ->
                    val text = when (section.type) {
                        ContentType.Paal -> kural.palTa
                        ContentType.Iyal -> kural.iyalTa
                        ContentType.Adhigaram -> kural.adikaramTa
                        ContentType.Kural -> kural.kuralTa.replace("<br />", "\n")
                        ContentType.Thiruvalluvar -> THIRUVALLUVAR_TA
                    }
                    if (text.isNotEmpty()) {
                        val textColor = Color(section.textColor.toColorInt())
                        RenderText(text, textColor, section)
                    }
                }
            }
            Image(
                provider = ImageProvider(R.drawable.refresh_24px),
                contentDescription = context.getString(R.string.refresh),
                colorFilter = ColorFilter.tint(
                    ColorProvider(
                        day = Color(config.refreshButtonColor.toColorInt()),
                        night = Color(config.refreshButtonColor.toColorInt())
                    )
                ),
                modifier = GlanceModifier.cornerRadius(12.dp).padding(6.dp)
                    .clickable(actionRunCallback<RefreshKuralAction>())

            )
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
                color = ColorProvider(
                    day = color,
                    night = color
                ),
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

class RefreshKuralAction : ActionCallback, KoinComponent {

    private val repository: ThirukkuralRepository by inject()
    private val favouritesSettings: FavouritesSettings by inject()

    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        updateAppWidgetState(context, PreferencesGlanceStateDefinition, glanceId) { prefs ->
            val config = prefs[WIDGET_CONFIG]?.let { Json.decodeFromString<WidgetConfig>(it) }
                ?: WidgetConfig()
            val currentId = prefs[ThirukkuralWidgetKeys.KURAL_ID]
            val newId = pickKuralId(config, repository, favouritesSettings, currentId)
            prefs.toMutablePreferences().apply {
                this[ThirukkuralWidgetKeys.KURAL_ID] = newId
            }
        }
        ThirukkuralWidget().update(context, glanceId)
    }
}

class ThirukkuralWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = ThirukkuralWidget()

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        super.onDeleted(context, appWidgetIds)
        appWidgetIds.forEach { WidgetRefreshScheduler.cancel(context, it) }
    }
}
