package com.kulothunganug.thirukkural.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.kulothunganug.thirukkural.viewmodels.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeView(vm: HomeViewModel, navController: NavController) {
    val uiState by vm.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                actions = {
                    IconButton(
                        onClick = { navController.navigate("settings") }
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                },
                title = {
                    Text("")
                }
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "திருக்குறள்",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                fontSize = 48.sp,
            )
            Spacer(modifier = Modifier.height(90.dp))
            uiState.randomKural?.let { kural ->
                Card(
                    onClick = {
                        navController.navigate("kural_detail/${kural.id}")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp),
                    colors = CardDefaults.cardColors().copy(
                        containerColor =
                            MaterialTheme.colorScheme.surfaceContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Kural of the Moment",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = kural.kuralTa.replace("<br />", "\n"),
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Left,
                            fontSize = 20.sp,
                            lineHeight = 28.sp,
                            maxLines = 2,
                            autoSize = TextAutoSize.StepBased(14.sp, 20.sp, 1.sp)

                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "— ${kural.adikaramTa}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.secondary,
                            textAlign = TextAlign.Left,
                        )
                    }
                }
            }

            FilledTonalButton(
                onClick = { navController.navigate("browse") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
            ) {
                Icon(Icons.Rounded.Search, contentDescription = "Browse")
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Browse Kurals",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}