package com.etno2500pixel.jarvis

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val TestBg = Color(0xFF010A15)
private val TestPanel = Color(0xCC021224)
private val TestCyan = Color(0xFF00E5FF)
private val TestGreen = Color(0xFF00FF66)
private val TestText = Color.White
private val TestDim = Color(0xFF7097B0)

@Composable
fun JarvisTestApp(
    onSend: (String, (String) -> Unit) -> Unit,
    onSpeak: () -> Unit
) {
    var input by remember { mutableStateOf("") }
    var processing by remember { mutableStateOf(false) }
    val messages = remember { mutableStateListOf("JARVIS: Guten Abend. Wie kann ich Ihnen helfen?") }
    val listState = rememberLazyListState()
    val keyboard = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    val transition = rememberInfiniteTransition(label = "earthTransition")
    val rotation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            tween(9000, easing = LinearEasing),
            RepeatMode.Restart
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

    MaterialTheme(
        colorScheme = darkColorScheme(
            background = TestBg,
            surface = TestPanel,
            primary = TestCyan
        )
    ) {
        Surface(Modifier.fillMaxSize(), color = TestBg) {
            Column(Modifier.fillMaxSize()) {
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Menu, "Menü", tint = TestCyan)
                    Column(
                        Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("JARVIS", color = TestText, fontSize = 24.sp, letterSpacing = 5.sp)
                        Text(
                            "JUST A RATHER VERY INTELLIGENT SYSTEM",
                            color = TestDim,
                            fontSize = 7.sp,
                            letterSpacing = 1.sp
                        )
                    }
                    Text("● ONLINE", color = TestGreen, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }

                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    TestCard("KOMMUNIKATION", "VERBUNDEN", Icons.Default.Settings, Modifier.weight(1f))
                    TestCard("NETZWERK", "STABIL", Icons.Default.Share, Modifier.weight(1f))
                }
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    TestCard("SICHERHEIT", "GESCHÜTZT", Icons.Default.Shield, Modifier.weight(1f))
                    TestCard("ENERGIE", "OPTIMAL", Icons.Default.Bolt, Modifier.weight(1f))
                }

                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        Text("DEIN STANDORT", color = TestDim, fontSize = 8.sp)
                        Text("MÜNCHEN, DEUTSCHLAND", color = TestText, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("14°C Klarer Himmel", color = TestText, fontSize = 9.sp)
                        Text("Dienstag, 4. Juni 2024", color = TestDim, fontSize = 7.sp)
                    }
                }

                // Exakt die im Prototyp hinterlegte Erde verwenden und nur um die Y-Achse drehen.
                Box(
                    Modifier.fillMaxWidth().height(145.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.jarvis_earth),
                        contentDescription = "Erde",
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .graphicsLayer { rotationY = rotation }
                    )
                }

                LazyColumn(
                    Modifier.weight(1f).fillMaxWidth().padding(horizontal = 12.dp),
                    state = listState,
                    verticalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    items(messages) { message ->
                        val user = message.startsWith("DU:")
                        Surface(
                            Modifier.fillMaxWidth().padding(
                                start = if (user) 35.dp else 0.dp,
                                end = if (user) 0.dp else 35.dp
                            ),
                            color = if (user) Color(0x2600E5FF) else Color(0xCC021C36),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                TestCyan.copy(alpha = .3f)
                            )
                        ) {
                            Text(message, Modifier.padding(9.dp), color = TestText, fontSize = 12.sp)
                        }
                    }
                    if (processing) {
                        item { Text("JARVIS verarbeitet …", color = TestCyan, fontSize = 9.sp) }
                    }
                }

                Row(
                    Modifier.fillMaxWidth().navigationBarsPadding().padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { hideKeyboard() }) {
                        Icon(Icons.Default.Keyboard, "Tastatur ausblenden", tint = TestCyan)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            Modifier
                                .size(54.dp)
                                .border(2.dp, TestCyan, CircleShape)
                                .background(Color(0x3300E5FF), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            IconButton(
                                enabled = !processing,
                                onClick = {
                                    hideKeyboard()
                                    onSpeak()
                                }
                            ) {
                                Icon(Icons.Default.Mic, "Sprechen", tint = TestCyan)
                            }
                        }
                        Text("HALTEN UND SPRECHEN", color = TestCyan, fontSize = 6.sp)
                    }
                    OutlinedTextField(
                        value = input,
                        onValueChange = { input = it },
                        modifier = Modifier.weight(1f).padding(horizontal = 6.dp),
                        placeholder = { Text("Nachricht …", fontSize = 10.sp) },
                        singleLine = true,
                        shape = RoundedCornerShape(20.dp)
                    )
                    IconButton(
                        enabled = input.isNotBlank() && !processing,
                        onClick = {
                            val text = input.trim()
                            input = ""
                            hideKeyboard()
                            processing = true
                            messages.add("DU: $text")
                            onSend(text) { answer ->
                                messages.add("JARVIS: $answer")
                                processing = false
                            }
                        }
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, "Senden", tint = TestCyan)
                    }
                }

                Row(
                    Modifier.fillMaxWidth().padding(vertical = 5.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TestNav("CHAT", Icons.Default.ChatBubble)
                    TestNav("SYSTEME", Icons.Default.Settings)
                    TestNav("AUFGABEN", Icons.Default.CheckCircle)
                    TestNav("OPTIONEN", Icons.Default.Notifications)
                }
            }
        }
    }
}

@Composable
private fun TestCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier
) {
    Surface(
        modifier,
        color = TestPanel,
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, TestCyan.copy(alpha = .25f))
    ) {
        Row(Modifier.padding(7.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = TestCyan, modifier = Modifier.size(18.dp))
            Spacer(Modifier.size(6.dp))
            Column {
                Text(title, color = TestDim, fontSize = 7.sp)
                Text(value, color = TestGreen, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun TestNav(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, null, tint = TestCyan, modifier = Modifier.size(17.dp))
        Text(label, color = TestDim, fontSize = 6.sp)
    }
}
