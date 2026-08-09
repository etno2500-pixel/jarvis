package com.etno2500pixel.jarvis

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Bg = Color(0xFF010A15)
private val Cyan = Color(0xFF00E5FF)
private val TextWhite = Color.White

@Composable
fun JarvisTestApp(
    onSend: (String, (String) -> Unit) -> Unit,
    onSpeak: () -> Unit
) {
    var input by remember { mutableStateOf("") }
    var processing by remember { mutableStateOf(false) }
    val messages = remember { mutableStateListOf<String>() }
    val listState = rememberLazyListState()
    val keyboard = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

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

    fun hideKeyboard() {
        focusManager.clearFocus(force = true)
        keyboard?.hide()
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) listState.animateScrollToItem(messages.lastIndex)
    }

    MaterialTheme(colorScheme = darkColorScheme(background = Bg, surface = Color.Transparent, primary = Cyan)) {
        Box(Modifier.fillMaxSize().background(Bg)) {
            Image(
                painter = painterResource(R.drawable.jarvis_hud),
                contentDescription = "JARVIS Oberfläche",
                modifier = Modifier.fillMaxSize(),
                contentScale = androidx.compose.ui.layout.ContentScale.FillBounds
            )

            Image(
                painter = painterResource(R.drawable.jarvis_earth),
                contentDescription = "Erde",
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 150.dp)
                    .size(120.dp)
                    .graphicsLayer { rotationY = earthRotation },
                contentScale = androidx.compose.ui.layout.ContentScale.FillBounds
            )

            Column(Modifier.fillMaxSize()) {
                LazyColumn(
                    Modifier.weight(1f).fillMaxWidth().padding(horizontal = 12.dp),
                    state = listState
                ) {
                    items(messages) { message ->
                        androidx.compose.material3.Text(
                            message,
                            color = TextWhite,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                    if (processing) {
                        item { androidx.compose.material3.Text("JARVIS verarbeitet …", color = Cyan, fontSize = 9.sp) }
                    }
                }

                Row(
                    Modifier.fillMaxWidth().navigationBarsPadding().padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { hideKeyboard() }) {
                        Icon(Icons.Default.Keyboard, "Tastatur ausblenden", tint = Cyan)
                    }
                    IconButton(enabled = !processing, onClick = { hideKeyboard(); onSpeak() }) {
                        Icon(Icons.Default.Mic, "Sprechen", tint = Cyan)
                    }
                    OutlinedTextField(
                        value = input,
                        onValueChange = { input = it },
                        modifier = Modifier.weight(1f).padding(horizontal = 6.dp),
                        placeholder = { androidx.compose.material3.Text("Nachricht …", fontSize = 10.sp) },
                        singleLine = true
                    )
                    IconButton(enabled = input.isNotBlank() && !processing, onClick = {
                        val text = input.trim()
                        input = ""
                        hideKeyboard()
                        processing = true
                        messages.add("DU: $text")
                        onSend(text) { answer ->
                            messages.add("JARVIS: $answer")
                            processing = false
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.Send, "Senden", tint = Cyan)
                    }
                }
            }
        }
    }
}
