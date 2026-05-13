package com.kulothunganug.thirukkural.views

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
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.kulothunganug.thirukkural.viewmodels.KuralDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KuralDetailView(
    kuralId: Int,
    vm: KuralDetailViewModel,
    navController: NavController
) {
    val kural by vm.kural.collectAsState()

    LaunchedEffect(kuralId) {
        vm.loadKural(kuralId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("குறள் எண் ${kural?.id ?: ""}") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        kural?.let { k ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = k.kuralTa.replace("<br />", "\n"),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 28.sp,
                    maxLines = 2,
                    autoSize = TextAutoSize.StepBased(14.sp, 20.sp, 1.sp)
                )

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    InfoCard(label = "குறள் எண்", value = k.id.toString(), modifier = Modifier.weight(1f))
                    InfoCard(label = "பால்", value = k.palTa, modifier = Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    InfoCard(label = "இயல்", value = k.iyalTa, modifier = Modifier.weight(1f))
                    InfoCard(label = "அதிகாரம்", value = k.adikaramTa, modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(16.dp))

                DetailItem(label = "விளக்கம்", value = k.explanationTa)
                Spacer(modifier = Modifier.height(8.dp))
                DetailItem(
                    label = "Transliteration",
                    value = k.kuralTl.replace("<br />", "\n")
                )
                Spacer(modifier = Modifier.height(8.dp))
                DetailItem(label = "Couplet", value = k.couplet)

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "உரையாசிரியர்கள்",
                    style = MaterialTheme.typography.titleMedium,
                )
                Spacer(modifier = Modifier.height(8.dp))

                CollapsibleExplanation(label = "மு.வரதராசனார் உரை", value = k.commentaryMuVaratharasanar)
                CollapsibleExplanation(label = "பரிமேலழகர் உரை", value = k.commentaryParimelazhagar)
                CollapsibleExplanation(label = "சாலமன் பாப்பையா உரை", value = k.commentarySalamanPappaiya)
                CollapsibleExplanation(label = "மணக்குடவர் உரை", value = k.commentaryManakudavar)
                CollapsibleExplanation(label = "திருக்குறளார் வீ. முனிசாமி உரை", value = k.commentaryMunusami)
                CollapsibleExplanation(label = "கலைஞர் மு.கருணாநிதி உரை", value = k.commentaryKarunanidhi)
            }
        }
    }


}

@Composable
fun DetailItem(label: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun InfoCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
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
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            // clip the ripple effect
            .clip(RoundedCornerShape(12.dp))
            .clickable { expanded = !expanded }
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(12.dp)
        ) {
            // accordion header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
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
                    Text(
                        text = value,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}
