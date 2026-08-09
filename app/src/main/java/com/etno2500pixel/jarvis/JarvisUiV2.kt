package com.etno2500pixel.jarvis

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val JBackground = Color(0xFF03070C)
private val JSurface = Color(0xFF09131C)
private val JCyan = Color(0xFF00E5FF)
private val JGreen = Color(0xFF38F58A)
private val JText = Color(0xFFEAFBFF)
private val JMuted = Color(0xFF78909C)

@Composable
fun JarvisAppV2(
    apiKey: String,
    saveKey: (String) -> Unit,
    onSend: (String, (String) -> Unit) -> Unit,
    onSpeak: () -> Unit
) {
    var input by remember { mutableStateOf("") }
    var processing by remember { mutableStateOf(false) }
    var settings by remember { mutableStateOf(false) }
    val messages = remember { mutableStateListOf("JARVIS: System bereit. Wie kann ich helfen?") }
    val scroll = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) scroll.animateScrollToItem(messages.lastIndex)
    }

    MaterialTheme(colorScheme = darkColorScheme(background = JBackground, surface = JSurface, primary = JCyan)) {
        Surface(Modifier.fillMaxSize().background(JBackground), color = JBackground) {
            Column(Modifier.fillMaxSize().imePadding().padding(horizontal = 12.dp)) {
                Row(
                    Modifier.fillMaxWidth().padding(top = 10.dp, bottom = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {}) { Icon(Icons.Default.Menu, "Menü", tint = JCyan) }
                    Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("J A R V I S", color = JText, fontSize = 23.sp, fontWeight = FontWeight.Light, letterSpacing = 5.sp)
                        Text("AI ASSISTANT // CORE ONLINE", color = JCyan, fontSize = 7.sp, letterSpacing = 1.5.sp)
                    }
                    IconButton(onClick = { settings = true }) { Icon(Icons.Default.Settings, "Einstellungen", tint = JCyan) }
                }

                Surface(
                    Modifier.fillMaxWidth(),
                    color = Color(0x1200E5FF),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, JCyan.copy(alpha = .28f))
                ) {
                    Row(Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(9.dp).background(JGreen, CircleShape))
                        Spacer(Modifier.size(8.dp))
                        Column(Modifier.weight(1f)) {
                            Text("JARVIS CORE", color = JText, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Text(if (apiKey.isBlank()) "KI nicht konfiguriert" else "KI VERBUNDEN", color = if (apiKey.isBlank()) JMuted else JGreen, fontSize = 8.sp)
                        }
                        Text("ONLINE", color = JGreen, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Box(Modifier.fillMaxWidth().height(185.dp), contentAlignment = Alignment.Center) {
                    Canvas(Modifier.size(168.dp)) {
                        val c = Offset(size.width / 2f, size.height / 2f)
                        val r = size.minDimension / 2f - 5f
                        drawCircle(JCyan.copy(alpha = .12f), r)
                        drawCircle(JCyan.copy(alpha = .7f), r, c, style = Stroke(1.5f))
                        drawCircle(JCyan.copy(alpha = .28f), r * .76f, c, style = Stroke(1f))
                        drawCircle(JGreen.copy(alpha = .75f), r * .10f, c)
                        drawCircle(JCyan.copy(alpha = .45f), r * .92f, c, style = Stroke(1f))
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("JARVIS", color = JCyan, fontSize = 14.sp, letterSpacing = 3.sp)
                        Text(if (processing) "VERARBEITET …" else "BEREIT", color = if (processing) JCyan else JGreen, fontSize = 8.sp, letterSpacing = 2.sp)
                    }
                }

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                    StatusTile("VOICE", if (processing) "BUSY" else "READY")
                    StatusTile("MEMORY", "ACTIVE")
                    StatusTile("SECURITY", "OK")
                }

                LazyColumn(
                    Modifier.fillMaxWidth().weight(1f).padding(top = 9.dp),
                    state = scroll,
                    verticalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    items(messages) { MessageBubble(it) }
                }

                Row(Modifier.fillMaxWidth().padding(vertical = 7.dp), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(enabled = !processing, onClick = onSpeak) {
                        Icon(Icons.Default.Mic, "Sprechen", tint = JCyan, modifier = Modifier.size(28.dp))
                    }
                    OutlinedTextField(
                        value = input,
                        onValueChange = { input = it },
                        Modifier.weight(1f),
                        singleLine = true,
                        placeholder = { Text("Mit JARVIS sprechen …", color = JMuted, fontSize = 11.sp) },
                        shape = RoundedCornerShape(18.dp)
                    )
                    IconButton(enabled = !processing && input.isNotBlank(), onClick = {
                        val text = input.trim()
                        input = ""
                        processing = true
                        messages.add("DU: $text")
                        onSend(text) { answer ->
                            messages.add("JARVIS: $answer")
                            processing = false
                        }
                    }) { Icon(Icons.AutoMirrored.Filled.Send, "Senden", tint = JCyan) }
                }

                Row(Modifier.fillMaxWidth().padding(bottom = 8.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                    NavLabel("ASSISTENT")
                    NavLabel("ERINNERUNGEN")
                    NavLabel("AUFGABEN")
                    Icon(Icons.Default.Tune, "System", tint = JCyan, modifier = Modifier.size(16.dp))
                }
            }
        }
    }

    if (settings) {
        JarvisApiSettings(apiKey, saveKey) { settings = false }
    }
}

@Composable
private fun StatusTile(title: String, value: String) {
    Surface(Modifier.weight(1f), color = JSurface, shape = RoundedCornerShape(10.dp), border = androidx.compose.foundation.BorderStroke(1.dp, JCyan.copy(alpha = .2f))) {
        Column(Modifier.padding(8.dp)) {
            Text(title, color = JMuted, fontSize = 7.sp, letterSpacing = 1.sp)
            Text(value, color = if (value == "OK" || value == "ACTIVE" || value == "READY") JGreen else JCyan, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun MessageBubble(message: String) {
    val user = message.startsWith("DU:")
    Surface(
        Modifier.fillMaxWidth().padding(start = if (user) 38.dp else 0.dp, end = if (user) 0.dp else 38.dp),
        color = if (user) Color(0xFF0A2030) else JSurface,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, JCyan.copy(alpha = .2f))
    ) {
        Column(Modifier.padding(9.dp)) {
            Text(if (user) "DU" else "JARVIS", color = JCyan, fontSize = 7.sp, fontWeight = FontWeight.Bold)
            Text(message.substringAfter(": ", message), color = JText, fontSize = 11.sp)
        }
    }
}

@Composable
private fun NavLabel(text: String) {
    Text(text, color = JMuted, fontSize = 7.sp, letterSpacing = .8.sp)
}

@Composable
private fun JarvisApiSettings(key: String, save: (String) -> Unit, close: () -> Unit) {
    var value by remember(key) { mutableStateOf(key) }
    androidx.compose.material3.AlertDialog(
        onDismissRequest = close,
        title = { Text("JARVIS KI") },
        text = { OutlinedTextField(value = value, onValueChange = { value = it }, singleLine = true, label = { Text("OpenAI API-Key") }) },
        confirmButton = { androidx.compose.material3.TextButton(onClick = { save(value); close() }) { Text("Speichern") } },
        dismissButton = { androidx.compose.material3.TextButton(onClick = close) { Text("Abbrechen") } }
    )
}
