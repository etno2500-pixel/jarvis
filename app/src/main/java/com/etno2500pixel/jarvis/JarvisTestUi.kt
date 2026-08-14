package com.etno2500pixel.jarvis

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@Composable
fun JarvisTestApp(
    onSend: (String, (String) -> Unit) -> Unit,
    onSpeak: () -> Unit
) {
    var keyboardMode by remember { mutableStateOf(false) }
    var input by remember { mutableStateOf("") }
    val keyboard = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val hud = ImageBitmap.imageResource(R.drawable.jarvis_hud)

    val transition = rememberInfiniteTransition(label = "earth")
    val rotation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(9000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "earthRotation"
    )

    fun hideKeyboard() {
        focusManager.clearFocus(force = true)
        keyboard?.hide()
        keyboardMode = false
    }

    BoxWithConstraints(Modifier.fillMaxSize().background(androidx.compose.ui.graphics.Color.Black)) {
        val earthSize = maxWidth * 0.41f
        val earthTop = maxHeight * 0.325f - earthSize / 2f

        Image(
            painter = painterResource(R.drawable.jarvis_hud),
            contentDescription = "JARVIS Oberfläche",
            modifier = Modifier.fillMaxSize()
        )

        // Nur die Erde wird aus demselben HUD-Bild ausgeschnitten und um die Y-Achse gedreht.
        Box(
            Modifier
                .size(earthSize)
                .offset(y = earthTop)
                .align(Alignment.TopCenter)
                .clip(CircleShape)
                .graphicsLayer {
                    rotationY = rotation
                    cameraDistance = 1200f
                }
        ) {
            Canvas(Modifier.fillMaxSize()) {
                val left = (hud.width * 0.295f).roundToInt()
                val top = (hud.height * 0.12f).roundToInt()
                val width = (hud.width * 0.41f).roundToInt()
                val height = width
                drawImage(
                    image = hud,
                    srcOffset = IntOffset(left, top),
                    srcSize = IntSize(width, height),
                    dstSize = IntSize(size.width.roundToInt(), size.height.roundToInt())
                )
            }
        }

        // Unsichtbare Funktionsflächen – das Abbild selbst bleibt unverändert.
        Box(
            Modifier
                .align(Alignment.BottomStart)
                .offset(x = maxWidth * 0.10f, y = -(maxHeight * 0.085f))
                .size(maxWidth * 0.13f)
                .clickable {
                    keyboardMode = true
                }
        )

        Box(
            Modifier
                .align(Alignment.BottomCenter)
                .offset(y = -(maxHeight * 0.105f))
                .size(maxWidth * 0.19f)
                .clip(CircleShape)
                .clickable {
                    hideKeyboard()
                    onSpeak()
                }
        )

        Box(
            Modifier
                .align(Alignment.BottomEnd)
                .offset(x = -(maxWidth * 0.10f), y = -(maxHeight * 0.085f))
                .size(maxWidth * 0.13f)
                .clickable {
                    hideKeyboard()
                    onSpeak()
                }
        )

        if (keyboardMode) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(92.dp)
                    .align(Alignment.BottomCenter)
                    .background(androidx.compose.ui.graphics.Color(0xEE00040A))
            ) {
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    singleLine = true,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .fillMaxWidth()
                        .padding(horizontal = 70.dp),
                    placeholder = { androidx.compose.material3.Text("Nachricht …") }
                )
                Icon(
                    Icons.Default.Send,
                    contentDescription = "Senden",
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .offset(x = (-20).dp)
                        .size(34.dp)
                        .clickable {
                            val text = input.trim()
                            if (text.isNotEmpty()) {
                                input = ""
                                hideKeyboard()
                                onSend(text) { }
                            }
                        }
                )
                Icon(
                    Icons.Default.Keyboard,
                    contentDescription = "Tastatur ausblenden",
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .offset(x = 18.dp)
                        .size(32.dp)
                        .clickable { hideKeyboard() }
                )
            }
        }
    }
}
