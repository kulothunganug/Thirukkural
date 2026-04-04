package com.kulothunganug.thirukkural.widget

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
import androidx.glance.layout.padding
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.kulothunganug.thirukkural.ThirukkuralDatabase
import kotlin.random.Random

object ThirukkuralWidgetKeys {
    val KURAL_TEXT = stringPreferencesKey("kural_text")
    val KURAL_ID = intPreferencesKey("kural_id")
    val ADHIGARAM = stringPreferencesKey("adhigaram")
}

class ThirukkuralWidget : GlanceAppWidget() {

    override val stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlanceTheme {
                val prefs = currentState<Preferences>()
                val kuralText = prefs[ThirukkuralWidgetKeys.KURAL_TEXT] ?: "Tap to load Kural"
                val kuralId = prefs[ThirukkuralWidgetKeys.KURAL_ID] ?: 0
                val adhigaram = prefs[ThirukkuralWidgetKeys.ADHIGARAM] ?: ""

                WidgetContent(kuralId, adhigaram, kuralText)
            }
        }
    }

    @Composable
    private fun WidgetContent(id: Int, adhigaram: String, text: String) {
        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(GlanceTheme.colors.background)
                .padding(16.dp)
                .clickable(actionRunCallback<RefreshKuralAction>()),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (id != 0) {
                    Text(
                        text = "$id - $adhigaram",
                        style = TextStyle(
                            color = GlanceTheme.colors.onSurface,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                    )
                }
                Text(
                    text = text.replace("<br />", "\n"),
                    style = TextStyle(
                        color = GlanceTheme.colors.onSurface,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    ),
                    modifier = GlanceModifier.padding(top = 8.dp)
                )
            }
        }
    }
}

class RefreshKuralAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val db = ThirukkuralDatabase.get(context)
        val randomId = Random.nextInt(1, 1331)
        val kural = db.dao().getByKural(randomId)

        updateAppWidgetState(context, PreferencesGlanceStateDefinition, glanceId) { prefs ->
            prefs.toMutablePreferences().apply {
                this[ThirukkuralWidgetKeys.KURAL_TEXT] = kural.kural
                this[ThirukkuralWidgetKeys.KURAL_ID] = kural.id
                this[ThirukkuralWidgetKeys.ADHIGARAM] = kural.adhigaram
            }
        }
        ThirukkuralWidget().update(context, glanceId)
    }
}

class ThirukkuralWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = ThirukkuralWidget()
}
