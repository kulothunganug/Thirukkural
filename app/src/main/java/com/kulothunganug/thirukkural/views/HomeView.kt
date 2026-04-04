package com.kulothunganug.thirukkural.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.kulothunganug.thirukkural.viewmodels.HomeViewModel
import kotlin.collections.isNotEmpty

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeView(vm: HomeViewModel, navController: NavController) {
    val uiState by vm.uiState.collectAsState()
    var query by remember { mutableStateOf("") }
    val MIN_ADHIGARAM = 1
    val MAX_ADHIGARAM = 133

    val canGoPrev = uiState.adhigaramId > MIN_ADHIGARAM
    val canGoNext = uiState.adhigaramId < MAX_ADHIGARAM

    Scaffold(
        topBar = {
            TopAppBar(
                actions = {
                    IconButton(
                        onClick = { navController.navigate("settings") }
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = "Search")
                    }
                },
                title = {
                    Text("Thirukural")
                }
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(8.dp)
        ) {
            OutlinedTextField(
                value = query,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "search"
                    )
                },
                onValueChange = { query = it },
                placeholder = { Text("Search") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(28.dp),
            )
            Spacer(Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(
                    enabled = canGoPrev,
                    onClick = {
                        vm.updateAdhigaram(uiState.adhigaramId - 1)
                    }
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "previous"
                    )
                }
                if (uiState.kurals.isNotEmpty()) {
                    Text(
                        "${uiState.adhigaramId} - ${uiState.kurals[0].adhigaram}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
                IconButton(
                    enabled = canGoNext,
                    onClick = {
                        vm.updateAdhigaram(uiState.adhigaramId + 1)
                    }
                ) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "next")
                }
            }
            Spacer(Modifier.height(8.dp))
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(uiState.kurals.size) { i ->
                    Card(onClick = {}) {
                        Text(
                            uiState.kurals[i].kural.replace("<br />", "\n"),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}