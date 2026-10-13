package com.kulothunganug.thirukkural.views

import android.app.LocaleManager
import android.content.Context
import android.os.Build
import android.os.LocaleList
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.kulothunganug.thirukkural.R
import com.kulothunganug.thirukkural.datastore.AppTheme
import com.kulothunganug.thirukkural.datastore.SecondaryScript
import com.kulothunganug.thirukkural.datastore.availableSecondaries
import com.kulothunganug.thirukkural.datastore.coerced
import com.kulothunganug.thirukkural.shared_ui.endItemShape
import com.kulothunganug.thirukkural.shared_ui.leadingItemShape
import com.kulothunganug.thirukkural.shared_ui.listItemColors
import com.kulothunganug.thirukkural.shared_ui.middleItemShape
import com.kulothunganug.thirukkural.viewmodels.SettingsViewModel
import org.koin.androidx.compose.koinViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsView(
    navController: NavController,
    vm: SettingsViewModel = koinViewModel()
) {
    val currentTheme by vm.theme.collectAsState()
    val secondaryScript by vm.secondaryScript.collectAsState()
    val currentLanguage = LocalLocale.current.platformLocale.language
    val isTamilPrimary = currentLanguage == "ta"
    val context = LocalContext.current

    fun setAppLanguage(languageTag: String) {
        if (Build.VERSION.SDK_INT >= 33) {
            val localeManager =
                context.getSystemService(LocaleManager::class.java)

            localeManager.applicationLocales =
                LocaleList(Locale.forLanguageTag(languageTag))
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.go_back)
                        )
                    }
                },
                title = { Text(stringResource(R.string.settings)) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(12.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = stringResource(R.string.theme),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(12.dp)
            )

            ThemeOption(
                label = stringResource(R.string.system_default),
                selected = currentTheme == AppTheme.SYSTEM,
                shape = leadingItemShape(),
                onClick = { vm.setTheme(AppTheme.SYSTEM) }
            )
            ThemeOption(
                label = stringResource(R.string.light),
                selected = currentTheme == AppTheme.LIGHT,
                shape = middleItemShape(),
                onClick = { vm.setTheme(AppTheme.LIGHT) }
            )
            ThemeOption(
                label = stringResource(R.string.dark),
                selected = currentTheme == AppTheme.DARK,
                shape = endItemShape(),
                onClick = { vm.setTheme(AppTheme.DARK) }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.app_language),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(12.dp)
            )

            val languageSwitchSupported = Build.VERSION.SDK_INT >= 33

            ThemeOption(
                label = stringResource(R.string.english),
                selected = currentLanguage == "en",
                shape = leadingItemShape(),
                enabled = languageSwitchSupported,
                onClick = {
                    setAppLanguage("en")
                }
            )
            ThemeOption(
                label = stringResource(R.string.tamil),
                selected = currentLanguage == "ta",
                shape = endItemShape(),
                enabled = languageSwitchSupported,
                onClick = {
                    setAppLanguage("ta")
                }
            )

            if (!languageSwitchSupported) {
                Text(
                    text = stringResource(R.string.language_switch_unsupported),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.secondary_text),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(12.dp)
            )

            // Only offer scripts that differ from the primary app language —
            // the primary is already the main line, so repeating it as the
            // supporting line would be redundant.
            val secondaryOptions = availableSecondaries(isTamilPrimary).map { it to it.label() }

            SingleSelectGroup(
                options = secondaryOptions,
                selected = secondaryScript.coerced(isTamilPrimary),
                onSelect = { vm.setSecondaryScript(it) }
            )
        }
    }
}

@Composable
private fun SecondaryScript.label(): String = when (this) {
    SecondaryScript.TAMIL -> stringResource(R.string.tamil)
    SecondaryScript.ENGLISH -> stringResource(R.string.english)
    SecondaryScript.TRANSLITERATION -> stringResource(R.string.transliteration)
    SecondaryScript.NONE -> stringResource(R.string.none)
}

/** Renders a radio group with grouped-list shapes (rounded ends, square middle). */
@Composable
private fun <T> SingleSelectGroup(
    options: List<Pair<T, String>>,
    selected: T,
    onSelect: (T) -> Unit,
    enabled: Boolean = true
) {
    options.forEachIndexed { index, (value, label) ->
        val shape = when {
            options.size == 1 -> leadingItemShape()
            index == 0 -> leadingItemShape()
            index == options.lastIndex -> endItemShape()
            else -> middleItemShape()
        }
        ThemeOption(
            label = label,
            selected = selected == value,
            shape = shape,
            enabled = enabled,
            onClick = { onSelect(value) }
        )
    }
}

@Composable
fun ThemeOption(
    label: String,
    selected: Boolean,
    shape: Shape,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    Surface(shape = shape) {
        ListItem(
            colors = listItemColors(),
            modifier = Modifier.clickable(enabled = enabled) { onClick() },
            headlineContent = { Text(text = label) },
            trailingContent = {
                RadioButton(
                    selected = selected,
                    onClick = onClick,
                    enabled = enabled
                )
            }
        )
    }
}
