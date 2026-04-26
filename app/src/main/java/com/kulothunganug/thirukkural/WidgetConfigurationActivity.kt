package com.kulothunganug.thirukkural

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.core.graphics.toColorInt
import com.github.skydoves.colorpicker.compose.AlphaSlider
import com.github.skydoves.colorpicker.compose.AlphaTile
import com.github.skydoves.colorpicker.compose.BrightnessSlider
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import com.kulothunganug.thirukkural.ui.theme.ThirukkuralTheme
import com.kulothunganug.thirukkural.viewmodels.SettingsUiState
import com.kulothunganug.thirukkural.viewmodels.WidgetConfigurationViewModel
import com.kulothunganug.thirukkural.views.ElementSettingItem
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState


class WidgetConfigurationActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val appWidgetId = intent?.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID

        setContent {
            WidgetConfiguration(
                appWidgetId,
                onDone = { code ->
                    val resultValue = Intent().putExtra(
                        AppWidgetManager.EXTRA_APPWIDGET_ID,
                        appWidgetId
                    )
                    setResult(code, resultValue)
                    finish()
                }
            )
        }
    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WidgetConfiguration(
    appWidgetId: Int,
    onDone: (Int) -> Unit,
) {
    val vm: WidgetConfigurationViewModel = koinViewModel(parameters = { parametersOf(appWidgetId) })

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

    ThirukkuralTheme {
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
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    WidgetPreview(state)
                }

                item {
                    ColorSettings("Background Color", state.bgColor) {
                        vm.updateBgColor(it)
                    }
                }

                item {
                    ColorSettings("Text Color", state.textColor) {
                        vm.updateTextColor(it)
                    }
                }

                item {
                    Text(
                        "Element Styling & Order",
                        style = MaterialTheme.typography.titleMedium
                    )
                }

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
                                            "LEFT",
                                            state.kuralBold,
                                            onShowChange = { vm.updateKuralSettings(show = it) },
                                            onSizeChange = { vm.updateKuralSettings(size = it) },
                                            onAlignChange = { vm.updateKuralSettings(align = it) },
                                            onBoldChange = { vm.updateKuralSettings(bold = it) }
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
                                    Icon(
                                        Icons.AutoMirrored.Filled.List,
                                        contentDescription = "Reorder"
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun WidgetPreview(state: SettingsUiState) {
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
                        "கடவுள் வாழ்த்து",
                        state.textColor,
                        state.adhigaramSize,
                        state.adhigaramAlign,
                        state.adhigaramBold
                    )

                    "KURAL" -> if (state.showKural) PreviewText(
                        "அகர முதல எழுத்தெல்லாம் ஆதி\nபகவன் முதற்றே உலகு.",
                        state.textColor,
                        state.kuralSize,
                        "LEFT",
                        state.kuralBold
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
    initialColor: String,
    onColorSelected: (String) -> Unit
) {
    Column {
        Text(label, style = MaterialTheme.typography.labelLarge)

        val controller = rememberColorPickerController()
        var hexCode by remember { mutableStateOf("") }
        var textColor by remember { mutableStateOf(Color.Transparent) }

        LaunchedEffect(initialColor) {
            controller.selectByColor(Color(initialColor.toColorInt()), false)
        }

        HsvColorPicker(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .padding(10.dp),
            controller = controller,
            onColorChanged = { colorEnvelope: ColorEnvelope ->
                hexCode = colorEnvelope.hexCode
                textColor = colorEnvelope.color
                if (colorEnvelope.fromUser) {
                    onColorSelected("#${colorEnvelope.hexCode}")
                }
            }
        )
        AlphaSlider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .height(35.dp),
            controller = controller,
        )
        BrightnessSlider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .height(35.dp),
            controller = controller,
        )
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
