package com.etno2500pixel.jarvis

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.room.Room
import com.etno2500pixel.jarvis.core.JarvisAgent
import com.etno2500pixel.jarvis.core.LearningEngine
import com.etno2500pixel.jarvis.data.JarvisDatabase
import com.etno2500pixel.jarvis.tools.AndroidTools
import com.etno2500pixel.jarvis.voice.VoiceManager
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var voice: VoiceManager
    private lateinit var agent: JarvisAgent
    private lateinit var tools: AndroidTools

    private val speech = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val text = result.data?.getStringArrayListExtra("android.speech.extra.RESULTS")?.firstOrNull()
        if (!text.isNullOrBlank()) handle(text)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = Room.databaseBuilder(applicationContext, JarvisDatabase::class.java, "jarvis.db").build()
        voice = VoiceManager(this)
        tools = AndroidTools(this)
        agent = JarvisAgent(db.memoryDao(), db.conversationDao(), LearningEngine(db.memoryDao()))
        setContent {
            JarvisApp(
                onSend = { text, onResult -> handle(text, onResult) },
                onSpeak = {
                    requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), 10)
                    speech.launch(voice.recognitionIntent())
                }
            )
        }
    }

    private fun handle(text: String, onResult: (String) -> Unit = {}) {
        MainScope().launch {
            val response = if (!tools.executeSafeCommand(text)) agent.respond(text) else "Erledigt."
            onResult(response)
            voice.speak(response)
        }
    }

    override fun onDestroy() {
        voice.release()
        super.onDestroy()
    }
}

@Composable
private fun JarvisApp(onSend: (String, (String) -> Unit) -> Unit, onSpeak: () -> Unit) {
    var input by remember { mutableStateOf("") }
    var processing by remember { mutableStateOf(false) }
    val messages = remember { mutableStateListOf("JARVIS: System online. Lernkern aktiv.") }
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) listState.animateScrollToItem(messages.lastIndex)
    }

    val transition = rememberInfiniteTransition(label = "jarvis_core")
    val rotation by transition.animateFloat(
        0f, 360f,
        androidx.compose.animation.core.infiniteRepeatable(
            androidx.compose.animation.core.tween(9000, easing = androidx.compose.animation.core.LinearEasing)
        ), label = "rotation"
    )
    val pulse by transition.animateFloat(
        0.88f, 1.08f,
        androidx.compose.animation.core.infiniteRepeatable(
            androidx.compose.animation.core.tween(1800),
            repeatMode = androidx.compose.animation.core.RepeatMode.Reverse
        ), label = "pulse"
    )

    MaterialTheme(colorScheme = darkColorScheme(
        background = Color(0xFF02060A),
        surface = Color(0xFF071018),
        surfaceVariant = Color(0xFF0B1A24),
        primary = Color(0xFF59DFFF),
        secondary = Color(0xFF2BA9D1)
    )) {
        Surface(Modifier.fillMaxSize()) {
            Column(Modifier.fillMaxSize().background(Color(0xFF02060A)).padding(horizontal = 16.dp, vertical = 12.dp)) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("J.A.R.V.I.S.", fontSize = 25.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE8FAFF))
                        Text(if (processing) "PROCESSING REQUEST" else "SYSTEM ONLINE", fontSize = 10.sp, letterSpacing = 1.6.sp, color = Color(0xFF59DFFF))
                    }
                    IconButton(onClick = {}) { Icon(Icons.Default.Settings, "Einstellungen", tint = Color(0xFF8BAFBD)) }
                }

                Spacer(Modifier.height(10.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatusChip("CORE", "ONLINE", Modifier.weight(1f))
                    StatusChip("MEMORY", "ACTIVE", Modifier.weight(1f))
                    StatusChip("VOICE", "READY", Modifier.weight(1f))
                }

                Spacer(Modifier.height(18.dp))
                Box(Modifier.fillMaxWidth().height(210.dp), contentAlignment = Alignment.Center) {
                    Box(Modifier.size(172.dp).graphicsLayer { rotationZ = rotation }.border(1.dp, Color(0xFF236D82), CircleShape))
                    Box(Modifier.size(142.dp).graphicsLayer { scaleX = pulse; scaleY = pulse }.clip(CircleShape).background(Color(0xFF08232E)).border(2.dp, Color(0xFF59DFFF), CircleShape), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("J", fontSize = 62.sp, fontWeight = FontWeight.Bold, color = Color(0xFF59DFFF))
                            Text(if (processing) "THINKING" else "READY", fontSize = 9.sp, letterSpacing = 1.4.sp, color = Color(0xFF9BEAFF))
                        }
                    }
                }

                Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFF07131C)), shape = RoundedCornerShape(18.dp)) {
                    Column(Modifier.padding(14.dp)) {
                        Text("JARVIS", fontSize = 10.sp, letterSpacing = 1.5.sp, color = Color(0xFF59DFFF))
                        Spacer(Modifier.height(4.dp))
                        Text("Bereit. Was kann ich für dich tun?", fontSize = 16.sp, color = Color(0xFFE2F8FF))
                    }
                }

                Spacer(Modifier.height(10.dp))
                LazyColumn(state = listState, modifier = Modifier.weight(1f).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(7.dp), contentPadding = PaddingValues(vertical = 4.dp)) {
                    items(messages) { message ->
                        val isUser = message.startsWith("DU:")
                        Surface(Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), color = if (isUser) Color(0xFF0B2733) else Color(0xFF08151E)) {
                            Text(message, Modifier.padding(12.dp), color = Color(0xFFDDF6FF), fontSize = 14.sp)
                        }
                    }
                    if (processing) item { Text("JARVIS verarbeitet …", Modifier.padding(6.dp), color = Color(0xFF59DFFF), fontSize = 12.sp) }
                }

                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(value = input, onValueChange = { input = it }, modifier = Modifier.weight(1f), placeholder = { Text("Nachricht an JARVIS …") }, singleLine = true, shape = RoundedCornerShape(18.dp))
                    Spacer(Modifier.width(6.dp))
                    IconButton(enabled = !processing && input.isNotBlank(), onClick = {
                        val text = input
                        messages.add("DU: $text")
                        input = ""
                        processing = true
                        onSend(text) { response -> messages.add("JARVIS: $response"); processing = false }
                    }) { Icon(Icons.AutoMirrored.Filled.Send, "Senden", tint = Color(0xFF59DFFF)) }
                    IconButton(enabled = !processing, onClick = onSpeak) { Icon(Icons.Default.Mic, "Sprechen", tint = Color(0xFF59DFFF)) }
                }
            }
        }
    }
}

@Composable
private fun StatusChip(title: String, value: String, modifier: Modifier = Modifier) {
    Surface(modifier = modifier, shape = RoundedCornerShape(10.dp), color = Color(0xFF07131B)) {
        Column(Modifier.padding(9.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(title, fontSize = 8.sp, letterSpacing = 1.sp, color = Color(0xFF6D8B96))
            Text(value, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF59DFFF))
        }
    }
}
