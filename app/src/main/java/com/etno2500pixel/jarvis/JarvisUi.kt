package com.etno2500pixel.jarvis

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.min

private val HudBackground = Color(0xFF01060B)
private val HudPanel = Color(0xCC06131D)
private val HudPanelStrong = Color(0xEE071A26)
private val HudCyan = Color(0xFF00D9FF)
private val HudGreen = Color(0xFF18F06B)
private val HudText = Color(0xFFE7F8FF)
private val HudMuted = Color(0xFFA8C3CF)

@Composable
fun JarvisApp(apiKey: String, saveKey: (String) -> Unit, onSend: (String, (String) -> Unit) -> Unit, onSpeak: () -> Unit) {
    var input by remember { mutableStateOf("") }
    var processing by remember { mutableStateOf(false) }
    var settingsOpen by remember { mutableStateOf(false) }
    val messages = remember { mutableStateListOf("JARVIS: Guten Abend.\nWie kann ich Ihnen helfen?") }
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) listState.animateScrollToItem(messages.lastIndex)
    }

    MaterialTheme(colorScheme = darkColorScheme(background = HudBackground, surface = HudPanel, primary = HudCyan, secondary = HudGreen)) {
        Surface(Modifier.fillMaxSize(), color = HudBackground) {
            BoxWithConstraints(Modifier.fillMaxSize().safeDrawingPadding().imePadding().background(HudBackground)) {
                val compact = maxWidth < 390.dp
                val globeSize = if (compact) 132.dp else 156.dp
                val orbitSize = globeSize + if (compact) 34.dp else 42.dp

                Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
                    HudHeader(apiKey.isNotBlank()) { settingsOpen = true }

                    Column(Modifier.fillMaxWidth().weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                        HudStatus("SYSTEM STATUS", "ONLINE", "ALLE SYSTEME VERBUNDEN", Icons.Default.CheckCircle)
                        Spacer(Modifier.height(8.dp))

                        Row(Modifier.fillMaxWidth().padding(horizontal = if (compact) 6.dp else 10.dp), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.Top) {
                            HudSideColumn(Modifier.weight(0.9f), listOf(
                                Triple("KOMMUNIKATION", "VERBUNDEN", Icons.Default.Settings),
                                Triple("DATENANALYSE", "AKTIV", Icons.Default.BarChart),
                                Triple("SICHERHEIT", "GESCHÜTZT", Icons.Default.Shield)
                            ), compact)

                            HudGlobe(Modifier.weight(1.35f), globeSize, orbitSize)

                            HudSideColumn(Modifier.weight(0.9f), listOf(
                                Triple("NETZWERK", "STABIL", Icons.Default.Share),
                                Triple("ÜBERWACHUNG", "AKTIV", Icons.Default.Visibility),
                                Triple("ENERGIE", "OPTIMAL", Icons.Default.Bolt)
                            ), compact)
                        }

                        HudActivity()
                        HudLocation(compact)

                        LazyColumn(
                            modifier = Modifier.fillMaxWidth().weight(1f).padding(horizontal = 10.dp),
                            state = listState,
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            contentPadding = PaddingValues(vertical = 6.dp)
                        ) {
                            items(messages) { message -> HudMessage(message) }
                            if (processing) item { Text("JARVIS verarbeitet …", color = HudCyan, fontSize = 11.sp) }
                        }
                    }

                    HudVoiceArea(input, { input = it }, processing, compact, onSpeak) {
                        val text = input.trim()
                        if (text.isNotEmpty() && !processing) {
                            input = ""
                            processing = true
                            messages.add("DU: $text")
                            onSend(text) { answer ->
                                messages.add("JARVIS: $answer")
                                processing = false
                            }
                        }
                    }
                    HudBottomNav(compact)
                }
            }
        }
    }

    if (settingsOpen) HudApiDialog(apiKey, saveKey) { settingsOpen = false }
}

@Composable
private fun HudHeader(aiOnline: Boolean, onSettings: () -> Unit) {
    Row(Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = {}) { Icon(Icons.Default.Menu, "Menü", tint = HudCyan) }
        Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("JARVIS", color = HudText, fontSize = 27.sp, fontWeight = FontWeight.Light, letterSpacing = 7.sp)
            Text("JUST A RATHER VERY INTELLIGENT SYSTEM", color = HudCyan, fontSize = 6.sp, letterSpacing = 1.sp)
        }
        Surface(shape = RoundedCornerShape(18.dp), color = if (aiOnline) Color(0x2218F06B) else Color.Transparent, border = androidx.compose.foundation.BorderStroke(1.dp, if (aiOnline) HudGreen else HudCyan)) {
            Text(if (aiOnline) "● ONLINE" else "● READY", Modifier.padding(horizontal = 9.dp, vertical = 6.dp), color = if (aiOnline) HudGreen else HudCyan, fontSize = 8.sp)
        }
        IconButton(onClick = onSettings) { Icon(Icons.Default.Settings, "Einstellungen", tint = HudCyan) }
    }
}

