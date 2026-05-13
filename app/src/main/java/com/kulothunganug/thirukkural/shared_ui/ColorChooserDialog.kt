package com.kulothunganug.thirukkural.shared_ui

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.maxLength
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.then
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.graphics.toColorInt
import com.github.skydoves.colorpicker.compose.AlphaSlider
import com.github.skydoves.colorpicker.compose.AlphaTile
import com.github.skydoves.colorpicker.compose.BrightnessSlider
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController

@Composable
fun ColorChooserDialog(
    label: String,
    initialColor: String,
    onColorSelected: (String) -> Unit,
    onDismissRequest: () -> Unit
) {

    val controller = rememberColorPickerController()

    var isError by remember { mutableStateOf(false) }
    val inputState = rememberTextFieldState(initialColor)
    val ctx = LocalContext.current;

    LaunchedEffect(initialColor) {
        controller.selectByColor(Color(initialColor.toColorInt()), false)
    }


    LaunchedEffect(inputState.text) {
        if (inputState.text.length != 8) {
            isError = true; return@LaunchedEffect
        }
        try {
            val color = Color("#${inputState.text}".toColorInt())
            isError = false
            controller.selectByColor(color, false)
        } catch (_: IllegalArgumentException) {
            isError = true
        }
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
                        inputState.edit { replace(0, length, colorEnvelope.hexCode) }
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

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AlphaTile(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(6.dp)),
                        controller = controller,
                    )
                    TextField(
                        inputState,
                        isError = isError,
                        leadingIcon = { Text("#") },
                        lineLimits = TextFieldLineLimits.SingleLine,
                        inputTransformation = InputTransformation.maxLength(8).then(
                            InputTransformation {
                                val filtered = asCharSequence()
                                    .filter {
                                        it.isDigit() ||
                                                it.lowercaseChar() in 'a'..'f'
                                    }

                                val filteredText = filtered.toString()

                                if (filteredText != asCharSequence().toString()) {
                                    replace(0, length, filteredText)
                                }
                            }
                        )
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.align(Alignment.End)
                ) {
                    OutlinedButton(onClick = { onDismissRequest() }) { Text("Cancel") }
                    Button(onClick = {
                        if (isError) {
                            Toast.makeText(
                                ctx,
                                "The entered hex code is not valid!",
                                Toast.LENGTH_SHORT
                            ).show();
                            return@Button;
                        }

                        onColorSelected("#${inputState.text}");
                        onDismissRequest()
                    }) {
                        Text(
                            "Ok"
                        )
                    }
                }
            }
        }
    }
}