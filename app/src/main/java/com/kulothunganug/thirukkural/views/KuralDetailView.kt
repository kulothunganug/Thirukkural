package com.kulothunganug.thirukkural.views

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.kulothunganug.thirukkural.R
import com.kulothunganug.thirukkural.datastore.SecondaryScript
import com.kulothunganug.thirukkural.datastore.coerced
import com.kulothunganug.thirukkural.datastore.text
import com.kulothunganug.thirukkural.models.ThirukkuralModel
import com.kulothunganug.thirukkural.shared_ui.LanguageAwareLabel
import com.kulothunganug.thirukkural.viewmodels.KuralDetailUiState
import com.kulothunganug.thirukkural.viewmodels.KuralDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KuralDetailView(
    kuralId: Int,
    vm: KuralDetailViewModel,
    navController: NavController
) {
    val uiState by vm.uiState.collectAsState()
    val secondaryScript by vm.secondaryScript.collectAsState()
    val context = LocalContext.current
    val isTamilPrimary = LocalLocale.current.platformLocale.language == "ta"

    LaunchedEffect(kuralId) {
        vm.loadKural(kuralId)
    }

    val foundState = uiState as? KuralDetailUiState.Found
    val foundKural = foundState?.kural
    val isFavourite = foundState?.isFavourite ?: false

    val explanationTaLabel = stringResource(R.string.explanation_ta)
    val explanationEnLabel = stringResource(R.string.explanation_en)
    val sentFromAppLabel = stringResource(R.string.sent_from_app)
    val shareTitle = stringResource(R.string.share_kural)

    // The shared text follows the app's primary/secondary language settings: the header
    // is rendered in those languages, the Tamil kural is always included, the
    // transliteration only when enabled as secondary, and both explanations always.
    val sharedText = foundKural?.let { kural ->
        remember(
            kural,
            secondaryScript,
            isTamilPrimary,
            explanationTaLabel,
            explanationEnLabel,
            sentFromAppLabel
        ) {
            buildSharedKuralText(
                kural = kural,
                secondaryScript = secondaryScript,
                isTamilPrimary = isTamilPrimary,
                explanationTaLabel = explanationTaLabel,
                explanationEnLabel = explanationEnLabel,
                sentFromAppLabel = sentFromAppLabel
            )
        }
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
                            sharedText?.let { text ->
                                val intent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, text)
                                }
                                context.startActivity(Intent.createChooser(intent, shareTitle))
                            }
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
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                lineHeight = 28.sp
                            )
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
                            taValue = k.id.toString(),
                            enValue = k.id.toString(),
                            tlValue = "",
                            modifier = Modifier.weight(1f)
                        )
                        InfoCard(
                            label = stringResource(R.string.part),
                            taValue = k.palTa,
                            enValue = k.palEn,
                            tlValue = k.palTl,
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
                            taValue = k.iyalTa,
                            enValue = k.iyalEn,
                            tlValue = k.iyalTl,
                            modifier = Modifier.weight(1f)
                        )
                        InfoCard(
                            label = stringResource(R.string.chapter),
                            taValue = k.adikaramTa,
                            enValue = k.adikaramEn,
                            tlValue = k.adikaramTl,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    DetailItem(
                        label = stringResource(R.string.explanation_ta),
                        value = k.explanationTa
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    DetailItem(
                        label = stringResource(R.string.explanation_en),
                        value = k.explanationEn
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    DetailItem(
                        label = stringResource(R.string.transliteration),
                        value = k.kuralTl.replace("<br />", "\n")
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    DetailItem(
                        label = stringResource(R.string.couplet),
                        value = k.couplet
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        stringResource(R.string.commentators),
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    if (k.commentaryMuVaratharasanar.isNotBlank()) {
                        CollapsibleExplanation(
                            label = stringResource(R.string.mu_varadarasanar),
                            value = k.commentaryMuVaratharasanar
                        )
                    }
                    if (k.commentaryParimelazhagar.isNotBlank()) {
                        CollapsibleExplanation(
                            label = stringResource(R.string.parimelazhagar),
                            value = k.commentaryParimelazhagar
                        )
                    }
                    if (k.commentarySalamanPappaiya.isNotBlank()) {
                        CollapsibleExplanation(
                            label = stringResource(R.string.solomon_pappaiah),
                            value = k.commentarySalamanPappaiya
                        )
                    }
                    if (k.commentaryManakudavar.isNotBlank()) {
                        CollapsibleExplanation(
                            label = stringResource(R.string.manakkudavar),
                            value = k.commentaryManakudavar
                        )
                    }
                    if (k.commentaryMunusami.isNotBlank()) {
                        CollapsibleExplanation(
                            label = stringResource(R.string.munusami),
                            value = k.commentaryMunusami
                        )
                    }
                    if (k.commentaryKarunanidhi.isNotBlank()) {
                        CollapsibleExplanation(
                            label = stringResource(R.string.karunanidhi),
                            value = k.commentaryKarunanidhi
                        )
                    }
                }
            }
        }
    }
}

/**
 * Builds the shared kural text from the app's primary/secondary language settings: the
 * header is rendered in those languages, the Tamil kural is always included, the
 * transliteration only when enabled as secondary, and both explanations always.
 */
private const val PLAY_STORE_URL =
    "https://play.google.com/store/apps/details?id=com.kulothunganug.thirukkural"

private fun buildSharedKuralText(
    kural: ThirukkuralModel,
    secondaryScript: SecondaryScript,
    isTamilPrimary: Boolean,
    explanationTaLabel: String,
    explanationEnLabel: String,
    sentFromAppLabel: String
): String {
    val secondary = secondaryScript.coerced(isTamilPrimary)
    return buildString {
        append("Thirukkural #${kural.id}\n")
        val primaryHeader = if (isTamilPrimary) {
            "${kural.palTa} | ${kural.iyalTa} | ${kural.adikaramTa}"
        } else {
            "${kural.palEn} | ${kural.iyalEn} | ${kural.adikaramEn}"
        }
        append(primaryHeader)
        append("\n")
        val secondaryHeader = secondary.text(
            "${kural.palTa} | ${kural.iyalTa} | ${kural.adikaramTa}",
            "${kural.palEn} | ${kural.iyalEn} | ${kural.adikaramEn}",
            "${kural.palTl} | ${kural.iyalTl} | ${kural.adikaramTl}"
        )
        if (secondaryHeader.isNotEmpty() && secondaryHeader != primaryHeader) {
            append(secondaryHeader)
            append("\n")
        }
        append("\n")
        append(kural.kuralTa.replace("<br />", "\n"))
        append("\n\n")
        if (secondary == SecondaryScript.TRANSLITERATION) {
            append(kural.kuralTl.replace("<br />", "\n"))
            append("\n\n")
        }
        append("$explanationTaLabel:\n")
        append(kural.explanationTa)
        append("\n\n")
        append("$explanationEnLabel:\n")
        append(kural.explanationEn)
        append("\n\n")
        append("- $sentFromAppLabel")
        append("\n")
        append(PLAY_STORE_URL)
    }.trim()
}

@Composable
fun DetailItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
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
            Spacer(modifier = Modifier.height(4.dp))
            SelectionContainer {
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

@Composable
fun InfoCard(
    label: String,
    taValue: String,
    enValue: String,
    tlValue: String,
    modifier: Modifier = Modifier
) {
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
                LanguageAwareLabel(
                    ta = taValue,
                    en = enValue,
                    tl = tlValue,
                    primaryStyle = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    secondaryStyle = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
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
