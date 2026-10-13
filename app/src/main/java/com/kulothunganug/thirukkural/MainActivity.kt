package com.kulothunganug.thirukkural

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.kulothunganug.thirukkural.datastore.AppTheme
import com.kulothunganug.thirukkural.datastore.ThemeSettings
import com.kulothunganug.thirukkural.models.MIN_KURAL_ID
import com.kulothunganug.thirukkural.models.isValidKuralId
import com.kulothunganug.thirukkural.ui.theme.ThirukkuralTheme
import com.kulothunganug.thirukkural.views.BrowseView
import com.kulothunganug.thirukkural.views.FavouritesView
import com.kulothunganug.thirukkural.views.HomeView
import com.kulothunganug.thirukkural.views.KuralDetailView
import com.kulothunganug.thirukkural.views.SettingsView
import com.kulothunganug.thirukkural.views.WidgetCustomizationView
import com.kulothunganug.thirukkural.widget.ThirukkuralWidgetReceiver
import org.koin.android.ext.android.inject
import org.koin.androidx.compose.koinViewModel

const val SCREEN_TRANSITION_MILLIS = 500

class MainActivity : ComponentActivity() {
    private val themeSettings: ThemeSettings by inject()

    // The widget id from an APPWIDGET_CONFIGURE launch, used ONLY for the
    // configuration-activity result contract. Customization itself is global:
    // one shared config applied to every widget instance.
    private var configureResultWidgetId: Int = AppWidgetManager.INVALID_APPWIDGET_ID
    // Set when a configure launch arrives via onNewIntent while already alive.
    private var pendingConfigureNavigate by mutableIntStateOf(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val initialConfigureId = extractConfigureWidgetId(intent)
        if (initialConfigureId != AppWidgetManager.INVALID_APPWIDGET_ID) {
            // Per the AppWidget configuration contract: default to RESULT_CANCELED
            // immediately, so backing out (including via system back gesture, which
            // never runs our onDone callback) is treated as a cancelled config.
            setResult(Activity.RESULT_CANCELED)
            configureResultWidgetId = initialConfigureId
        }

        enableEdgeToEdge()

        setContent {
            val theme by themeSettings.themeStream.collectAsState(initial = AppTheme.SYSTEM)
            val navController = rememberNavController()

            // A configure launch that arrives while this activity is already alive
            // (singleTop / onNewIntent) still needs to open the customization screen.
            // The cold-start configure case is already covered by startDestination.
            val pendingNavigate = pendingConfigureNavigate
            LaunchedEffect(pendingNavigate) {
                if (pendingNavigate != 0) {
                    navController.navigate("widget_customization") {
                        launchSingleTop = true
                    }
                }
            }

            // If launched directly as the configure activity, start on the
            // customization screen instead of home.
            val startDestination =
                if (initialConfigureId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                    "widget_customization"
                } else {
                    "home"
                }

            ThirukkuralTheme(appTheme = theme) {
                NavHost(
                    navController = navController,
                    startDestination = startDestination,
                    enterTransition = {
                        slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.Start, tween(
                                SCREEN_TRANSITION_MILLIS
                            )
                        )
                    },
                    exitTransition = {
                        slideOutOfContainer(
                            AnimatedContentTransitionScope.SlideDirection.Start, tween(
                                SCREEN_TRANSITION_MILLIS
                            )
                        )
                    },
                    popEnterTransition = {
                        slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.End, tween(
                                SCREEN_TRANSITION_MILLIS
                            )
                        )
                    },
                    popExitTransition = {
                        slideOutOfContainer(
                            AnimatedContentTransitionScope.SlideDirection.End, tween(
                                SCREEN_TRANSITION_MILLIS
                            )
                        )
                    }
                ) {
                    composable("home") {
                        HomeView(
                            vm = koinViewModel(),
                            navController = navController
                        )
                    }
                    composable("browse") {
                        BrowseView(
                            vm = koinViewModel(),
                            navController = navController
                        )
                    }
                    composable(
                        "kural_detail/{kuralId}",
                        arguments = listOf(navArgument("kuralId") { type = NavType.StringType }),
                        deepLinks = listOf(navDeepLink {
                            uriPattern = "thirukkural_app://kural/{kuralId}"
                        })
                    ) { backStackEntry ->
                        // The id can come from an externally-triggered deep link (BROWSABLE
                        // intent-filter), so it must never be trusted without a range check —
                        // an out-of-range id would otherwise crash the database lookup.
                        val kuralId = backStackEntry.arguments?.getString("kuralId")
                            ?.toIntOrNull()
                            ?.takeIf { isValidKuralId(it) }
                            ?: MIN_KURAL_ID
                        KuralDetailView(
                            kuralId = kuralId,
                            vm = koinViewModel(),
                            navController = navController
                        )
                    }
                    composable("settings") {
                        SettingsView(
                            navController = navController,
                        )
                    }
                    composable("favourites") {
                        FavouritesView(
                            vm = koinViewModel(),
                            navController = navController
                        )
                    }
                    composable("widget_customization") {
                        // True only for a real system configure session (picker add /
                        // reconfigure / pin callback). In-app edits from Settings just pop.
                        // The widget id is used solely for the RESULT contract — the saved
                        // config is applied globally to all instances.
                        val isConfigureSession =
                            configureResultWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID &&
                                isLaunchedAsConfigure()

                        WidgetCustomizationView(
                            onDone = { code ->
                                if (isConfigureSession) {
                                    val resultValue = Intent().putExtra(
                                        AppWidgetManager.EXTRA_APPWIDGET_ID,
                                        configureResultWidgetId
                                    )
                                    setResult(code, resultValue)
                                    finish()
                                } else {
                                    navController.popBackStack()
                                }
                            }
                        )
                    }

                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        val configureId = extractConfigureWidgetId(intent)
        if (configureId != AppWidgetManager.INVALID_APPWIDGET_ID) {
            setResult(Activity.RESULT_CANCELED)
            configureResultWidgetId = configureId
            pendingConfigureNavigate++
        }
    }

    private fun isLaunchedAsConfigure(): Boolean =
        intent?.action == AppWidgetManager.ACTION_APPWIDGET_CONFIGURE

    /**
     * Returns the widget id only when this intent is an APPWIDGET_CONFIGURE
     * request for a widget id owned by our provider. Anything else (normal
     * launcher start, deep link, in-app navigation) returns INVALID, so a
     * forged or unrelated extra can never drive Glance state writes.
     */
    private fun extractConfigureWidgetId(intent: Intent?): Int {
        if (intent?.action != AppWidgetManager.ACTION_APPWIDGET_CONFIGURE) return AppWidgetManager.INVALID_APPWIDGET_ID
        val appWidgetId = intent.getIntExtra(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        )
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) return AppWidgetManager.INVALID_APPWIDGET_ID
        return if (isOwnedByThisWidget(appWidgetId)) appWidgetId else AppWidgetManager.INVALID_APPWIDGET_ID
    }

    private fun isOwnedByThisWidget(appWidgetId: Int): Boolean {
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) return false
        val info = AppWidgetManager.getInstance(this).getAppWidgetInfo(appWidgetId) ?: return false
        return info.provider == ComponentName(this, ThirukkuralWidgetReceiver::class.java)
    }
}
