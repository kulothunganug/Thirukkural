package com.kulothunganug.thirukkural.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.kulothunganug.thirukkural.datastore.AppTheme
import com.kulothunganug.thirukkural.shared_ui.endItemShape
import com.kulothunganug.thirukkural.shared_ui.leadingItemShape
import com.kulothunganug.thirukkural.shared_ui.listItemColors
import com.kulothunganug.thirukkural.shared_ui.middleItemShape
import com.kulothunganug.thirukkural.viewmodels.SettingsViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsView(
    navController: NavController,
    vm: SettingsViewModel = koinViewModel()
) {
    val currentTheme by vm.theme.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Go back"
                        )
                    }
                },
                title = { Text("Settings") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(8.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = "Theme",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(16.dp)
            )

            ThemeOption(
                label = "System Default",
                selected = currentTheme == AppTheme.SYSTEM,
                shape = leadingItemShape(),
                onClick = { vm.setTheme(AppTheme.SYSTEM) }
            )
            ThemeOption(
                label = "Light",
                selected = currentTheme == AppTheme.LIGHT,
                shape = middleItemShape(),
                onClick = { vm.setTheme(AppTheme.LIGHT) }
            )
            ThemeOption(
                label = "Dark",
                selected = currentTheme == AppTheme.DARK,
                shape = endItemShape(),
                onClick = { vm.setTheme(AppTheme.DARK) }
            )
        }
    }
}

@Composable
fun ThemeOption(
    label: String,
    selected: Boolean,
    shape: Shape,
    onClick: () -> Unit
) {
    Surface(shape = shape) {
        ListItem(
            colors = listItemColors(),
            modifier = Modifier.clickable { onClick() },
            headlineContent = { Text(text = label) },
            trailingContent = {
                RadioButton(
                    selected = selected,
                    onClick = onClick
                )
            }
        )
    }
}
