package com.kulothunganug.thirukkural

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.kulothunganug.thirukkural.datastore.AppTheme
import com.kulothunganug.thirukkural.datastore.ThemeSettings
import com.kulothunganug.thirukkural.ui.theme.ThirukkuralTheme
import com.kulothunganug.thirukkural.views.BrowseView
import com.kulothunganug.thirukkural.views.HomeView
import com.kulothunganug.thirukkural.views.KuralDetailView
import com.kulothunganug.thirukkural.views.SettingsView
import org.koin.android.ext.android.inject
import org.koin.androidx.compose.koinViewModel

const val SCREEN_TRANSITION_MILLIS = 500

class MainActivity : ComponentActivity() {
    private val themeSettings: ThemeSettings by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        setContent {
            val theme by themeSettings.themeStream.collectAsState(initial = AppTheme.SYSTEM)
            val navController = rememberNavController()

            ThirukkuralTheme(appTheme = theme) {
                NavHost(
                    navController = navController,
                    startDestination = "home",
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
                        val kuralId =
                            backStackEntry.arguments?.getString("kuralId")?.toIntOrNull() ?: 1
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

                }
            }
        }
    }
}