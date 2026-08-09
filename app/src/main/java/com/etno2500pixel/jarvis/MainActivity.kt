package com.etno2500pixel.jarvis

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.room.Room
import com.etno2500pixel.jarvis.ai.LocalAIProvider
import com.etno2500pixel.jarvis.core.JarvisAgent
import com.etno2500pixel.jarvis.core.LearningEngine
import com.etno2500pixel.jarvis.data.JarvisDatabase
import com.etno2500pixel.jarvis.tools.AndroidTools
import com.etno2500pixel.jarvis.voice.VoiceManager
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

private val Bg = Color(0xFF02070D)
private val Panel = Color(0xB3081622)
private val Cyan = Color(0xFF12D9FF)
private val Green = Color(0xFF27F58A)
private val HudText = Color(0xFFE7F8FF)

class MainActivity : ComponentActivity() {
    private lateinit var voice: VoiceManager
    private lateinit var agent: JarvisAgent
    private lateinit var tools: AndroidTools
    private val speech = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { r ->
        r.data?.getStringArrayListExtra("android.speech.extra.RESULTS")?.firstOrNull()?.let { handle(it) }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = Room.databaseBuilder(applicationContext, JarvisDatabase::class.java, "jarvis.db").build()
        voice = VoiceManager(this); tools = AndroidTools(this)
        agent = JarvisAgent(db.memoryDao(), db.conversationDao(), LearningEngine(db.memoryDao()), LocalAIProvider(this))
        setContent { JarvisDashboard({ t, done -> handle(t, done) }, { requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), 10); speech.launch(voice.recognitionIntent()) }) }
    }
    private fun handle(text: String, done: (String) -> Unit = {}) = MainScope().launch {
        val answer = if (!tools.executeSafeCommand(text)) agent.respond(text) else "Erledigt."
        done(answer); voice.speak(answer)
    }
    override fun onDestroy() { voice.release(); super.onDestroy() }
}

