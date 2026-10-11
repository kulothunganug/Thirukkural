package com.kulothunganug.thirukkural.shared_ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kulothunganug.thirukkural.R
import com.kulothunganug.thirukkural.models.KuralCategory
import com.kulothunganug.thirukkural.shared_ui.LanguageAwareLabel

/**
 * Pal → Iyal → Adhigaram cascading filter dialog, shared by BrowseView (browsing Kurals) and
 * WidgetCustomizationView (picking the widget auto-refresh category) since both need the exact
 * same three-level narrowing UI, just with a different [title] and a different backing state.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryFilterDialog(
    title: String,
    pals: List<KuralCategory>,
    iyals: List<KuralCategory>,
    adikarams: List<KuralCategory>,
    selectedPals: List<String>,
    selectedIyals: List<String>,
    selectedAdikarams: List<String>,
    onTogglePal: (String) -> Unit,
    onToggleIyal: (String) -> Unit,
    onToggleAdikaram: (String) -> Unit,
    onDismissRequest: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(R.string.done))
            }
        },
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                MultiFilterDropdown(
                    label = stringResource(R.string.part),
                    options = pals,
                    selectedOptions = selectedPals,
                    onOptionToggled = onTogglePal
                )

                MultiFilterDropdown(
                    label = stringResource(R.string.section),
                    options = iyals,
                    selectedOptions = selectedIyals,
                    enabled = selectedPals.isNotEmpty(),
                    onOptionToggled = onToggleIyal
                )

                MultiFilterDropdown(
                    label = stringResource(R.string.chapter),
                    options = adikarams,
                    selectedOptions = selectedAdikarams,
                    enabled = selectedIyals.isNotEmpty(),
                    onOptionToggled = onToggleAdikaram
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MultiFilterDropdown(
    label: String,
    options: List<KuralCategory>,
    selectedOptions: List<String>,
    onOptionToggled: (String) -> Unit,
    enabled: Boolean = true
) {
    var expanded by remember { mutableStateOf(false) }
    val currentLanguage = LocalLocale.current.platformLocale.language
    val isTamil = currentLanguage == "ta"

    val displayText = when {
        selectedOptions.isEmpty() -> stringResource(R.string.select_label, label)
        selectedOptions.size == 1 -> {
            val selectedTa = selectedOptions.first()
            val category = options.find { it.ta == selectedTa }
            if (category != null) {
                if (isTamil) category.ta else category.en
            } else {
                selectedTa
            }
        }
        else -> stringResource(R.string.label_selected, selectedOptions.size, label)
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
                val isSelected = selectedOptions.contains(option.ta)
                DropdownMenuItem(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = isSelected,
                                onCheckedChange = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            LanguageAwareLabel(
                                ta = option.ta,
                                en = option.en,
                                tl = option.tl,
                                primaryStyle = MaterialTheme.typography.bodyLarge,
                                secondaryStyle = MaterialTheme.typography.bodySmall
                            )
                        }
                    },
                    onClick = {
                        onOptionToggled(option.ta)
                    }
                )
            }
        }
    }
}
