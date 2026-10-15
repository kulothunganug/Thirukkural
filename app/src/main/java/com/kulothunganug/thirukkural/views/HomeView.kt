package com.kulothunganug.thirukkural.views

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.AddToHomeScreen
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Colorize
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.kulothunganug.thirukkural.MainActivity
import com.kulothunganug.thirukkural.R
import com.kulothunganug.thirukkural.shared_ui.LanguageAwareLabel
import com.kulothunganug.thirukkural.viewmodels.HomeViewModel
import com.kulothunganug.thirukkural.widget.ThirukkuralWidgetReceiver

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeView(vm: HomeViewModel, navController: NavController) {
    val uiState by vm.uiState.collectAsState()
    val context = LocalContext.current
    var showWidgetBottomSheet by remember { mutableStateOf(false) }

    if (showWidgetBottomSheet) {
        WidgetInfoBottomSheet(
            onDismissRequest = { showWidgetBottomSheet = false },
            onAddWidget = {
                requestPinThirukkuralWidget(context)
                showWidgetBottomSheet = false
            },
            onOpenWidgetSettings = {
                showWidgetBottomSheet = false
                navController.navigate("widget_customization")
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                actions = {
                    IconButton(
                        onClick = { showWidgetBottomSheet = true }
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.AddToHomeScreen,
                            contentDescription = stringResource(R.string.add_widget_to_home_screen)
                        )
                    }
                    IconButton(
                        onClick = { navController.navigate("favourites") }
                    ) {
                        Icon(
                            Icons.Default.Favorite,
                            contentDescription = stringResource(R.string.favourites)
                        )
                    }
                    IconButton(
                        onClick = { navController.navigate("settings") }
                    ) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = stringResource(R.string.settings)
                        )
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
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                fontSize = 48.sp,
            )
            Spacer(modifier = Modifier.height(90.dp))

            if (uiState.showWidgetHint) {
                WidgetHintCard(
                    onAddWidget = {
                        showWidgetBottomSheet = true
                        vm.onWidgetHintShown()
                    },
                    onDismiss = { vm.onWidgetHintShown() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )
            }

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
                            text = stringResource(R.string.kural_of_the_moment),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = kural.kuralTa.replace("<br />", "\n"),
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Start,
                            lineHeight = 28.sp,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        LanguageAwareLabel(
                            ta = "— ${kural.adikaramTa}",
                            en = "— ${kural.adikaramEn}",
                            tl = kural.adikaramTl,
                            primaryStyle = MaterialTheme.typography.labelMedium,
                            secondaryStyle = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Start,
                            modifier = Modifier.fillMaxWidth()
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
                Icon(
                    Icons.Rounded.Search,
                    contentDescription = stringResource(R.string.browse_kurals)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    stringResource(R.string.browse_kurals),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/**
 * First-time call-to-action explaining that Thirukkural can be added as a Home Screen widget.
 * Shown once (tracked via [com.kulothunganug.thirukkural.datastore.WidgetHintSettings]) and
 * dismissible either by acting on it or explicitly closing it.
 */
@Composable
private fun WidgetHintCard(
    onAddWidget: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors().copy(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                Icon(
                    Icons.AutoMirrored.Filled.AddToHomeScreen,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.add_widget_hint_title),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.add_widget_hint_description),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = stringResource(R.string.close)
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onAddWidget) {
                    Text(stringResource(R.string.add_widget_to_home_screen))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WidgetInfoBottomSheet(
    onDismissRequest: () -> Unit,
    onAddWidget: () -> Unit,
    onOpenWidgetSettings: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.widget_info_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(
                text = stringResource(R.string.widget_info_description),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            // Action Button
            Button(
                onClick = onAddWidget,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.AddToHomeScreen,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.add_widget_to_home_screen),
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onOpenWidgetSettings,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Colorize,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.widget_customization_title),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/**
 * Asks the launcher to pin the Thirukkural widget to the Home Screen, if the launcher supports it
 * (Android 8+ and most launchers, though support isn't guaranteed on every device).
 *
 * Unlike a manual drag-from-picker add, [AppWidgetManager.requestPinAppWidget] does NOT launch the
 * widget's `android:configure` entry on its own — the launcher hands control straight back to
 * us once pinning succeeds. Since the widget declares MainActivity as its configure activity,
 * the system defers its very first update until that activity finishes with RESULT_OK, so without
 * a [successCallback] that opens it ourselves, the widget would be pinned but never actually
 * rendered (stuck showing "Can't load widget"). The callback targets MainActivity with
 * ACTION_APPWIDGET_CONFIGURE so it opens directly on the in-app customization screen, and must
 * be mutable so the system can fill in [AppWidgetManager.EXTRA_APPWIDGET_ID].
 */
private fun requestPinThirukkuralWidget(context: Context) {
    val appWidgetManager = AppWidgetManager.getInstance(context)
    val provider = ComponentName(context, ThirukkuralWidgetReceiver::class.java)
    if (appWidgetManager.isRequestPinAppWidgetSupported) {
        val configureIntent = Intent(
            AppWidgetManager.ACTION_APPWIDGET_CONFIGURE,
            null,
            context,
            MainActivity::class.java
        ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val successCallback = PendingIntent.getActivity(
            context,
            0,
            configureIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
        appWidgetManager.requestPinAppWidget(provider, null, successCallback)
    }
}