@Composable
private fun HudStatus(title: String, value: String, sub: String, icon: ImageVector) {
    Surface(Modifier.fillMaxWidth().padding(horizontal = 10.dp), color = HudPanelStrong, shape = RoundedCornerShape(10.dp), border = androidx.compose.foundation.BorderStroke(1.dp, HudCyan.copy(alpha = 0.55f))) {
        Row(Modifier.padding(horizontal = 10.dp, vertical = 7.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = HudCyan, modifier = Modifier.size(19.dp))
            Spacer(Modifier.size(7.dp))
            Column(Modifier.weight(1f)) {
                Text(title, color = HudText, fontSize = 8.sp)
                Text(value, color = HudGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
            Text(sub, color = HudMuted, fontSize = 7.sp)
        }
    }
}

@Composable
private fun HudSideColumn(modifier: Modifier, cards: List<Triple<String, String, ImageVector>>, compact: Boolean) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(if (compact) 5.dp else 7.dp)) {
        cards.forEach { (title, value, icon) -> HudCard(title, value, icon, compact) }
    }
}

@Composable
private fun HudCard(title: String, value: String, icon: ImageVector, compact: Boolean) {
    Surface(Modifier.fillMaxWidth(), color = HudPanel, shape = RoundedCornerShape(9.dp), border = androidx.compose.foundation.BorderStroke(1.dp, HudCyan.copy(alpha = 0.48f))) {
        Column(Modifier.padding(if (compact) 6.dp else 8.dp)) {
            Icon(icon, null, tint = HudCyan, modifier = Modifier.size(if (compact) 17.dp else 19.dp))
            Spacer(Modifier.height(3.dp))
            Text(title, color = HudMuted, fontSize = if (compact) 6.sp else 7.sp, maxLines = 1)
            Text(value, color = HudGreen, fontSize = if (compact) 8.sp else 9.sp, fontWeight = FontWeight.Bold, maxLines = 1)
            Text("●", color = HudGreen, fontSize = 7.sp)
        }
    }
}

@Composable
private fun HudGlobe(modifier: Modifier, globeSize: Dp, orbitSize: Dp) {
    var rotation by remember { mutableStateOf(0f) }

    LaunchedEffect(Unit) {
        while (true) {
            rotation = (rotation + 0.6f) % 360f
            delay(16L)
        }
    }

    Box(modifier.height(205.dp), contentAlignment = Alignment.Center) {
        Canvas(Modifier.size(orbitSize)) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = min(size.width, size.height) / 2f - 2f
            drawCircle(HudCyan.copy(alpha = 0.16f), radius, center, style = Stroke(1f))
            drawOval(HudCyan.copy(alpha = 0.22f), topLeft = Offset(center.x - radius * 0.83f, center.y - radius * 0.23f), size = Size(radius * 1.66f, radius * 0.46f), style = Stroke(1f))
        }
        Box(Modifier.size(globeSize).border(2.dp, HudCyan.copy(alpha = 0.8f), CircleShape).background(Color(0xFF06131D), CircleShape), contentAlignment = Alignment.Center) {
            Icon(Icons.Default.Public, "Erde", tint = HudCyan.copy(alpha = 0.92f), modifier = Modifier.size(globeSize * 0.72f).rotate(rotation))
        }
        Text("JARVIS", Modifier.align(Alignment.BottomCenter), color = HudCyan, fontSize = 9.sp, letterSpacing = 2.sp)
    }
}

@Composable
private fun HudActivity() {
    Column(Modifier.fillMaxWidth().padding(horizontal = 18.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("AKTIVITÄT", color = HudCyan, fontSize = 8.sp, letterSpacing = 2.sp)
        Canvas(Modifier.fillMaxWidth().height(28.dp)) {
            val mid = size.height / 2f
            var last = Offset(0f, mid)
            for (i in 1..60) {
                val x = i / 60f * size.width
                val y = mid + if (i % 7 == 0) -size.height * 0.35f else if (i % 11 == 0) size.height * 0.35f else 0f
                val current = Offset(x, y)
                drawLine(HudCyan, last, current, strokeWidth = 1.4f)
                last = current
            }
        }
    }
}

@Composable
private fun HudLocation(compact: Boolean) {
    Surface(Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 3.dp), color = HudPanelStrong, shape = RoundedCornerShape(10.dp), border = androidx.compose.foundation.BorderStroke(1.dp, HudCyan.copy(alpha = 0.42f))) {
        Row(Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.LocationOn, null, tint = HudCyan, modifier = Modifier.size(if (compact) 22.dp else 26.dp))
            Spacer(Modifier.size(6.dp))
            Column(Modifier.weight(1f)) {
                Text("DEIN STANDORT", color = HudCyan, fontSize = 7.sp)
                Text("MÜNCHEN, DEUTSCHLAND", color = HudText, fontSize = if (compact) 9.sp else 10.sp, fontWeight = FontWeight.Medium)
                Text("48.1351° N · 11.5820° E", color = HudMuted, fontSize = 6.sp)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("14°C", color = HudText, fontSize = 12.sp)
                Text("KLARER HIMMEL", color = HudMuted, fontSize = 6.sp)
            }
        }
    }
}

