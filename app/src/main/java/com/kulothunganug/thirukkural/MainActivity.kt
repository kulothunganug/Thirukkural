package com.kulothunganug.thirukkural

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kulothunganug.thirukkural.di.appModule
import com.kulothunganug.thirukkural.ui.theme.ThirukkuralTheme
import com.kulothunganug.thirukkural.views.HomeView
import com.kulothunganug.thirukkural.views.SettingsScreen
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.compose.koinViewModel
import org.koin.core.context.GlobalContext.startKoin

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
                    startDestination = "home"
                ) {
                    composable("home") { 
                        HomeView(
                            vm = koinViewModel(),
                            navController = navController
                        ) 
                    }
                    composable("settings") { SettingsScreen(navController) }
                }
            }
        }
    }
}