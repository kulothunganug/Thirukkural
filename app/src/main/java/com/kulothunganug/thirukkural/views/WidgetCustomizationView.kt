package com.kulothunganug.thirukkural.views

import android.app.Activity
import androidx.compose.animation.core.animateDpAsState
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.Colorize
import androidx.compose.material.icons.rounded.DragHandle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.graphics.toColorInt
import com.kulothunganug.thirukkural.R
import com.kulothunganug.thirukkural.shared_ui.CategoryFilterDialog
import com.kulothunganug.thirukkural.shared_ui.ColorChooserDialog
import com.kulothunganug.thirukkural.shared_ui.detachedItemShape
import com.kulothunganug.thirukkural.shared_ui.endItemShape
import com.kulothunganug.thirukkural.shared_ui.leadingItemShape
import com.kulothunganug.thirukkural.shared_ui.listItemColors
import com.kulothunganug.thirukkural.shared_ui.middleItemShape
import com.kulothunganug.thirukkural.viewmodels.WidgetCustomizationViewModel
import com.kulothunganug.thirukkural.widget.ContentType
import com.kulothunganug.thirukkural.widget.MAX_AUTO_REFRESH_INTERVAL_MINUTES
import com.kulothunganug.thirukkural.widget.MIN_AUTO_REFRESH_INTERVAL_MINUTES
import com.kulothunganug.thirukkural.widget.RefreshSource
import com.kulothunganug.thirukkural.widget.SectionConfig
import com.kulothunganug.thirukkural.widget.THIRUVALLUVAR_TA
import com.kulothunganug.thirukkural.widget.WidgetConfig
import com.kulothunganug.thirukkural.widget.WidgetTextAlign
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import kotlin.math.roundToInt


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WidgetCustomizationView(
    appWidgetId: Int,
    onDone: (Int) -> Unit,
) {
    val vm: WidgetCustomizationViewModel = koinViewModel(parameters = { parametersOf(appWidgetId) })

    val state by vm.uiState.collectAsState()
    val isLoading by vm.isLoading.collectAsState()
    val loadFailed by vm.loadFailed.collectAsState()
    val pals by vm.pals.collectAsState()
    val categoryIyals by vm.categoryIyals.collectAsState()
    val categoryAdikarams by vm.categoryAdikarams.collectAsState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(loadFailed) {
        if (loadFailed) onDone(Activity.RESULT_CANCELED)
    }

    val openBgColorChooser by vm.openBgColorChooser.collectAsState()
    val openRefreshColorChooser by vm.openRefreshColorChooser.collectAsState()
    val haptic = LocalHapticFeedback.current
    var editingSection by remember { mutableStateOf<SectionConfig?>(null) }
    var showCategoryFilterDialog by remember { mutableStateOf(false) }

    var reorderableSections by remember(state.contentOrder) {
        mutableStateOf(state.contentOrder)
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

    when {
        openBgColorChooser -> {
            ColorChooserDialog(
                stringResource(R.string.background_color),
                state.bgColor,
                onColorSelected = { vm.updateBgColor(it) },
                onDismissRequest = {
                    vm.toggleBgColorChooser(false)
                })
        }

        openRefreshColorChooser -> {
            ColorChooserDialog(
                stringResource(R.string.refresh_button_color),
                state.refreshButtonColor,
                onColorSelected = { vm.updateRefreshButtonColor(it) },
                onDismissRequest = {
                    vm.toggleRefreshColorChooser(false)
                })
        }

        editingSection != null -> {
            val currentSection = reorderableSections.find { it.type == editingSection!!.type }
                ?: editingSection!!
            ElementSettingsDialog(
                sectionConfig = currentSection,
                onDismissRequest = { editingSection = null },
                onSizeChange = { vm.updateSectionSettings(currentSection.type, size = it) },
                onAlignChange = { vm.updateSectionSettings(currentSection.type, align = it) },
                onBoldChange = { vm.updateSectionSettings(currentSection.type, bold = it) },
                onColorChange = {
                    vm.updateSectionSettings(currentSection.type, textColor = it)
                }
            )
        }

        showCategoryFilterDialog -> {
            CategoryFilterDialog(
                title = stringResource(R.string.select_categories),
                pals = pals,
                iyals = categoryIyals,
                adikarams = categoryAdikarams,
                selectedPals = state.refreshCategoryPals,
                selectedIyals = state.refreshCategoryIyals,
                selectedAdikarams = state.refreshCategoryAdikarams,
                onTogglePal = { vm.toggleRefreshCategoryPal(it) },
                onToggleIyal = { vm.toggleRefreshCategoryIyal(it) },
                onToggleAdikaram = { vm.toggleRefreshCategoryAdikaram(it) },
                onDismissRequest = { showCategoryFilterDialog = false }
            )
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = {
                        onDone(Activity.RESULT_CANCELED)
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.go_back)
                        )
                    }
                },
                title = { Text(stringResource(R.string.widget_customization_title)) },
                actions = {
                    IconButton(
                        enabled = !isLoading,
                        onClick = {
                            scope.launch {
                                if (vm.saveSettings()) {
                                    onDone(Activity.RESULT_OK)
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = stringResource(R.string.save_changes)
                        )
                    }
                }
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
        ) {
            item {
                WidgetPreview(state)
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                Text(
                    stringResource(R.string.colors),
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
                            ) {
                                Icon(
                                    Icons.Outlined.Colorize,
                                    contentDescription = stringResource(R.string.pick_color),
                                )
                            }
                        },
                        headlineContent = { Text(stringResource(R.string.background_color)) },
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Surface(
                    shape = endItemShape(),
                    tonalElevation = 2.dp
                ) {
                    ListItem(
                        colors = listItemColors(),
                        trailingContent = {
                            IconButton(
                                onClick = { vm.toggleRefreshColorChooser(true) },
                            ) {
                                Icon(
                                    Icons.Outlined.Colorize,
                                    contentDescription = stringResource(R.string.pick_color),
                                )
                            }
                        },
                        headlineContent = { Text(stringResource(R.string.refresh_button_color)) },
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                Text(
                    stringResource(R.string.auto_refresh),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            item {
                AutoRefreshSection(
                    state = state,
                    onEnabledChange = { vm.updateAutoRefreshEnabled(it) },
                    onIntervalChange = { vm.updateAutoRefreshInterval(it) },
                    onSourceChange = { vm.updateRefreshSource(it) },
                    onOpenCategoryDialog = { showCategoryFilterDialog = true }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                Text(
                    stringResource(R.string.element_styling_order),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            itemsIndexed(
                reorderableSections,
                key = { _, it -> it.type.name }) { index, sectionConfig ->
                ReorderableItem(reorderableState, key = sectionConfig.type.name) { isDragging ->
                    val elevation by animateDpAsState(
                        if (isDragging) 8.dp else 0.dp,
                        label = "elevation"
                    )

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
                                        contentDescription = stringResource(R.string.reorder)
                                    )
                                }
                            },
                            headlineContent = {
                                Text(
                                    contentTypeLabel(sectionConfig.type),
                                )
                            },
                            trailingContent = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = { editingSection = sectionConfig },
                                        enabled = sectionConfig.show
                                    ) {
                                        Icon(
                                            Icons.Default.Edit,
                                            contentDescription = stringResource(R.string.edit_settings)
                                        )
                                    }
                                    Switch(
                                        checked = sectionConfig.show,
                                        onCheckedChange = {
                                            vm.updateSectionSettings(
                                                sectionConfig.type,
                                                show = it
                                            )
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AutoRefreshSection(
    state: WidgetConfig,
    onEnabledChange: (Boolean) -> Unit,
    onIntervalChange: (Int) -> Unit,
    onSourceChange: (RefreshSource) -> Unit,
    onOpenCategoryDialog: () -> Unit,
) {
    val rows = buildList {
        add("enable")
        if (state.autoRefreshEnabled) {
            add("interval")
            add("source")
            if (state.refreshSource == RefreshSource.Category) add("categories")
        }
    }

    rows.forEachIndexed { index, row ->
        val shape = when {
            rows.size == 1 -> detachedItemShape()
            index == 0 -> leadingItemShape()
            index == rows.size - 1 -> endItemShape()
            else -> middleItemShape()
        }

        Surface(shape = shape, tonalElevation = 2.dp) {
            when (row) {
                "enable" -> ListItem(
                    colors = listItemColors(),
                    headlineContent = { Text(stringResource(R.string.auto_refresh)) },
                    trailingContent = {
                        Switch(
                            checked = state.autoRefreshEnabled,
                            onCheckedChange = onEnabledChange
                        )
                    }
                )

                "interval" -> ListItem(
                    colors = listItemColors(),
                    headlineContent = { Text(stringResource(R.string.refresh_interval)) },
                    supportingContent = {
                        Column {
                            Text(
                                formatIntervalMinutes(state.autoRefreshIntervalMinutes),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Slider(
                                value = state.autoRefreshIntervalMinutes.toFloat(),
                                onValueChange = { onIntervalChange(it.roundToInt()) },
                                valueRange = MIN_AUTO_REFRESH_INTERVAL_MINUTES.toFloat()..
                                        MAX_AUTO_REFRESH_INTERVAL_MINUTES.toFloat(),
                                steps = (MAX_AUTO_REFRESH_INTERVAL_MINUTES - MIN_AUTO_REFRESH_INTERVAL_MINUTES) /
                                        MIN_AUTO_REFRESH_INTERVAL_MINUTES - 1
                            )
                        }
                    }
                )

                "source" -> ListItem(
                    colors = listItemColors(),
                    headlineContent = { Text(stringResource(R.string.refresh_source)) },
                    trailingContent = {
                        RefreshSourceDropdown(
                            selected = state.refreshSource,
                            onSelected = onSourceChange
                        )
                    }
                )

                "categories" -> ListItem(
                    colors = listItemColors(),
                    headlineContent = { Text(stringResource(R.string.select_categories)) },
                    supportingContent = { Text(categorySummary(state)) },
                    trailingContent = {
                        IconButton(onClick = onOpenCategoryDialog) {
                            Icon(
                                Icons.Default.FilterList,
                                contentDescription = stringResource(R.string.select_categories)
                            )
                        }
                    },
                    modifier = Modifier.clickable(onClick = onOpenCategoryDialog)
                )
            }
        }
        Spacer(modifier = Modifier.height(2.dp))
    }
}

@Composable
private fun formatIntervalMinutes(minutes: Int): String {
    val hours = minutes / 60
    val mins = minutes % 60
    return when {
        hours == 0 -> stringResource(R.string.duration_minutes, mins)
        mins == 0 -> stringResource(R.string.duration_hours, hours)
        else -> stringResource(R.string.duration_hours_minutes, hours, mins)
    }
}

@Composable
private fun categorySummary(state: WidgetConfig): String {
    val segments = buildList {
        state.refreshCategoryPals.size.takeIf { it > 0 }?.let {
            add(stringResource(R.string.category_selection_count, it, stringResource(R.string.pal)))
        }
        state.refreshCategoryIyals.size.takeIf { it > 0 }?.let {
            add(stringResource(R.string.category_selection_count, it, stringResource(R.string.iyal)))
        }
        state.refreshCategoryAdikarams.size.takeIf { it > 0 }?.let {
            add(
                stringResource(
                    R.string.category_selection_count,
                    it,
                    stringResource(R.string.adikaram)
                )
            )
        }
    }

    return if (segments.isEmpty()) {
        stringResource(R.string.select_label, stringResource(R.string.pal))
    } else {
        stringResource(R.string.category_selection_summary, segments.joinToString(", "))
    }
}

@Composable
private fun refreshSourceLabel(source: RefreshSource): String = when (source) {
    RefreshSource.All -> stringResource(R.string.refresh_source_all)
    RefreshSource.Category -> stringResource(R.string.refresh_source_category)
    RefreshSource.Favourites -> stringResource(R.string.refresh_source_favourites)
}

@Composable
private fun RefreshSourceDropdown(
    selected: RefreshSource,
    onSelected: (RefreshSource) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { expanded = true }
        ) {
            Text(refreshSourceLabel(selected), color = MaterialTheme.colorScheme.primary)
            Icon(
                Icons.Default.ArrowDropDown,
                contentDescription = stringResource(R.string.refresh_source),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            RefreshSource.entries.forEach { source ->
                DropdownMenuItem(
                    text = { Text(refreshSourceLabel(source)) },
                    onClick = {
                        onSelected(source)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun WidgetPreview(state: WidgetConfig) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        colors = CardDefaults.cardColors(containerColor = Color(state.bgColor.toColorInt()))
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                tint = Color(state.refreshButtonColor.toColorInt()),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(12.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                state.contentOrder.filter { it.show }.forEach { section ->
                    val text = when (section.type) {
                        ContentType.Paal -> "அறத்துப்பால்"
                        ContentType.Iyal -> "பாயிரவியல்"
                        ContentType.Adhigaram -> "கடவுள் வாழ்த்து"
                        ContentType.Kural -> "அகர முதல எழுத்தெல்லாம் ஆதி\nபகவன் முதற்றே உலகு."
                        ContentType.Transliteration -> "Akara Mudhala Ezhuththellam Aadhi..."
                        ContentType.Thiruvalluvar -> THIRUVALLUVAR_TA
                    }
                    PreviewText(
                        text,
                        section.textColor,
                        section.size,
                        section.align,
                        section.bold
                    )
                }
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
    onBoldChange: (Boolean) -> Unit,
    onColorChange: (String) -> Unit
) {
    var showColorPicker by remember { mutableStateOf(false) }
    val sectionLabel = contentTypeLabel(sectionConfig.type)

    if (showColorPicker) {
        ColorChooserDialog(
            label = stringResource(R.string.section_settings, sectionLabel),
            initialColor = sectionConfig.textColor,
            onColorSelected = { onColorChange(it) },
            onDismissRequest = { showColorPicker = false }
        )
    }

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
                    text = stringResource(R.string.section_settings, sectionLabel),
                    style = MaterialTheme.typography.headlineSmall,
                )

                // Color Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(stringResource(R.string.color))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { showColorPicker = true },
                        ) {
                            Icon(
                                Icons.Outlined.Colorize,
                                contentDescription = stringResource(R.string.pick_color),
                            )
                        }
                    }
                }

                // Bold row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(stringResource(R.string.bold_text))
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
                        Text(stringResource(R.string.text_size))
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
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(stringResource(R.string.alignment))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        WidgetTextAlign.entries.forEach {
                            FilterChip(
                                selected = sectionConfig.align == it,
                                onClick = { onAlignChange(it) },
                                label = { Text(widgetTextAlignLabel(it)) }
                            )
                        }
                    }
                }

                TextButton(
                    onClick = onDismissRequest,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(stringResource(R.string.close))
                }
            }
        }
    }
}

@Composable
private fun contentTypeLabel(type: ContentType): String = when (type) {
    ContentType.Paal -> stringResource(R.string.pal)
    ContentType.Iyal -> stringResource(R.string.iyal)
    ContentType.Adhigaram -> stringResource(R.string.adikaram)
    ContentType.Kural -> stringResource(R.string.tamil_kural)
    ContentType.Transliteration -> stringResource(R.string.transliteration)
    ContentType.Thiruvalluvar -> stringResource(R.string.thiruvalluvar)
}

@Composable
private fun widgetTextAlignLabel(align: WidgetTextAlign): String = when (align) {
    WidgetTextAlign.Start -> stringResource(R.string.align_start)
    WidgetTextAlign.Center -> stringResource(R.string.align_center)
    WidgetTextAlign.End -> stringResource(R.string.align_end)
}
