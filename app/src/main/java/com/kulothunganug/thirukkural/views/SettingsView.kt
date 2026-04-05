package com.kulothunganug.thirukkural.views

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kulothunganug.thirukkural.viewmodels.SettingsViewModel
import androidx.core.graphics.toColorInt
import androidx.navigation.NavController
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

data class ElementSettingItem(
    val id: String,
    val label: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsView(navController: NavController, vm: SettingsViewModel) {
    val state by vm.uiState.collectAsState()
    val haptic = LocalHapticFeedback.current

    // Local state for reordering to provide immediate feedback
    var elements by remember(state.contentOrder) {
        mutableStateOf(
            state.contentOrder.split(",").map { id ->
                ElementSettingItem(
                    id = id,
                    label = when (id) {
                        "PAAL" -> "Section (Paal)"
                        "IYAL" -> "Chapter Group (Iyal)"
                        "ADHIGARAM" -> "Chapter (Adhigaram)"
                        "KURAL" -> "Kural Text"
                        "TRANSLITERATION" -> "Transliteration"
                        else -> id
                    }
                )
            }
        )
    }

    val lazyListState = rememberLazyListState()
    val reorderableState = rememberReorderableLazyListState(lazyListState) { from, to ->
        val fromIndex = from.index - 4
        val toIndex = to.index - 4
        if (fromIndex in elements.indices && toIndex in elements.indices) {
            elements = elements.toMutableList().apply {
                add(toIndex, removeAt(fromIndex))
            }
            haptic.performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Go back"
                    )
                }
            }, title = { Text("Widget Customization") })
        }
    ) { padding ->
        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                WidgetPreview(state)
            }

            item {
                ColorSettings(
                    "Background Color",
                    state.bgColor,
                    listOf("#FFFFFF", "#F5F5DC", "#E6F3FF", "#FFF0F5", "#000000")
                ) {
                    vm.updateBgColor(it)
                }
            }

            item {
                ColorSettings(
                    "Text Color",
                    state.textColor,
                    listOf("#000000", "#333333", "#555555", "#FFFFFF", "#FF5722")
                ) {
                    vm.updateTextColor(it)
                }
            }

            item { Text("Element Styling & Order", style = MaterialTheme.typography.titleMedium) }

            items(elements, key = { it.id }) { item ->
                ReorderableItem(reorderableState, key = item.id) { isDragging ->
                    val elevation by animateDpAsState(if (isDragging) 8.dp else 0.dp)

                    Surface(
                        shadowElevation = elevation,
                        shape = MaterialTheme.shapes.medium,
                        tonalElevation = if (isDragging) 4.dp else 0.dp
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.weight(1f)) {
                                when (item.id) {
                                    "PAAL" -> ElementSettingsCard(
                                        item.label,
                                        state.showPaal,
                                        state.paalSize,
                                        state.paalAlign,
                                        state.paalBold,
                                        onShowChange = { vm.updatePaalSettings(show = it) },
                                        onSizeChange = { vm.updatePaalSettings(size = it) },
                                        onAlignChange = { vm.updatePaalSettings(align = it) },
                                        onBoldChange = { vm.updatePaalSettings(bold = it) }
                                    )

                                    "IYAL" -> ElementSettingsCard(
                                        item.label,
                                        state.showIyal,
                                        state.iyalSize,
                                        state.iyalAlign,
                                        state.iyalBold,
                                        onShowChange = { vm.updateIyalSettings(show = it) },
                                        onSizeChange = { vm.updateIyalSettings(size = it) },
                                        onAlignChange = { vm.updateIyalSettings(align = it) },
                                        onBoldChange = { vm.updateIyalSettings(bold = it) }
                                    )

                                    "ADHIGARAM" -> ElementSettingsCard(
                                        item.label,
                                        state.showAdhigaram,
                                        state.adhigaramSize,
                                        state.adhigaramAlign,
                                        state.adhigaramBold,
                                        onShowChange = { vm.updateAdhigaramSettings(show = it) },
                                        onSizeChange = { vm.updateAdhigaramSettings(size = it) },
                                        onAlignChange = { vm.updateAdhigaramSettings(align = it) },
                                        onBoldChange = { vm.updateAdhigaramSettings(bold = it) }
                                    )

                                    "KURAL" -> ElementSettingsCard(
                                        item.label,
                                        state.showKural,
                                        state.kuralSize,
                                        state.kuralAlign,
                                        state.kuralBold,
                                        onShowChange = { vm.updateKuralSettings(show = it) },
                                        onSizeChange = { vm.updateKuralSettings(size = it) },
                                        onAlignChange = { vm.updateKuralSettings(align = it) },
                                        onBoldChange = { vm.updateKuralSettings(bold = it) }
                                    )

                                    "TRANSLITERATION" -> ElementSettingsCard(
                                        item.label,
                                        state.showTranslit,
                                        state.translitSize,
                                        state.translitAlign,
                                        state.translitBold,
                                        onShowChange = { vm.updateTranslitSettings(show = it) },
                                        onSizeChange = { vm.updateTranslitSettings(size = it) },
                                        onAlignChange = { vm.updateTranslitSettings(align = it) },
                                        onBoldChange = { vm.updateTranslitSettings(bold = it) }
                                    )
                                }
                            }

                            IconButton(
                                modifier = Modifier.draggableHandle(
                                    onDragStarted = {
                                        haptic.performHapticFeedback(
                                            HapticFeedbackType.GestureThresholdActivate
                                        )
                                    },
                                    onDragStopped = {
                                        haptic.performHapticFeedback(HapticFeedbackType.GestureEnd)
                                        vm.updateContentOrder(elements.joinToString(",") { it.id })
                                    }
                                ),
                                onClick = {}
                            ) {
                                Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Reorder")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WidgetPreview(state: com.kulothunganug.thirukkural.viewmodels.SettingsUiState) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 150.dp),
        colors = CardDefaults.cardColors(containerColor = Color(state.bgColor.toColorInt()))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            state.contentOrder.split(",").forEach { item ->
                when (item) {
                    "PAAL" -> if (state.showPaal) PreviewText(
                        "அறத்துப்பால்",
                        state.textColor,
                        state.paalSize,
                        state.paalAlign,
                        state.paalBold
                    )

                    "IYAL" -> if (state.showIyal) PreviewText(
                        "பாயிரவியல்",
                        state.textColor,
                        state.iyalSize,
                        state.iyalAlign,
                        state.iyalBold
                    )

                    "ADHIGARAM" -> if (state.showAdhigaram) PreviewText(
                        "1 - கடவுள் வாழ்த்து",
                        state.textColor,
                        state.adhigaramSize,
                        state.adhigaramAlign,
                        state.adhigaramBold
                    )

                    "KURAL" -> if (state.showKural) PreviewText(
                        "அகர முதல எழுத்தெல்லாம் ஆதி\nபகவன் முதற்றே உலகு.",
                        state.textColor,
                        state.kuralSize,
                        state.kuralAlign,
                        state.kuralBold
                    )

                    "TRANSLITERATION" -> if (state.showTranslit) PreviewText(
                        "Akara Muthala Ezhuthellam Aadhi\nPakavan Muthatre Ulaku.",
                        state.textColor,
                        state.translitSize,
                        state.translitAlign,
                        state.translitBold
                    )
                }
            }
        }
    }
}

