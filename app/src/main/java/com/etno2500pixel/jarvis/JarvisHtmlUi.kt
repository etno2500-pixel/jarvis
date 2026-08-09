package com.etno2500pixel.jarvis

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Public
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val HtmlBg = Color(0xFF010A15)
private val HtmlPanel = Color(0xB3021224)
private val HtmlCyan = Color(0xFF00E5FF)
private val HtmlDim = Color(0xFF7097B0)
private val HtmlSuccess = Color(0xFF00FF66)
private val HtmlWhite = Color.White

@Composable
fun JarvisHtmlApp(
    apiKey: String,
    saveKey: (String) -> Unit,
    onSend: (String, (String) -> Unit) -> Unit,
    onSpeak: () -> Unit
) {
    var input by remember { mutableStateOf("") }
    var processing by remember { mutableStateOf(false) }
    val messages = remember {
        mutableStateListOf(
            "JARVIS|Guten Abend. Wie kann ich Ihnen helfen?|23:47",
            "SIE|Was kannst du?|23:47",
            "JARVIS|Ich kann mit Ihnen sprechen, Fragen beantworten, Erinnerungen speichern, Aufgaben planen, Systeme steuern und vieles mehr. Fragen Sie einfach.|23:47"
        )
    }
    val listState = rememberLazyListState()
    val earthTransition = rememberInfiniteTransition(label = "earth")
    val earthRotation by earthTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(14000, easing = LinearEasing), RepeatMode.Restart),
        label = "earthRotation"
    )

    MaterialTheme(colorScheme = darkColorScheme(background = HtmlBg, surface = HtmlPanel, primary = HtmlCyan)) {
        Surface(
            modifier = Modifier.fillMaxSize().background(HtmlBg).safeDrawingPadding(),
            color = HtmlBg
        ) {
            Column(
                modifier = Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding().imePadding()
            ) {
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 7.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("23:47", color = HtmlDim, fontSize = 12.sp)
                    Text("● ONLINE", color = HtmlSuccess, fontSize = 12.sp)
                }

                Column(Modifier.fillMaxWidth().padding(vertical = 7.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("JARVIS", color = HtmlWhite, fontSize = 28.sp, letterSpacing = 4.sp, fontWeight = FontWeight.Normal)
                    Text("JUST A RATHER VERY INTELLIGENT SYSTEM", color = HtmlDim, fontSize = 9.sp, letterSpacing = 1.sp)
                }

                Row(Modifier.fillMaxWidth().padding(horizontal = 10.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    HtmlStatusCard("Kommunikation", "VERBUNDEN", "STABIL", Icons.Default.NetworkCheck, Modifier.weight(1f))
                    HtmlStatusCard("Netzwerk", "STABIL", "PING 12ms", Icons.Default.Public, Modifier.weight(1f))
                }
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth().padding(horizontal = 10.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    HtmlStatusCard("Sicherheit", "GESCHÜTZT", "OPTIMAL", Icons.Default.Security, Modifier.weight(1f))
                    HtmlStatusCard("Energie", "OPTIMAL", "100%", Icons.Default.Bolt, Modifier.weight(1f))
                }

                Box(Modifier.fillMaxWidth().height(82.dp), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Public, contentDescription = "Drehende Erde", modifier = Modifier.size(62.dp).graphicsLayer { rotationY = earthRotation }, tint = HtmlCyan.copy(alpha = .85f))
                }

                Row(Modifier.fillMaxWidth().background(HtmlPanel).padding(horizontal = 15.dp, vertical = 9.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text("DEIN STANDORT", color = HtmlDim, fontSize = 9.sp)
                        Text("MÜNCHEN, DEUTSCHLAND", color = HtmlWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("14°C Klarer Himmel", color = HtmlWhite, fontSize = 11.sp)
                        Text("Dienstag, 4. Juni 2024", color = HtmlDim, fontSize = 9.sp)
                    }
                }

                LazyColumn(
                    state = listState,
                    modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = 15.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 12.dp)
                ) {
                    items(messages) { entry ->
                        val parts = entry.split("|", limit = 3)
                        val sender = parts.getOrElse(0) { "JARVIS" }
                        val body = parts.getOrElse(1) { "" }
                        val time = parts.getOrElse(2) { "23:47" }
                        val user = sender == "SIE"
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(start = if (user) 45.dp else 0.dp, end = if (user) 0.dp else 45.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (user) HtmlCyan.copy(alpha = .15f) else Color(0xCC021C36))
                                .border(1.dp, HtmlCyan.copy(alpha = .18f), RoundedCornerShape(12.dp))
                                .padding(10.dp)
                        ) {
                            Text(sender, color = HtmlCyan, fontSize = 9.sp, letterSpacing = 1.sp)
                            Text(body, color = HtmlWhite, fontSize = 13.sp, lineHeight = 18.sp, modifier = Modifier.padding(top = 3.dp))
                            Text(time, color = HtmlDim, fontSize = 8.sp, modifier = Modifier.fillMaxWidth().padding(top = 4.dp), textAlign = TextAlign.End)
                        }
                    }
                    if (processing) item { Text("JARVIS verarbeitet …", color = HtmlCyan, fontSize = 12.sp) }
                }

                Row(Modifier.fillMaxWidth().background(Color(0xE6010A15)).padding(horizontal = 15.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(Modifier.size(64.dp).clip(CircleShape).background(HtmlBg).border(2.dp, HtmlCyan, CircleShape).clickable(enabled = !processing) { onSpeak() }, contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Mic, "Mikrofon", tint = HtmlCyan, modifier = Modifier.size(28.dp))
                    }
                    OutlinedTextField(value = input, onValueChange = { input = it }, modifier = Modifier.weight(1f), singleLine = true, placeholder = { Text("Nachricht …", color = HtmlDim) }, shape = RoundedCornerShape(20.dp))
                    IconButton(enabled = !processing && input.isNotBlank(), onClick = {
                        val text = input.trim()
                        input = ""
                        messages.add("SIE|$text|23:47")
                        processing = true
                        onSend(text) { answer ->
                            messages.add("JARVIS|$answer|23:47")
                            processing = false
                        }
                    }) { Icon(Icons.Default.Send, "Senden", tint = HtmlCyan) }
                }

                Row(Modifier.fillMaxWidth().background(Color(0xFF01060D)).padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                    listOf("CHAT", "SYSTEME", "AUFGABEN", "ERINNERUNGEN", "EINSTELLUNGEN").forEachIndexed { index, label ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(horizontal = 4.dp)) {
                            Icon(if (index == 0) Icons.Default.Smartphone else Icons.Default.Public, contentDescription = label, tint = if (index == 0) HtmlCyan else HtmlDim, modifier = Modifier.size(20.dp))
                            Text(label, color = if (index == 0) HtmlCyan else HtmlDim, fontSize = 8.sp)
                        }
                    }
                }
            }
        }
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) listState.animateScrollToItem(messages.lastIndex)
    }
}

@Composable
private fun HtmlStatusCard(
    title: String,
    value: String,
    sub: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth().border(1.dp, HtmlCyan.copy(alpha = .25f), RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(8.dp),
        color = HtmlPanel
    ) {
        Row(Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp), tint = HtmlCyan)
            Spacer(Modifier.width(8.dp))
            Column(Modifier.weight(1f)) {
                Text(title, color = HtmlDim, fontSize = 8.sp)
                Text(value, color = HtmlSuccess, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Text(sub, color = HtmlDim, fontSize = 8.sp)
            }
        }
    }
}
