package com.kulothunganug.thirukkural.shared_ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import com.kulothunganug.thirukkural.datastore.LanguageSettings
import com.kulothunganug.thirukkural.datastore.SecondaryScript
import com.kulothunganug.thirukkural.datastore.coerced
import com.kulothunganug.thirukkural.datastore.text
import org.koin.compose.koinInject

/**
 * Renders a primary line in the app language (Tamil or English) with a smaller
 * secondary line in the user-chosen [SecondaryScript].
 */
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
    val isTamilPrimary = LocalLocale.current.platformLocale.language == "ta"
    val languageSettings: LanguageSettings = koinInject()
    val storedSecondary by languageSettings.secondaryStream.collectAsState(
        initial = SecondaryScript.TRANSLITERATION
    )

    val primaryText = if (isTamilPrimary) ta else en
    val secondaryText = storedSecondary.coerced(isTamilPrimary).text(ta, en, tl)

    Column(modifier = modifier) {
        Text(
            text = primaryText,
            style = primaryStyle,
            color = primaryColor,
            textAlign = textAlign,
            modifier = if (textAlign == TextAlign.Center || textAlign == TextAlign.End) Modifier.fillMaxWidth() else Modifier
        )
        if (secondaryText.isNotEmpty() && secondaryText != primaryText) {
            Text(
                text = secondaryText,
                style = secondaryStyle,
                color = secondaryColor,
                textAlign = textAlign,
                modifier = if (textAlign == TextAlign.Center || textAlign == TextAlign.End) Modifier.fillMaxWidth() else Modifier
            )
        }
    }
}
