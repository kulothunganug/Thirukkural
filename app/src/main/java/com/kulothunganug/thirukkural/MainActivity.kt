package com.kulothunganug.thirukkural

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kulothunganug.thirukkural.di.appModule
import com.kulothunganug.thirukkural.ui.theme.ThirukkuralTheme
import com.kulothunganug.thirukkural.views.HomeView
import com.kulothunganug.thirukkural.views.KuralDetailView
import com.kulothunganug.thirukkural.views.SettingsView
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import org.koin.core.context.GlobalContext.startKoin

const val SCREEN_TRANSITION_MILLIS = 500
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        startKoin {
            androidLogger()
            androidContext(this@MainActivity)
            modules(appModule)
        }

        setContent {
            val navController = rememberNavController()

            ThirukkuralTheme {
                NavHost(
                    navController = navController,
                    startDestination = "home",
                    enterTransition = { slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Start, tween(
                            SCREEN_TRANSITION_MILLIS
                        )
                    ) },
                    exitTransition = { slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Start, tween(
                            SCREEN_TRANSITION_MILLIS
                        )
                    ) },
                    popEnterTransition = { slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.End, tween(
                            SCREEN_TRANSITION_MILLIS
                        )
                    ) },
                    popExitTransition = { slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.End, tween(
                            SCREEN_TRANSITION_MILLIS
                        )
                    ) }
                ) {
                    composable("home") { 
                        HomeView(
                            vm = koinViewModel(),
                            navController = navController
                        ) 
                    }
                    composable("kural_detail/{kuralId}") { backStackEntry ->
                        val kuralId = backStackEntry.arguments?.getString("kuralId")?.toIntOrNull() ?: 1
                        KuralDetailView(
                            kuralId = kuralId,
                            vm = koinViewModel(),
                            navController = navController
                        )
                    }
                    composable("settings") { SettingsView(koinInject()) }

                }
            }
        }
    }
}