@Composable
private fun HudMessage(message: String) {
    val user = message.startsWith("DU:")
    Surface(Modifier.fillMaxWidth().padding(start = if (user) 34.dp else 0.dp, end = if (user) 0.dp else 34.dp), color = if (user) Color(0xFF071C31) else HudPanel, shape = RoundedCornerShape(10.dp), border = androidx.compose.foundation.BorderStroke(1.dp, HudCyan.copy(alpha = 0.35f))) {
        Column(Modifier.padding(9.dp)) {
            Text(if (user) "SIE" else "JARVIS", color = HudCyan, fontSize = 7.sp)
            Text(message.removePrefix("DU:").removePrefix("JARVIS:").trim(), color = HudText, fontSize = 10.sp)
        }
    }
}

@Composable
private fun HudVoiceArea(input: String, onInputChange: (String) -> Unit, processing: Boolean, compact: Boolean, onSpeak: () -> Unit, onSend: () -> Unit) {
    Row(Modifier.fillMaxWidth().navigationBarsPadding().padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = {}) { Icon(Icons.Default.Keyboard, "Tastatur", tint = HudCyan) }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(Modifier.size(if (compact) 68.dp else 76.dp).border(2.dp, HudCyan, CircleShape).background(Color(0xFF071624), CircleShape), contentAlignment = Alignment.Center) {
                IconButton(enabled = !processing, onClick = onSpeak) {
                    Icon(Icons.Default.Mic, "Sprechen", tint = HudCyan, modifier = Modifier.size(if (compact) 31.dp else 36.dp))
                }
            }
            Text("HALTEN UND SPRECHEN", color = HudCyan, fontSize = 6.sp, letterSpacing = 0.7.sp)
        }
        OutlinedTextField(value = input, onValueChange = onInputChange, modifier = Modifier.weight(1f).padding(start = 6.dp), placeholder = { Text("Nachricht …", fontSize = 10.sp) }, singleLine = true, shape = RoundedCornerShape(10.dp))
        IconButton(enabled = !processing && input.isNotBlank(), onClick = onSend) { Icon(Icons.AutoMirrored.Filled.Send, "Senden", tint = HudCyan) }
    }
}

@Composable
private fun HudBottomNav(compact: Boolean) {
    Row(Modifier.fillMaxWidth().padding(horizontal = 4.dp), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
        HudNavItem("CHAT", Icons.Default.ChatBubble, compact)
        HudNavItem("SYSTEME", Icons.Default.Settings, compact)
        HudNavItem("AUFGABEN", Icons.Default.CheckCircle, compact)
        HudNavItem("ERINNERUNGEN", Icons.Default.Notifications, compact)
        HudNavItem("EINSTELLUNGEN", Icons.Default.Settings, compact)
    }
}

@Composable
private fun HudNavItem(label: String, icon: ImageVector, compact: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(horizontal = 2.dp, vertical = 3.dp)) {
        Icon(icon, null, tint = HudCyan, modifier = Modifier.size(if (compact) 17.dp else 19.dp))
        Text(label, color = HudText, fontSize = if (compact) 5.sp else 6.sp, maxLines = 1)
    }
}

@Composable
private fun HudApiDialog(key: String, save: (String) -> Unit, close: () -> Unit) {
    var value by remember(key) { mutableStateOf(key) }
    AlertDialog(
        onDismissRequest = close,
        title = { Text("JARVIS KI") },
        text = {
            OutlinedTextField(value = value, onValueChange = { value = it }, singleLine = true, visualTransformation = PasswordVisualTransformation(), label = { Text("OpenAI API-Key") })
        },
        confirmButton = { TextButton(onClick = { save(value); close() }) { Text("Speichern") } },
        dismissButton = { TextButton(onClick = close) { Text("Abbrechen") } }
    )
}