@Composable
private fun JarvisDashboard(onSend: (String, (String) -> Unit) -> Unit, onSpeak: () -> Unit) {
    var input by remember { mutableStateOf("") }
    var processing by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }
    val messages = remember { mutableStateListOf("JARVIS|Guten Abend.\nWie kann ich Ihnen helfen?", "USER|Was kannst du?", "JARVIS|Ich kann mit Ihnen sprechen, Fragen beantworten, Erinnerungen speichern, Aufgaben planen, Systeme steuern und vieles mehr.") }
    val listState = rememberLazyListState()
    LaunchedEffect(messages.size) { if (messages.isNotEmpty()) listState.animateScrollToItem(messages.lastIndex) }
    val transition = rememberInfiniteTransition(label = "earth")
    val rotation by transition.animateFloat(initialValue = 0f, targetValue = 360f, animationSpec = infiniteRepeatable(animation = tween(18000, easing = LinearEasing)), label = "earthRotation")
    val pulse by transition.animateFloat(initialValue = .92f, targetValue = 1.06f, animationSpec = infiniteRepeatable(animation = tween(1400), repeatMode = RepeatMode.Reverse), label = "pulse")

    Box(Modifier.fillMaxSize().background(Bg)) {
        Column(Modifier.fillMaxSize().padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                HudButton("☰", Modifier.size(58.dp)) {}
                Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("J A R V I S", color = HudText, fontSize = 30.sp, fontWeight = FontWeight.Light, letterSpacing = 6.sp)
                    Text("JUST A RATHER VERY INTELLIGENT SYSTEM", color = Color(0xFF91B8C7), fontSize = 8.sp, letterSpacing = 1.2.sp)
                }
                Surface(shape = RoundedCornerShape(28.dp), color = Color.Transparent, border = BorderStroke(1.dp, Green)) {
                    Row(Modifier.padding(horizontal = 15.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) { Dot(Green); Spacer(Modifier.width(8.dp)); Text("ONLINE", color = Green, fontSize = 12.sp) }
                }
            }
            Spacer(Modifier.height(8.dp))
            HudPanel(Modifier.fillMaxWidth().height(82.dp)) {
                Text("SYSTEM STATUS", color = Cyan, fontSize = 11.sp, letterSpacing = 1.5.sp)
                Row(verticalAlignment = Alignment.CenterVertically) { Dot(Green); Spacer(Modifier.width(8.dp)); Text("ONLINE", color = Green, fontSize = 22.sp) }
                Text("ALLE SYSTEME VERBUNDEN", color = Color(0xFFA9CBD6), fontSize = 12.sp)
            }
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(7.dp)) { Status("⌁", "KOMMUNIKATION", "VERBUNDEN", "STABIL"); Status("▥", "DATENANALYSE", "AKTIV", "87%"); Status("♢", "SICHERHEIT", "GESCHÜTZT", "OPTIMAL"); Status("⚙", "SYSTEME", "NOMINAL", "ALLE SYSTEME OK") }
                Box(Modifier.weight(2.2f).height(390.dp), contentAlignment = Alignment.Center) { Earth(rotation, pulse) }
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(7.dp)) { Status("⌘", "NETZWERK", "STABIL", "PING 12ms"); Status("◎", "ÜBERWACHUNG", "AKTIV", "24/7"); Status("ϟ", "ENERGIE", "OPTIMAL", "100%"); Status("↻", "UPDATES", "AKTUELL", "KEINE UPDATES") }
            }
            Text("AKTIVITÄT", color = Cyan, fontSize = 10.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
            Spacer(Modifier.height(5.dp))
            HudPanel(Modifier.fillMaxWidth().height(82.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("⌖", color = Cyan, fontSize = 35.sp); Spacer(Modifier.width(12.dp)); Column(Modifier.weight(1f)) { Text("DEIN STANDORT", color = Cyan, fontSize = 11.sp); Text("MÜNCHEN, DEUTSCHLAND 🇩🇪", color = HudText, fontSize = 14.sp); Text("KOORDINATEN  48.1351° N, 11.5820° E", color = Color(0xFF8EAAB6), fontSize = 9.sp) }
                    Column(horizontalAlignment = Alignment.End) { Text("☁  14°C", color = HudText, fontSize = 14.sp); Text("KLARER HIMMEL", color = Color(0xFF8EAAB6), fontSize = 9.sp) }
                }
            }
            Spacer(Modifier.height(5.dp))
            LazyColumn(state = listState, modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                items(messages) { msg -> ChatBubble(msg) }
                if (processing) item { Text("JARVIS verarbeitet …", color = Cyan, fontSize = 11.sp) }
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
                RoundAction("⌨", "TASTATUR") {}
                Box(Modifier.size(92.dp).clip(CircleShape).background(Color(0xFF061827)).border(2.dp, Cyan, CircleShape).clickable { onSpeak() }, contentAlignment = Alignment.Center) { Icon(Icons.Default.Mic, null, tint = HudText, modifier = Modifier.size(46.dp)) }
                RoundAction("▥", "SPRACHMODUS") { onSpeak() }
            }
            Text("HALTEN UND SPRECHEN", color = Cyan, fontSize = 9.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
            Spacer(Modifier.height(4.dp))
            Row(Modifier.fillMaxWidth().height(54.dp), horizontalArrangement = Arrangement.SpaceAround) {
                Nav("CHAT", Icons.Default.Chat, true) { }; Nav("SYSTEME", Icons.Default.Settings, false) { }; Nav("AUFGABEN", Icons.Default.Checklist, false) { }; Nav("✦", Icons.Default.AutoAwesome, false) { }; Nav("ERINNERUNGEN", Icons.Default.Notifications, false) { }; Nav("EINSTELLUNGEN", Icons.Default.Settings, false) { showSettings = true }
            }
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(input, { input = it }, Modifier.weight(1f), placeholder = { Text("Nachricht an JARVIS …") }, singleLine = true, shape = RoundedCornerShape(14.dp))
                IconButton(enabled = input.isNotBlank() && !processing, onClick = { val t=input; input=""; messages.add("USER|$t"); processing=true; onSend(t){ r -> messages.add("JARVIS|$r"); processing=false } }) { Icon(Icons.AutoMirrored.Filled.Send, null, tint=Cyan) }
            }
        }
        if (showSettings) AISettings { showSettings = false }
    }
}

