package com.kulothunganug.thirukkural.shared_ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign

@Composable
fun LanguageAwareLabel(
    ta: String,
    en: String,
    tl: String,
    modifier: Modifier = Modifier,
    primaryStyle: TextStyle = MaterialTheme.typography.bodyLarge,
    secondaryStyle: TextStyle = MaterialTheme.typography.bodySmall,
    primaryColor: Color = Color.Unspecified,
    secondaryColor: Color = MaterialTheme.colorScheme.secondary,
    textAlign: TextAlign? = null
) {
    val currentLanguage = LocalLocale.current.platformLocale.language
    val isTamil = currentLanguage == "ta"

    Column(modifier = modifier) {
        Text(
            text = if (isTamil) ta else en,
            style = primaryStyle,
            color = primaryColor,
            textAlign = textAlign,
            modifier = if (textAlign == TextAlign.Center || textAlign == TextAlign.End) Modifier.fillMaxWidth() else Modifier
        )
        if (!isTamil && tl.isNotEmpty()) {
            Text(
                text = tl,
                style = secondaryStyle,
                color = secondaryColor,
                textAlign = textAlign,
                modifier = if (textAlign == TextAlign.Center || textAlign == TextAlign.End) Modifier.fillMaxWidth() else Modifier
            )
        }
    }
}
