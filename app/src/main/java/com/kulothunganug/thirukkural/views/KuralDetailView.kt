package com.kulothunganug.thirukkural.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.kulothunganug.thirukkural.formatTransliteration
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
                    text = k.kural.replace("<br />", "\n"),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 28.sp,
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))

                DetailItem(label = "பால் (Section)", value = k.paal)
                DetailItem(label = "இயல் (Chapter Group)", value = k.iyal)
                DetailItem(label = "அதிகாரம் (Chapter)", value = k.adhigaram)
                
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))

                DetailItem(label = "Transliteration", value = k.transliteration.replace("<br />", "\n"))
                DetailItem(label = "விளக்கம் (Meaning)", value = k.vilakam)
                DetailItem(label = "Couplet", value = k.couplet)
                
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))

                Text("Explanations", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                
                DetailItem(label = "M. Varadharajanar", value = k.mVaradharajanar)
                DetailItem(label = "Solomon Pappaiya", value = k.solomonPappaiya)
                DetailItem(label = "Kalaingar Urai", value = k.kalaingarUrai)
                DetailItem(label = "Parimezhalagar Urai", value = k.parimezhalagarUrai)
            }
        }
    }


}

@Composable
fun DetailItem(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.secondary
        )
        Text(
            text = value.replace("<br />", "\n"),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}