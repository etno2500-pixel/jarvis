package com.etno2500pixel.jarvis

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.room.Room
import com.etno2500pixel.jarvis.ai.RemoteAIProvider
import com.etno2500pixel.jarvis.core.JarvisAgent
import com.etno2500pixel.jarvis.core.LearningEngine
import com.etno2500pixel.jarvis.data.JarvisDatabase
import com.etno2500pixel.jarvis.tools.AndroidTools
import com.etno2500pixel.jarvis.voice.VoiceManager
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

private val Bg = Color(0xFF01060B)
private val Panel = Color(0xCC06131D)
private val Cyan = Color(0xFF00D9FF)
private val Green = Color(0xFF18F06B)
private val TextMain = Color(0xFFE7F8FF)

class MainActivity : ComponentActivity() {
    private lateinit var voice: VoiceManager
    private lateinit var agent: JarvisAgent
    private lateinit var tools: AndroidTools
    private val prefs by lazy { getSharedPreferences("jarvis_settings", MODE_PRIVATE) }
    private val speech = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        result.data?.getStringArrayListExtra("android.speech.extra.RESULTS")?.firstOrNull()?.takeIf { it.isNotBlank() }?.let(::handle)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = Room.databaseBuilder(applicationContext, JarvisDatabase::class.java, "jarvis.db").build()
        voice = VoiceManager(this)
        tools = AndroidTools(this)
        agent = JarvisAgent(db.memoryDao(), db.conversationDao(), LearningEngine(db.memoryDao()), RemoteAIProvider { prefs.getString("openai_api_key", "").orEmpty() })
        setContent { JarvisApp(prefs.getString("openai_api_key", "").orEmpty(), { k -> prefs.edit().putString("openai_api_key", k.trim()).apply() }, { t, r -> handle(t, r) }, { requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), 10); speech.launch(voice.recognitionIntent()) }) }
    }
    private fun handle(text: String, onResult: (String) -> Unit = {}) = MainScope().launch { val response = if (!tools.executeSafeCommand(text)) agent.respond(text) else "Erledigt."; onResult(response); voice.speak(response) }
    override fun onDestroy() { voice.release(); super.onDestroy() }
}

@Composable
private fun JarvisApp(apiKey: String, saveKey: (String) -> Unit, onSend: (String, (String) -> Unit) -> Unit, onSpeak: () -> Unit) {
    var input by remember { mutableStateOf("") }
    var processing by remember { mutableStateOf(false) }
    var settings by remember { mutableStateOf(false) }
    val messages = remember { mutableStateListOf("JARVIS: Guten Abend.\nWie kann ich Ihnen helfen?") }
    val listState = rememberLazyListState()
    val infinite = rememberInfiniteTransition(label = "hud")
    val rotation by infinite.animateFloat(0f, 360f, infiniteRepeatable(tween(14000, easing = LinearEasing)), label = "earth")
    LaunchedEffect(messages.size) { if (messages.isNotEmpty()) listState.animateScrollToItem(messages.lastIndex) }
    MaterialTheme(colorScheme = darkColorScheme(background = Bg, surface = Panel, primary = Cyan, secondary = Green)) {
        Surface(Modifier.fillMaxSize().background(Bg)) {
            Column(Modifier.fillMaxSize().background(Bg).statusBarsPadding()) {
                Header(apiKey.isNotBlank(), { settings = true })
                SystemStatus()
                Row(Modifier.fillMaxWidth().padding(horizontal = 10.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        HudCard("KOMMUNIKATION", "VERBUNDEN", "STABIL", Icons.Default.Settings)
                        HudCard("DATENANALYSE", "AKTIV", "87%", Icons.Default.BarChart)
                        HudCard("SICHERHEIT", "GESCHÜTZT", "OPTIMAL", Icons.Default.Shield)
                        HudCard("SYSTEME", "NOMINAL", "ALLE SYSTEME OK", Icons.Default.Settings)
                    }
                    Box(Modifier.weight(1.55f).height(300.dp), contentAlignment = Alignment.Center) {
                        Box(Modifier.size(260.dp).graphicsLayer { rotationZ = rotation }.border(1.dp, Cyan.copy(alpha=.7f), CircleShape))
                        Box(Modifier.size(205.dp).graphicsLayer { rotationY = rotation }.border(3.dp, Cyan, CircleShape), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Public, "Erde", Modifier.size(175.dp), tint = Cyan.copy(alpha=.9f))
                        }
                        Text("JARVIS", Modifier.align(Alignment.BottomCenter), color = Cyan, fontSize = 11.sp, letterSpacing = 3.sp)
                    }
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        HudCard("NETZWERK", "STABIL", "PING 12ms", Icons.Default.Share)
                        HudCard("ÜBERWACHUNG", "AKTIV", "24/7", Icons.Default.Visibility)
                        HudCard("ENERGIE", "OPTIMAL", "100%", Icons.Default.Bolt)
                        HudCard("UPDATES", "AKTUELL", "KEINE UPDATES", Icons.Default.Refresh)
                    }
                }
                LocationBar()
                LazyColumn(state = listState, Modifier.weight(1f).fillMaxWidth().padding(horizontal = 14.dp), verticalArrangement = Arrangement.spacedBy(8.dp), contentPadding = PaddingValues(vertical = 8.dp)) {
                    items(messages) { m -> MessageBubble(m) }
                    if (processing) item { Text("JARVIS verarbeitet …", color = Cyan, fontSize = 12.sp) }
                }
                VoiceArea(input, { input = it }, processing, onSend, onSpeak)
                BottomNav()
            }
        }
    }
    if (settings) ApiDialog(apiKey, saveKey) { settings = false }
}