@Composable
fun PreviewText(text: String, color: String, size: Int, align: String, bold: Boolean) {
    Text(
        text = text,
        color = Color(color.toColorInt()),
        fontSize = size.sp,
        fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
        textAlign = when (align) {
            "LEFT" -> TextAlign.Left
            "RIGHT" -> TextAlign.Right
            else -> TextAlign.Center
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 2.dp),
        lineHeight = (size + 4).sp
    )
}

@Composable
fun ColorSettings(
    label: String,
    selectedColor: String,
    colors: List<String>,
    onColorSelected: (String) -> Unit
) {
    Column {
        Text(label, style = MaterialTheme.typography.labelLarge)
        Row(
            modifier = Modifier.padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            colors.forEach { color ->
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(color.toColorInt()))
                        .clickable { onColorSelected(color) }
                        .then(
                            if (selectedColor == color) Modifier.border(
                                2.dp,
                                MaterialTheme.colorScheme.primary,
                                CircleShape
                            ) else Modifier
                        )
                )
            }
        }
    }
}

@Composable
fun ElementSettingsCard(
    label: String, show: Boolean, size: Int, align: String, bold: Boolean,
    onShowChange: (Boolean) -> Unit, onSizeChange: (Int) -> Unit,
    onAlignChange: (String) -> Unit, onBoldChange: (Boolean) -> Unit
) {
    Column(modifier = Modifier.padding(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = show, onCheckedChange = onShowChange)
            Text(label, style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.weight(1f))
            IconButton(onClick = { onBoldChange(!bold) }) {
                Text(
                    "B",
                    fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
                    color = if (bold) MaterialTheme.colorScheme.primary else LocalContentColor.current
                )
            }
        }
        if (show) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "Size: $size",
                    modifier = Modifier.width(60.dp),
                    style = MaterialTheme.typography.bodySmall
                )
                Slider(
                    value = size.toFloat(),
                    onValueChange = { onSizeChange(it.toInt()) },
                    valueRange = 10f..24f,
                    modifier = Modifier.weight(1f)
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("LEFT", "CENTER", "RIGHT").forEach {
                    FilterChip(
                        selected = align == it,
                        onClick = { onAlignChange(it) },
                        label = { Text(it, fontSize = 10.sp) }
                    )
                }
            }
        }
    }
}
