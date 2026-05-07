package com.kulothunganug.thirukkural.views

import android.app.Activity
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Colorize
import androidx.compose.material.icons.rounded.DragHandle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.graphics.toColorInt
import com.github.skydoves.colorpicker.compose.AlphaSlider
import com.github.skydoves.colorpicker.compose.AlphaTile
import com.github.skydoves.colorpicker.compose.BrightnessSlider
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import com.kulothunganug.thirukkural.shared_ui.endItemShape
import com.kulothunganug.thirukkural.shared_ui.leadingItemShape
import com.kulothunganug.thirukkural.shared_ui.listItemColors
import com.kulothunganug.thirukkural.shared_ui.middleItemShape
import com.kulothunganug.thirukkural.ui.theme.ThirukkuralTheme
import com.kulothunganug.thirukkural.viewmodels.SettingsUiState
import com.kulothunganug.thirukkural.viewmodels.WidgetConfigurationViewModel
import com.kulothunganug.thirukkural.widget.ContentType
import com.kulothunganug.thirukkural.widget.SectionConfig
import com.kulothunganug.thirukkural.widget.WidgetTextAlign
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@Composable
fun ColorChooserDialog(
    label: String,
    initialColor: String,
    onColorSelected: (String) -> Unit,
    onDismissRequest: () -> Unit
) {

    val controller = rememberColorPickerController()
    var hexCode by remember { mutableStateOf("") }
    var textColor by remember { mutableStateOf(Color.Transparent) }

    LaunchedEffect(initialColor) {
        controller.selectByColor(Color(initialColor.toColorInt()), false)
    }

    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 16.dp, bottom = 12.dp)
            ) {
                Text(
                    label,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium
                )

                HsvColorPicker(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .padding(10.dp),
                    controller = controller,
                    onColorChanged = { colorEnvelope: ColorEnvelope ->
                        hexCode = colorEnvelope.hexCode
                        textColor = colorEnvelope.color
                    }
                )
                AlphaSlider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(35.dp),
                    controller = controller,
                )
                BrightnessSlider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(35.dp),
                    controller = controller,
                )

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                    AlphaTile(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(6.dp)),
                        controller = controller,
                    )
                    Text(
                        "#$hexCode",
                        color = textColor,

                        )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.align(Alignment.End)
                ) {
                    OutlinedButton(onClick = { onDismissRequest() }) { Text("Cancel") }
                    Button(onClick = { onColorSelected("#${hexCode}"); onDismissRequest() }) {
                        Text(
                            "Ok"
                        )
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WidgetConfigurationView(
    appWidgetId: Int,
    onDone: (Int) -> Unit,
) {
    val vm: WidgetConfigurationViewModel = koinViewModel(parameters = { parametersOf(appWidgetId) })

    val state by vm.uiState.collectAsState()

    val openBgColorChooser by vm.openBgColorChooser.collectAsState()
    val openTextColorChooser by vm.openTextColorChooser.collectAsState()
    val haptic = LocalHapticFeedback.current
    var editingSection by remember { mutableStateOf<SectionConfig?>(null) }

    var reorderableSections by remember(state.config.contentOrder) {
        mutableStateOf(state.config.contentOrder)
    }

    val lazyListState = rememberLazyListState()
    val reorderableState = rememberReorderableLazyListState(lazyListState) { from, to ->
        val fromIndex = reorderableSections.indexOfFirst { it.type.name == from.key }
        val toIndex = reorderableSections.indexOfFirst { it.type.name == to.key }

        if (fromIndex != -1 && toIndex != -1) {
            reorderableSections = reorderableSections.toMutableList().apply {
                add(toIndex, removeAt(fromIndex))
            }
            haptic.performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
        }
    }

    ThirukkuralTheme {
        when {
            openBgColorChooser -> {
                ColorChooserDialog(
                    "Background Color",
                    state.config.bgColor,
                    onColorSelected = { vm.updateBgColor(it) },
                    onDismissRequest = {
                        vm.toggleBgColorChooser(false)
                    })
            }
            openTextColorChooser -> {
                ColorChooserDialog(
                    "Text Color",
                    state.config.textColor,
                    onColorSelected = { vm.updateTextColor(it) },
                    onDismissRequest = {
                        vm.toggleTextColorChooser(false)
                    })
            }
            editingSection != null -> {
                val currentSection = reorderableSections.find { it.type == editingSection!!.type } ?: editingSection!!
                ElementSettingsDialog(
                    sectionConfig = currentSection,
                    onDismissRequest = { editingSection = null },
                    onSizeChange = { vm.updateSectionSettings(currentSection.type, size = it) },
                    onAlignChange = { vm.updateSectionSettings(currentSection.type, align = it) },
                    onBoldChange = { vm.updateSectionSettings(currentSection.type, bold = it) }
                )
            }
        }
        Scaffold(
            topBar = {
                TopAppBar(navigationIcon = {
                    IconButton(onClick = {
                        onDone(Activity.RESULT_CANCELED)
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Go back"
                        )
                    }
                }, title = { Text("Widget Customization") })
            },
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    onClick = {
                        vm.saveSettings()
                        onDone(Activity.RESULT_OK)
                    },
                    icon = { Icon(Icons.Default.Check, contentDescription = null) },
                    text = { Text("Save Changes") }
                )
            }
        ) { padding ->
            LazyColumn(
                state = lazyListState,
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
            ) {
                item {
                    WidgetPreview(state, reorderableSections)
                    Spacer(modifier = Modifier.height(24.dp))
                }

                item {
                    Text(
                        "Colors",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                item {
                    Surface(
                        shape = leadingItemShape(),
                        tonalElevation = 2.dp
                    ) {
                        ListItem(
                            colors = listItemColors(),
                            trailingContent = {
                                IconButton(
                                    onClick = { vm.toggleBgColorChooser(true) },
                                    modifier = Modifier.background(
                                        Color(state.config.bgColor.toColorInt()),
                                        CircleShape
                                    )
                                ) {
                                    Icon(
                                        Icons.Outlined.Colorize,
                                        contentDescription = "Pick color"
                                    )
                                }
                            },
                            headlineContent = { Text("Background Color") },
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))

                }
                item {
                    Surface(
                        shape = endItemShape(),
                        tonalElevation = 2.dp
                    ) {
                        ListItem(
                            colors = listItemColors(),
                            trailingContent = {
                                IconButton(
                                    onClick = { vm.toggleTextColorChooser(true) },
                                    modifier = Modifier.background(
                                        Color(state.config.textColor.toColorInt()),
                                        CircleShape
                                    )

                                ) {
                                    Icon(
                                        Icons.Outlined.Colorize,
                                        contentDescription = "Pick color"
                                    )
                                }
                            },
                            headlineContent = { Text("Text Color") },
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }

                item {
                    Text(
                        "Element Styling & Order",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                itemsIndexed(reorderableSections, key = { _, it -> it.type.name }) { index, sectionConfig ->
                    ReorderableItem(reorderableState, key = sectionConfig.type.name) { isDragging ->
                        val elevation by animateDpAsState(if (isDragging) 8.dp else 0.dp, label = "elevation")

                        val cardShape = when (index) {
                            0 -> leadingItemShape()
                            reorderableSections.size - 1 -> endItemShape()
                            else -> middleItemShape()
                        }

                        Surface(
                            shape = cardShape,
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
                                                vm.updateContentOrder(reorderableSections)
                                            }
                                        ),
                                        onClick = {}
                                    ) {
                                        Icon(
                                            Icons.Rounded.DragHandle,
                                            contentDescription = "Reorder"
                                        )
                                    }
                                },
                                headlineContent = {
                                    Text(
                                        sectionConfig.type.name,
                                    )
                                },
                                trailingContent = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        IconButton(
                                            onClick = { editingSection = sectionConfig },
                                            enabled = sectionConfig.show
                                        ) {
                                            Icon(Icons.Default.Edit, contentDescription = "Edit settings")
                                        }
                                        Switch(
                                            checked = sectionConfig.show,
                                            onCheckedChange = {
                                                vm.updateSectionSettings(sectionConfig.type, show = it)
                                            }
                                        )
                                    }
                                }
                            )
                        }

                    }
                    Spacer(modifier = Modifier.height(2.dp))

                }
            }
        }
    }
}


