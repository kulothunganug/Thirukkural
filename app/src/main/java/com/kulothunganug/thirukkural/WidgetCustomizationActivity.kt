package com.kulothunganug.thirukkural

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.kulothunganug.thirukkural.datastore.AppTheme
import com.kulothunganug.thirukkural.datastore.ThemeSettings
import com.kulothunganug.thirukkural.ui.theme.ThirukkuralTheme
import com.kulothunganug.thirukkural.views.WidgetCustomizationView
import org.koin.android.ext.android.inject
import kotlin.getValue


class WidgetCustomizationActivity : ComponentActivity() {

    private val themeSettings: ThemeSettings by inject()
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val appWidgetId = intent?.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID

        setContent {

            val theme by themeSettings.themeStream.collectAsState(initial = AppTheme.SYSTEM)

            ThirukkuralTheme(theme) {WidgetCustomizationView(
                appWidgetId,
                onDone = { code ->
                    val resultValue = Intent().putExtra(
                        AppWidgetManager.EXTRA_APPWIDGET_ID,
                        appWidgetId
                    )
                    setResult(code, resultValue)
                    finish()
                }
            ) }

        }
    }
}
