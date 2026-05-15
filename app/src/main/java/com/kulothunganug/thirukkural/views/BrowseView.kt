package com.kulothunganug.thirukkural.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.kulothunganug.thirukkural.viewmodels.BrowseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowseView(vm: BrowseViewModel, navController: NavController) {
    val uiState by vm.uiState.collectAsState()
    var showFilterDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Browse Kurals") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showFilterDialog = true }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filter")
                    }
                }
            )
        }
    ) { padding ->
        if (showFilterDialog) {
            AlertDialog(
                onDismissRequest = { showFilterDialog = false },
                confirmButton = {
                    TextButton(onClick = { showFilterDialog = false }) {
                        Text("Done")
                    }
                },
                title = { Text("Filters") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        MultiFilterDropdown(
                            label = "Pal",
                            options = uiState.pals,
                            selectedOptions = uiState.selectedPals,
                            onOptionToggled = { vm.togglePal(it) }
                        )

                        MultiFilterDropdown(
                            label = "Iyal",
                            options = uiState.iyals,
                            selectedOptions = uiState.selectedIyals,
                            enabled = uiState.selectedPals.isNotEmpty(),
                            onOptionToggled = { vm.toggleIyal(it) }
                        )

                        MultiFilterDropdown(
                            label = "Adikaram",
                            options = uiState.adikarams,
                            selectedOptions = uiState.selectedAdikarams,
                            enabled = uiState.selectedIyals.isNotEmpty(),
                            onOptionToggled = { vm.toggleAdikaram(it) }
                        )
                    }
                }
            )
        }

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                if (uiState.kurals.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillParentMaxSize()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Select filters to browse Kurals",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                } else {
                    items(uiState.kurals) { kural ->
                        Card(
                            onClick = {
                                navController.navigate("kural_detail/${kural.id}")
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = kural.kuralTa.replace("<br />", "\n"),
                                    fontSize = 16.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${kural.palTa} - ${kural.iyalTa} - ${kural.adikaramTa}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MultiFilterDropdown(
    label: String,
    options: List<String>,
    selectedOptions: List<String>,
    onOptionToggled: (String) -> Unit,
    enabled: Boolean = true
) {
    var expanded by remember { mutableStateOf(false) }
    val displayText = when {
        selectedOptions.isEmpty() -> "Select $label"
        selectedOptions.size == 1 -> selectedOptions.first()
        else -> "${selectedOptions.size} $label selected"
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { if (enabled) expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = displayText,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, true)
                .fillMaxWidth(),
            enabled = enabled
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                val isSelected = selectedOptions.contains(option)
                DropdownMenuItem(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = isSelected,
                                onCheckedChange = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(option)
                        }
                    },
                    onClick = {
                        onOptionToggled(option)
                    }
                )
            }
        }
    }
}
