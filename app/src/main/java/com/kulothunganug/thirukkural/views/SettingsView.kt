package com.kulothunganug.thirukkural.views

import android.app.LocaleManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.LocaleList
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Widgets
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavController
import com.kulothunganug.thirukkural.R
import com.kulothunganug.thirukkural.datastore.AppTheme
import com.kulothunganug.thirukkural.datastore.SecondaryScript
import com.kulothunganug.thirukkural.datastore.availableSecondaries
import com.kulothunganug.thirukkural.datastore.coerced
import com.kulothunganug.thirukkural.shared_ui.detachedItemShape
import com.kulothunganug.thirukkural.shared_ui.endItemShape
import com.kulothunganug.thirukkural.shared_ui.leadingItemShape
import com.kulothunganug.thirukkural.shared_ui.listItemColors
import com.kulothunganug.thirukkural.shared_ui.middleItemShape
import com.kulothunganug.thirukkural.viewmodels.SettingsViewModel
import org.koin.androidx.compose.koinViewModel
import java.util.Locale

private const val GITHUB_REPO_URL = "https://github.com/kulothunganug/Thirukkural"
private const val GITHUB_PROFILE_URL = "https://github.com/kulothunganug"
private const val WEBSITE_URL = "https://kulothunganug.github.io/"
private const val EMAIL_ADDRESS = "kulothunganug@gmail.com"

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

    val versionName = remember {
        try {
            val packageInfo = if (Build.VERSION.SDK_INT >= 33) {
                context.packageManager.getPackageInfo(
                    context.packageName,
                    PackageManager.PackageInfoFlags.of(0)
                )
            } else {
                @Suppress("DEPRECATION")
                context.packageManager.getPackageInfo(context.packageName, 0)
            }
            packageInfo.versionName ?: "—"
        } catch (_: Exception) {
            "—"
        }
    }

    fun openUrl(url: String) {
        try {
            context.startActivity(Intent(Intent.ACTION_VIEW, url.toUri()))
        } catch (_: Exception) {
        }
    }

    fun sendEmail() {
        try {
            context.startActivity(Intent(Intent.ACTION_SENDTO, "mailto:$EMAIL_ADDRESS".toUri()))
        } catch (_: Exception) {
        }
    }

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
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
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
                text = stringResource(R.string.secondary_text_language),
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

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.widget),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(12.dp)
            )

            Surface(shape = detachedItemShape()) {
                ListItem(
                    colors = listItemColors(),
                    modifier = Modifier.clickable {
                        navController.navigate("widget_customization")
                    },
                    headlineContent = { Text(text = stringResource(R.string.customize_widget_look)) }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.about),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(12.dp)
            )

            Surface(shape = leadingItemShape()) {
                ListItem(
                    colors = listItemColors(),
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = null
                        )
                    },
                    headlineContent = { Text(text = stringResource(R.string.app_name)) },
                    supportingContent = { Text(text = stringResource(R.string.app_version, versionName)) },
                    trailingContent = {
                        IconButton(onClick = { openUrl(GITHUB_REPO_URL) }) {
                            Icon(
                                painter = painterResource(R.drawable.ic_github),
                                contentDescription = stringResource(R.string.open_github_repository)
                            )
                        }
                    }
                )
            }
            Surface(shape = endItemShape()) {
                ListItem(
                    colors = listItemColors(),
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = null
                        )
                    },
                    headlineContent = { Text(text = stringResource(R.string.developer_name)) },
                    supportingContent = { Text(text = stringResource(R.string.developer)) },
                    trailingContent = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { openUrl(GITHUB_PROFILE_URL) }) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_github),
                                    contentDescription = stringResource(R.string.open_github_profile)
                                )
                            }
                            IconButton(onClick = { openUrl(WEBSITE_URL) }) {
                                Icon(
                                    imageVector = Icons.Filled.Public,
                                    contentDescription = stringResource(R.string.open_website)
                                )
                            }
                            IconButton(onClick = { sendEmail() }) {
                                Icon(
                                    imageVector = Icons.Filled.Email,
                                    contentDescription = stringResource(R.string.open_email)
                                )
                            }
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
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