@Composable
fun WidgetPreview(state: SettingsUiState, order: List<SectionConfig> = state.config.contentOrder) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 150.dp),
        colors = CardDefaults.cardColors(containerColor = Color(state.config.bgColor.toColorInt()))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            order.filter { it.show }.forEach { section ->
                val text = when (section.type) {
                    ContentType.Paal -> "அறத்துப்பால்"
                    ContentType.Iyal -> "பாயிரவியல்"
                    ContentType.Adhigaram -> "கடவுள் வாழ்த்து"
                    ContentType.Kural -> "அகர முதல எழுத்தெல்லாம் ஆதி\nபகவன் முதற்றே உலகு."
                    ContentType.Transliteration -> "Akara Mudhala Ezhuththellam Aadhi..."
                }
                PreviewText(
                    text,
                    state.config.textColor,
                    section.size,
                    section.align,
                    section.bold
                )
            }
        }
    }
}

@Composable
fun PreviewText(text: String, color: String, size: Int, align: WidgetTextAlign, bold: Boolean) {
    Text(
        text = text,
        color = Color(color.toColorInt()),
        fontSize = size.sp,
        fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
        textAlign = when (align) {
            WidgetTextAlign.Start -> TextAlign.Left
            WidgetTextAlign.End -> TextAlign.Right
            WidgetTextAlign.Center -> TextAlign.Center
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 2.dp),
        lineHeight = (size + 4).sp
    )
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ElementSettingsDialog(
    sectionConfig: SectionConfig,
    onDismissRequest: () -> Unit,
    onSizeChange: (Int) -> Unit,
    onAlignChange: (WidgetTextAlign) -> Unit,
    onBoldChange: (Boolean) -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "${sectionConfig.type.name} Settings",
                    style = MaterialTheme.typography.headlineSmall,
                )

                // Bold row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Bold Text", style = MaterialTheme.typography.bodyLarge)
                    Switch(
                        checked = sectionConfig.bold,
                        onCheckedChange = onBoldChange,
                    )
                }

                // Size row
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Text Size", style = MaterialTheme.typography.bodyLarge)
                        Text(
                            "${sectionConfig.size}sp",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Slider(
                        value = sectionConfig.size.toFloat(),
                        onValueChange = { onSizeChange(it.toInt()) },
                        valueRange = 10f..24f,
                    )
                }

                // Align row
                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Alignment", style = MaterialTheme.typography.bodyLarge)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        WidgetTextAlign.entries.forEach {
                            FilterChip(
                                selected = sectionConfig.align == it,
                                onClick = { onAlignChange(it) },
                                label = { Text(it.name) }
                            )
                        }
                    }
                }

                TextButton(
                    onClick = onDismissRequest,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Close")
                }
            }
        }
    }
}
