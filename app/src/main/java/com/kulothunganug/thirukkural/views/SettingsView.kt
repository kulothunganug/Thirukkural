package com.kulothunganug.thirukkural.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

data class ElementSettingItem(
    val idx: Int,
    val label: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsView(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Go back"
                    )
                }
            }, title = { Text("Settings") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            ListItem(headlineContent = {
                Text("Hello")
            }, modifier = Modifier.clickable(true, onClick = {}))
            Text("Hello2 world this is me of the worlds one of the best things that happended to me some days of the best thing this ", maxLines = 1, autoSize = TextAutoSize.StepBased(12.sp, 20.sp, 2.sp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SetttingsViewPreview() {
    val navController = rememberNavController()
    SettingsView(navController = navController)
}