package com.etno2500pixel.jarvis

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Alignment

@Composable
fun JarvisAppV2(
    apiKey: String,
    saveKey: (String) -> Unit,
    onSend: (String, (String) -> Unit) -> Unit,
    onSpeak: () -> Unit
) {
    var input by remember { mutableStateOf("") }
    var settings by remember { mutableStateOf(false) }
    val keyboard = LocalSoftwareKeyboardController.current

    val transition = rememberInfiniteTransition(label = "earth")
    val earthRotation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(14000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "earthRotation"
    )

    BoxWithConstraints(
        Modifier
            .fillMaxSize()
            .imePadding()
    ) {
        Image(
            painter = painterResource(R.drawable.jarvis_hud),
            contentDescription = "JARVIS Oberfläche",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        // Nur die Erde wird animiert. Der Rest des vom Nutzer vorgegebenen Bildes bleibt unverändert.
        Image(
            painter = painterResource(R.drawable.jarvis_earth),
            contentDescription = "Rotierende Erde",
            modifier = Modifier
                .size(maxWidth * 0.42f)
                .align(Alignment.TopCenter)
                .offset(y = maxHeight * 0.175f)
                .clip(CircleShape)
                .graphicsLayer { rotationZ = earthRotation },
            contentScale = ContentScale.Crop
        )

        // Unsichtbare Bedienflächen: Die sichtbare Oberfläche bleibt exakt das vorgegebene Bild.
        Box(
            Modifier
                .align(Alignment.TopStart)
                .size(76.dp)
                .offset(x = 14.dp, y = 38.dp)
                .clickable { }
        )

        Box(
            Modifier
                .align(Alignment.TopEnd)
                .size(100.dp, 62.dp)
                .offset(x = (-14).dp, y = 42.dp)
                .clickable { settings = true }
        )

        BasicTextField(
            value = input,
            onValueChange = { input = it },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .width(maxWidth * 0.39f)
                .height(70.dp)
                .offset(x = (-20).dp, y = (-maxHeight * 0.235f)),
            textStyle = androidx.compose.ui.text.TextStyle(color = Color.Transparent),
            cursorBrush = Brush.verticalGradient(listOf(Color.Transparent, Color.Transparent))
        )

        Box(
            Modifier
                .align(Alignment.BottomCenter)
                .size(120.dp)
                .offset(y = (-maxHeight * 0.135f))
                .clickable { onSpeak() }
        )

        Box(
            Modifier
                .align(Alignment.BottomEnd)
                .size(90.dp, 70.dp)
                .offset(x = (-12).dp, y = (-maxHeight * 0.205f))
                .clickable {
                    val text = input.trim()
                    if (text.isNotEmpty()) {
                        input = ""
                        keyboard?.hide()
                        onSend(text) { }
                    }
                }
        )
    }

    if (settings) {
        JarvisApiSettings(apiKey, saveKey) { settings = false }
    }
}

@Composable
private fun JarvisApiSettings(key: String, save: (String) -> Unit, close: () -> Unit) {
    var value by remember(key) { mutableStateOf(key) }
    AlertDialog(
        onDismissRequest = close,
        title = { androidx.compose.material3.Text("JARVIS KI") },
        text = {
            androidx.compose.material3.OutlinedTextField(
                value = value,
                onValueChange = { value = it },
                singleLine = true,
                label = { androidx.compose.material3.Text("OpenAI API-Key") }
            )
        },
        confirmButton = { TextButton(onClick = { save(value); close() }) { androidx.compose.material3.Text("Speichern") } },
        dismissButton = { TextButton(onClick = close) { androidx.compose.material3.Text("Abbrechen") } }
    )
}