@Composable private fun Header(aiOnline: Boolean, settings: () -> Unit) {
    Row(Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = {}) { Icon(Icons.Default.Menu, "Menü", tint = Cyan) }
        Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("JARVIS", color = TextMain, fontSize = 32.sp, fontWeight = FontWeight.Light, letterSpacing = 9.sp)
            Text("JUST A RATHER VERY INTELLIGENT SYSTEM", color = Cyan, fontSize = 7.sp, letterSpacing = 1.2.sp)
        }
        Surface(shape = RoundedCornerShape(20.dp), color = Color.Transparent, border = androidx.compose.foundation.BorderStroke(1.dp, Green)) { Text("● ONLINE", Modifier.padding(horizontal = 12.dp, vertical = 8.dp), color = Green, fontSize = 10.sp) }
    }
}

@Composable private fun SystemStatus() { HudCard("SYSTEM STATUS", "ONLINE", "ALLE SYSTEME VERBUNDEN", Icons.Default.CheckCircle, Modifier.padding(horizontal = 110.dp)) }

@Composable private fun HudCard(title: String, value: String, sub: String, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier = Modifier) {
    Surface(modifier.fillMaxWidth().border(1.dp, Cyan.copy(alpha=.65f), RoundedCornerShape(12.dp)), shape = RoundedCornerShape(12.dp), color = Panel) {
        Row(Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, Modifier.size(26.dp), tint = Cyan)
            Spacer(Modifier.width(9.dp)); Column(Modifier.weight(1f)) { Text(title, color = TextMain, fontSize = 9.sp); Text(value, color = Green, fontSize = 11.sp, fontWeight = FontWeight.Bold); Text(sub, color = Color(0xFFA8C3CF), fontSize = 8.sp) }
            Text("●", color = Green, fontSize = 10.sp)
        }
    }
}

@Composable private fun LocationBar() { Surface(Modifier.fillMaxWidth().padding(horizontal = 10.dp), color = Panel, shape = RoundedCornerShape(12.dp), border = androidx.compose.foundation.BorderStroke(1.dp, Cyan.copy(alpha=.45f))) { Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.LocationOn, null, Modifier.size(34.dp), tint = Cyan); Column(Modifier.weight(1f)) { Text("DEIN STANDORT", color = Cyan, fontSize = 10.sp); Text("MÜNCHEN, DEUTSCHLAND 🇩🇪", color = TextMain, fontSize = 12.sp); Text("48.1351° N, 11.5820° E", color = Color(0xFFA8C3CF), fontSize = 9.sp) }; Column(horizontalAlignment = Alignment.End) { Text("14°C", color = TextMain, fontSize = 16.sp); Text("KLARER HIMMEL", color = Color(0xFFA8C3CF), fontSize = 8.sp) } } } }

@Composable private fun MessageBubble(message: String) { val user = message.startsWith("DU:"); Surface(Modifier.fillMaxWidth().padding(start = if (user) 45.dp else 0.dp, end = if (user) 0.dp else 45.dp), color = if (user) Color(0xFF071C31) else Panel, shape = RoundedCornerShape(14.dp), border = androidx.compose.foundation.BorderStroke(1.dp, Cyan.copy(alpha=.45f))) { Text(message, Modifier.padding(12.dp), color = TextMain, fontSize = 13.sp) } }

@Composable private fun VoiceArea(input: String, setInput: (String) -> Unit, processing: Boolean, onSend: (String, (String) -> Unit) -> Unit, onSpeak: () -> Unit) {
    Row(Modifier.fillMaxWidth().navigationBarsPadding().imePadding().padding(horizontal = 16.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = {}) { Icon(Icons.Default.Keyboard, "Tastatur", tint = Cyan) }
        Box(Modifier.size(82.dp).border(2.dp, Cyan, CircleShape), contentAlignment = Alignment.Center) { IconButton(enabled = !processing, onClick = onSpeak) { Icon(Icons.Default.Mic, "Halten und sprechen", Modifier.size(42.dp), tint = Cyan) } }
        IconButton(enabled = !processing && input.isNotBlank(), onClick = { val t = input; setInput(""); onSend(t) {} }) { Icon(Icons.AutoMirrored.Filled.Send, "Senden", tint = Cyan) }
        OutlinedTextField(value = input, onValueChange = setInput, Modifier.weight(1f).padding(start = 6.dp), placeholder = { Text("Nachricht …") }, singleLine = true, shape = RoundedCornerShape(14.dp))
    }
}

@Composable private fun BottomNav() { Row(Modifier.fillMaxWidth().navigationBarsPadding().padding(horizontal = 8.dp), horizontalArrangement = Arrangement.SpaceEvenly) { listOf("CHAT" to Icons.Default.ChatBubble, "SYSTEME" to Icons.Default.Settings, "AUFGABEN" to Icons.Default.Checklist, "ERINNERUNGEN" to Icons.Default.Notifications, "EINSTELLUNGEN" to Icons.Default.Settings).forEach { (label, icon) -> Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(6.dp)) { Icon(icon, null, tint = Cyan, modifier = Modifier.size(22.dp)); Text(label, color = TextMain, fontSize = 7.sp) } } } }

@Composable private fun ApiDialog(key: String, save: (String) -> Unit, close: () -> Unit) { var value by remember(key) { mutableStateOf(key) }; AlertDialog(onDismissRequest = close, title = { Text("JARVIS KI") }, text = { OutlinedTextField(value, { value = it }, singleLine = true, visualTransformation = PasswordVisualTransformation(), label = { Text("OpenAI API-Key") }) }, confirmButton = { TextButton(onClick = { save(value); close() }) { Text("Speichern") } }, dismissButton = { TextButton(onClick = close) { Text("Abbrechen") } }) }
