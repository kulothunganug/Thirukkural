package com.kulothunganug.thirukkural

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.ComponentName
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
import com.kulothunganug.thirukkural.widget.ThirukkuralWidgetReceiver
import org.koin.android.ext.android.inject


class WidgetCustomizationActivity : ComponentActivity() {

    private val themeSettings: ThemeSettings by inject()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Per the AppWidget configuration contract: default to RESULT_CANCELED immediately, so
        // that if the user backs out (including via the system back gesture, which never runs
        // our onDone callback) the widget host correctly treats this as a cancelled config.
        setResult(Activity.RESULT_CANCELED)

        val appWidgetId = intent?.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID

        if (!isOwnedByThisWidget(appWidgetId)) {
            // Either launched without a real widget id, or (defensively) with one that doesn't
            // belong to our widget provider — nothing safe to configure, so bail out before
            // touching any Glance state for it.
            finish()
            return
        }

        enableEdgeToEdge()

        setContent {

            val theme by themeSettings.themeStream.collectAsState(initial = AppTheme.SYSTEM)

            ThirukkuralTheme(theme) {
                WidgetCustomizationView(
                    appWidgetId,
                    onDone = { code ->
                        val resultValue = Intent().putExtra(
                            AppWidgetManager.EXTRA_APPWIDGET_ID,
                            appWidgetId
                        )
                        setResult(code, resultValue)
                        finish()
                    }
                )
            }

        }
    }

    private fun isOwnedByThisWidget(appWidgetId: Int): Boolean {
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) return false

        val info = AppWidgetManager.getInstance(this).getAppWidgetInfo(appWidgetId) ?: return false
        return info.provider == ComponentName(this, ThirukkuralWidgetReceiver::class.java)
    }
}