@Composable private fun Earth(rotation: Float, pulse: Float) {
    Box(Modifier.size(245.dp), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize().rotate(rotation)) { val c=size.minDimension/2; val center=Offset(c,c); for(i in 0..5) { val r=c*(.7f+i*.045f); drawCircle(Color(0xFF087CFF).copy(alpha=.10f), r, center, style=Stroke(1f)) }; for(i in -2..2) drawOval(Color(0xFF0BC8FF).copy(alpha=.28f), Rect(c*.18f, c*(.35f+i*.14f), c*1.82f, c*(1.65f+i*.14f)), style=Stroke(1.2f)) }
        Canvas(Modifier.size(178.dp).graphicsLayer { scaleX=pulse; scaleY=pulse }) { val c=size.minDimension/2; drawCircle(Color(0xFF052B50), c); drawCircle(Color(0xFF14AFFF).copy(alpha=.75f), c, style=Stroke(3f)); for(i in 0..12) { val a=i*0.48f; val x=c+kotlin.math.cos(a)*c*.65f; val y=c+kotlin.math.sin(a)*c*.65f; drawCircle(Color(0xFF55E9FF), 2.5f, Offset(x,y)) }; drawOval(Color(0xFF47D7FF).copy(alpha=.45f), Rect(c*.35f,c*.1f,c*1.65f,c*1.9f), style=Stroke(2f)); drawOval(Color(0xFF3AC7FF).copy(alpha=.35f), Rect(c*.05f,c*.45f,c*1.95f,c*1.55f), style=Stroke(1.5f)) }
    }
}
@Composable private fun Status(icon:String,title:String,value:String,sub:String){ HudPanel(Modifier.fillMaxWidth().height(88.dp)){ Row(verticalAlignment=Alignment.CenterVertically){ Text(icon,color=Cyan,fontSize=25.sp,modifier=Modifier.width(34.dp)); Column{ Text(title,color=HudText,fontSize=10.sp); Text(value,color=Green,fontSize=11.sp,fontWeight=FontWeight.Bold); Text(sub,color=Color(0xFF9CB7C2),fontSize=9.sp) } } } }
@Composable private fun HudPanel(modifier:Modifier,content:@Composable ColumnScope.()->Unit){ Surface(modifier,shape=RoundedCornerShape(14.dp),color=Panel,border=BorderStroke(1.dp,Color(0xFF0C7A9C))){ Column(Modifier.padding(10.dp),horizontalAlignment=Alignment.CenterHorizontally,content=content) } }
@Composable private fun HudButton(t:String,m:Modifier,onClick:()->Unit){ Surface(m.clickable{onClick()},color=Color.Transparent,border=BorderStroke(1.dp,Cyan),shape=CircleShape){Box(contentAlignment=Alignment.Center){Text(t,color=HudText,fontSize=24.sp)}} }
@Composable private fun Dot(c:Color){ Box(Modifier.size(9.dp).clip(CircleShape).background(c)) }
@Composable private fun ChatBubble(msg:String){ val p=msg.split("|",limit=2); val user=p.firstOrNull()=="USER"; Row(Modifier.fillMaxWidth(),horizontalArrangement=if(user)Arrangement.End else Arrangement.Start){ Surface(shape=RoundedCornerShape(14.dp),color=if(user)Color(0xFF071E34) else Color(0xFF07131F),border=BorderStroke(1.dp,Color(0xFF075B83)),modifier=Modifier.widthIn(max=340.dp)){ Column(Modifier.padding(10.dp)){Text(if(user)"SIE" else "JARVIS",color=Cyan,fontSize=9.sp);Text(p.getOrElse(1){""},color=HudText,fontSize=13.sp)} } } }
@Composable private fun RoundAction(icon:String,label:String,onClick:()->Unit){ Column(horizontalAlignment=Alignment.CenterHorizontally,modifier=Modifier.clickable{onClick()}){ Box(Modifier.size(55.dp).clip(CircleShape).border(1.dp,Cyan,CircleShape),contentAlignment=Alignment.Center){Text(icon,color=Cyan,fontSize=24.sp)}; Text(label,color=Color(0xFF72B8CA),fontSize=8.sp) } }
@Composable private fun Nav(label:String,icon:androidx.compose.ui.graphics.vector.ImageVector,active:Boolean,onClick:()->Unit){ Column(horizontalAlignment=Alignment.CenterHorizontally,modifier=Modifier.clickable{onClick()}){Icon(icon,null,tint=if(active)HudText else Color(0xFF5F9AAD));Text(label,color=if(active)HudText else Color(0xFF6C9AAA),fontSize=7.sp)} }

@Composable private fun AISettings(onClose:()->Unit){
    val ctx=androidx.compose.ui.platform.LocalContext.current
    val p=remember{ctx.getSharedPreferences("jarvis_ai",0)}
    var endpoint by remember{mutableStateOf(p.getString("endpoint","")?:"")}; var model by remember{mutableStateOf(p.getString("model","")?:"")}; var key by remember{mutableStateOf(p.getString("api_key","")?:"")}
    AlertDialog(onDismissRequest=onClose,title={Text("EXTERNE KI-SCHNITTSTELLE")},text={Column(verticalArrangement=Arrangement.spacedBy(8.dp)){Text("OpenAI-kompatibler Endpunkt",fontSize=11.sp);OutlinedTextField(endpoint,{endpoint=it},singleLine=true,placeholder={Text("https://…/v1/chat/completions")});OutlinedTextField(model,{model=it},singleLine=true,placeholder={Text("Modell")});OutlinedTextField(key,{key=it},singleLine=true,placeholder={Text("API-Schlüssel")})}},confirmButton={Button(onClick={p.edit().putString("endpoint",endpoint.trim()).putString("model",model.trim()).putString("api_key",key.trim()).apply(); onClose()}){Text("SPEICHERN")}},dismissButton={TextButton(onClick=onClose){Text("ABBRECHEN")}})
}