package com.kulothunganug.thirukkural.views

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.rounded.DragHandle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.kulothunganug.thirukkural.R
import com.kulothunganug.thirukkural.models.ThirukkuralModel
import com.kulothunganug.thirukkural.shared_ui.endItemShape
import com.kulothunganug.thirukkural.shared_ui.leadingItemShape
import com.kulothunganug.thirukkural.shared_ui.listItemColors
import com.kulothunganug.thirukkural.shared_ui.middleItemShape
import com.kulothunganug.thirukkural.viewmodels.KuralDetailUiState
import com.kulothunganug.thirukkural.viewmodels.KuralDetailViewModel
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KuralDetailView(
    kuralId: Int,
    vm: KuralDetailViewModel,
    navController: NavController
) {
    val uiState by vm.uiState.collectAsState()
    val context = LocalContext.current
    var showShareSheet by remember { mutableStateOf(false) }

    LaunchedEffect(kuralId) {
        vm.loadKural(kuralId)
    }

    val foundState = uiState as? KuralDetailUiState.Found
    val foundKural = foundState?.kural
    val isFavourite = foundState?.isFavourite ?: false

    if (showShareSheet && foundKural != null) {
        KuralShareSheet(
            kural = foundKural,
            vm = vm,
            onDismiss = { showShareSheet = false }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.go_back)
                        )
                    }
                },
                actions = {
                    if (foundKural != null) {
                        IconButton(onClick = { vm.toggleFavourite() }) {
                            Icon(
                                imageVector = if (isFavourite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                contentDescription = stringResource(
                                    if (isFavourite) R.string.remove_from_favourites
                                    else R.string.add_to_favourites
                                ),
                                tint = if (isFavourite) MaterialTheme.colorScheme.error
                                else LocalContentColor.current
                            )
                        }
                        IconButton(onClick = {
                            showShareSheet = true
                        }) {
                            Icon(
                                Icons.Filled.Share,
                                contentDescription = stringResource(R.string.share)
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        when (uiState) {
            is KuralDetailUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is KuralDetailUiState.NotFound -> {
                Box(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.kural_not_found),
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            is KuralDetailUiState.Found -> {
                val k = (uiState as KuralDetailUiState.Found).kural
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .padding(16.dp)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    SelectionContainer {
                    Text(
                        text = k.kuralTa.replace("<br />", "\n"),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 28.sp,
                        maxLines = 2,
                        autoSize = TextAutoSize.StepBased(10.sp, 20.sp, 1.sp),
                    )
                }
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        InfoCard(
                            label = stringResource(R.string.kural_no),
                            value = k.id.toString(),
                            modifier = Modifier.weight(1f)
                        )
                        InfoCard(
                            label = stringResource(R.string.part),
                            value = k.palTa,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        InfoCard(
                            label = stringResource(R.string.section),
                            value = k.iyalTa,
                            modifier = Modifier.weight(1f)
                        )
                        InfoCard(
                            label = stringResource(R.string.chapter),
                            value = k.adikaramTa,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    DetailItem(label = stringResource(R.string.explanation), value = k.explanationTa)
                    Spacer(modifier = Modifier.height(8.dp))
                    DetailItem(
                        label = stringResource(R.string.transliteration),
                        value = k.kuralTl.replace("<br />", "\n")
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    DetailItem(label = stringResource(R.string.couplet), value = k.couplet)
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        stringResource(R.string.commentators),
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    CollapsibleExplanation(
                        label = stringResource(R.string.mu_varadarasanar),
                        value = k.commentaryMuVaratharasanar
                    )
                    CollapsibleExplanation(
                        label = stringResource(R.string.parimelazhagar),
                        value = k.commentaryParimelazhagar
                    )
                    CollapsibleExplanation(
                        label = stringResource(R.string.solomon_pappaiah),
                        value = k.commentarySalamanPappaiya
                    )
                    CollapsibleExplanation(
                        label = stringResource(R.string.manakkudavar),
                        value = k.commentaryManakudavar
                    )
                    CollapsibleExplanation(
                        label = stringResource(R.string.munusami),
                        value = k.commentaryMunusami
                    )
                    CollapsibleExplanation(
                        label = stringResource(R.string.karunanidhi),
                        value = k.commentaryKarunanidhi
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KuralShareSheet(
    kural: ThirukkuralModel,
    vm: KuralDetailViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val config by vm.shareConfig.collectAsState()

    val explanationLabel = stringResource(R.string.explanation)
    val coupletLabel = stringResource(R.string.couplet)
    val sentFromAppLabel = stringResource(R.string.sent_from_app)

    val sharedText = remember(
        config,
        explanationLabel,
        coupletLabel,
        sentFromAppLabel
    ) {
        buildString {
            config.order.forEach { sectionId ->
                if (config.enabledSections.contains(sectionId)) {
                    when (sectionId) {
                        "header" -> {
                            append("Thirukkural #${kural.id}\n")
                            append("${kural.palTa} | ${kural.iyalTa} | ${kural.adikaramTa}\n\n")
                        }
                        "kural" -> {
                            append(kural.kuralTa.replace("<br />", "\n"))
                            append("\n\n")
                        }
                        "transliteration" -> {
                            append(kural.kuralTl.replace("<br />", "\n"))
                            append("\n\n")
                        }
                        "explanation" -> {
                            append("$explanationLabel:\n")
                            append(kural.explanationTa)
                            append("\n\n")
                        }
                        "translation" -> {
                            append("$coupletLabel:\n")
                            append(kural.couplet)
                            append("\n\n")
                        }
                    }
                }
            }
            append("- $sentFromAppLabel")
        }.trim()
    }

    val lazyListState = rememberLazyListState()
    val reorderableState = rememberReorderableLazyListState(lazyListState) { from, to ->
        val newOrder = config.order.toMutableList().apply {
            val fromIndex = indexOfFirst { it == from.key }
            val toIndex = indexOfFirst { it == to.key }
            if (fromIndex != -1 && toIndex != -1) {
                add(toIndex, removeAt(fromIndex))
            }
        }
        vm.updateShareConfig(config.copy(order = newOrder))
        haptic.performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            item {
                Text(
                    text = stringResource(R.string.share_kural),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            /*
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .animateContentSize(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Text(
                        text = sharedText,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            */

            itemsIndexed(config.order, key = { _, id -> id }) { index, sectionId ->
                val labelRes = when (sectionId) {
                    "header" -> R.string.include_header
                    "kural" -> R.string.tamil_kural
                    "transliteration" -> R.string.transliteration
                    "explanation" -> R.string.explanation
                    "translation" -> R.string.couplet
                    else -> R.string.app_name
                }
                val enabled = config.enabledSections.contains(sectionId)

                ReorderableItem(reorderableState, key = sectionId) { isDragging ->
                    val elevation by animateDpAsState(
                        if (isDragging) 8.dp else 0.dp,
                        label = "elevation"
                    )

                    val shape = when (index) {
                        0 -> leadingItemShape()
                        config.order.size - 1 -> endItemShape()
                        else -> middleItemShape()
                    }

                    Surface(
                        shape = shape,
                        tonalElevation = 2.dp,
                        shadowElevation = elevation,
                    ) {
                        ListItem(
                            colors = listItemColors(),
                            leadingContent = {
                                IconButton(
                                    modifier = Modifier.draggableHandle(
                                        onDragStarted = {
                                            haptic.performHapticFeedback(
                                                HapticFeedbackType.GestureThresholdActivate
                                            )
                                        },
                                        onDragStopped = {
                                            haptic.performHapticFeedback(HapticFeedbackType.GestureEnd)
                                        }
                                    ),
                                    onClick = {}
                                ) {
                                    Icon(
                                        Icons.Rounded.DragHandle,
                                        contentDescription = stringResource(R.string.reorder)
                                    )
                                }
                            },
                            headlineContent = { Text(stringResource(labelRes)) },
                            trailingContent = {
                                Switch(
                                    checked = enabled,
                                    onCheckedChange = { isEnabled ->
                                        val newEnabled = config.enabledSections.toMutableSet().apply {
                                            if (isEnabled) add(sectionId) else remove(sectionId)
                                        }
                                        vm.updateShareConfig(config.copy(enabledSections = newEnabled))
                                    }
                                )
                            }
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))

                val shareTitle = stringResource(R.string.share_kural)
                ElevatedButton(
                    onClick = {
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, sharedText)
                        }
                        context.startActivity(Intent.createChooser(intent, shareTitle))
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Filled.Share, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.share))
                }
            }
        }
    }
}

@Composable
fun DetailItem(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(), colors = CardDefaults.cardColors().copy(
            containerColor =
                MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
            )
            SelectionContainer {
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
fun InfoCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors().copy(
            containerColor =
                MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            SelectionContainer {

                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
fun CollapsibleExplanation(label: String, value: String, modifier: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(false) }
    val arrowRotation by animateFloatAsState(
        targetValue = if (expanded) 90f else 0f,
        label = "accordion-arrow"
    )

    Surface(
        color = MaterialTheme.colorScheme.surfaceContainer,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier
            .fillMaxWidth()
            .padding(4.dp)
            // clip the ripple effect
            .clip(RoundedCornerShape(12.dp))
            .clickable { expanded = !expanded }
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            // accordion header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    modifier = Modifier.rotate(arrowRotation)
                )
            }

            // accordion content
            Box {
                // accordion content - collapsed item
                this@Column.AnimatedVisibility(
                    visible = !expanded,
                    // only used fadeIn and fadeOut animation for the collapsed item
                    enter = fadeIn(animationSpec = tween(durationMillis = 0)),
                    exit = fadeOut(animationSpec = tween())
                ) {
                    Box {
                        Text(
                            text = value,
                            minLines = 2,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                        // overlay above the text
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .height(32.dp)
                                .fillMaxWidth()
                                .background(
                                    // gradient color from top to bottom
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            MaterialTheme.colorScheme.surfaceContainer
                                        )
                                    )
                                )
                        )
                    }
                }
                // accordion content - expanded item
                this@Column.AnimatedVisibility(
                    visible = expanded,
                    // animate expand vertically from the top when expanded
                    enter = expandVertically(
                        expandFrom = Alignment.Top,
                        animationSpec = tween()
                    ),
                    // animate shrink vertically to the top when collapsed + fade out
                    exit = shrinkVertically(
                        shrinkTowards = Alignment.Top,
                        animationSpec = tween()
                    ) + fadeOut()
                ) {
                    SelectionContainer {
                        Text(
                            text = value,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}
