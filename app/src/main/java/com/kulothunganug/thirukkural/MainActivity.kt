package com.kulothunganug.thirukkural

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.kulothunganug.thirukkural.screens.HomeScreen
import com.kulothunganug.thirukkural.screens.SettingsScreen
import com.kulothunganug.thirukkural.ui.theme.ThirukkuralTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val navController = rememberNavController()

            val factory = remember {
                ThirukkuralViewModelFactory(
                    ThirukkuralDatabase.get(this).dao()
                )
            }

            val vm: ThirukkuralViewModel = viewModel (factory = factory)

            ThirukkuralTheme {
                NavHost(
                    navController = navController,
                    startDestination = "home"
                ) {
                    composable("home") { HomeScreen(vm, navController) }
                    composable("settings") { SettingsScreen(navController) }
                }
            }
        }
    }
}



@Composable
fun _HomeScreen(navController: NavController) {
    var query by remember { mutableStateOf("") }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            IconButton(
                onClick = {navController.navigate("settings")}, modifier = Modifier
                    .padding(32.dp)
                    .align(Alignment.BottomEnd)
            ) {
                Icon(Icons.Default.Settings, contentDescription = "Search")
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(
                    12.dp,
                    alignment = Alignment.CenterVertically
                ),
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(6.dp)
                    .fillMaxSize()
            ) {
                OutlinedTextField(
                    value = query,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Search thirukkural"
                        )
                    },
                    onValueChange = { query = it },
                    placeholder = { Text("Search") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(28.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceBright,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceBright
                    )
                )

                FilledTonalButton(
                    onClick = {navController.navigate("all-kural")},
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.List, contentDescription = "List")
                    Spacer(Modifier.weight(1f))
                    Text("List all")
                    Spacer(Modifier.weight(1f))
                }

                OutlinedButton(
                    onClick = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Random")
                    Spacer(Modifier.weight(1f))
                    Text("Random kural")
                    Spacer(Modifier.weight(1f))
                }
            }

        }
    }
}