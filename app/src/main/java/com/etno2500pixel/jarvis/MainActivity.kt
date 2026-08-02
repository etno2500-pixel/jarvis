package com.etno2500pixel.jarvis

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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

    private val speech =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->

            val text =
                result.data
                    ?.getStringArrayListExtra("android.speech.extra.RESULTS")
                    ?.firstOrNull()

            if (!text.isNullOrBlank()) {
                handle(text)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = Room.databaseBuilder(
            applicationContext,
            JarvisDatabase::class.java,
            "jarvis.db"
        ).build()

        voice = VoiceManager(this)

        tools = AndroidTools(this)

        agent = JarvisAgent(
            db.memoryDao(),
            db.conversationDao(),
            LearningEngine(db.memoryDao())
        )

        setContent {

            JarvisApp(
                onSend = ::handle,

                onSpeak = {
                    requestPermissions(
                        arrayOf(Manifest.permission.RECORD_AUDIO),
                        10
                    )

                    speech.launch(
                        voice.recognitionIntent()
                    )
                }
            )
        }
    }

    private fun handle(text: String) {

        MainScope().launch {

            if (!tools.executeSafeCommand(text)) {

                val response = agent.respond(text)

                voice.speak(response)

            } else {

                voice.speak("Erledigt.")
            }
        }
    }

    override fun onDestroy() {

        voice.release()

        super.onDestroy()
    }
}

@Composable
private fun JarvisApp(
    onSend: (String) -> Unit,
    onSpeak: () -> Unit
) {

    var input by remember {
        mutableStateOf("")
    }

    val messages = remember {
        mutableStateListOf(
            "JARVIS: System online. Lernkern aktiv."
        )
    }

    MaterialTheme(
        colorScheme = darkColorScheme(
            background = Color(0xFF05070B),
            surface = Color(0xFF0A1119),
            primary = Color(0xFF5AD9FF)
        )
    ) {

        Surface(
            modifier = Modifier.fillMaxSize()
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp),

                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(
                    modifier = Modifier.height(24.dp)
                )

                Text(
                    text = "J.A.R.V.I.S.",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "LEARNING AGENT",
                    fontSize = 11.sp,
                    color = Color(0xFF66DFFF)
                )

                Spacer(
                    modifier = Modifier.height(22.dp)
                )

                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF0B2430)),

                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = "J",
                        fontSize = 70.sp,
                        color = Color(0xFF5AD9FF),
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(
                    modifier = Modifier.height(20.dp)
                )

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),

                    contentPadding = PaddingValues(
                        vertical = 8.dp
                    )
                ) {

                    items(messages) { message ->

                        Text(
                            text = message,
                            modifier = Modifier.padding(8.dp),
                            color = Color(0xFFE4F7FF)
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    OutlinedTextField(
                        value = input,
                        onValueChange = {
                            input = it
                        },

                        modifier = Modifier.weight(1f),

                        placeholder = {
                            Text("Sag etwas zu JARVIS …")
                        },

                        singleLine = true
                    )

                    IconButton(
                        onClick = {

                            if (input.isNotBlank()) {

                                val text = input

                                messages.add(
                                    "DU: $text"
                                )

                                onSend(text)

                                input = ""
                            }
                        }
                    ) {

                        Icon(
                            Icons.Default.Send,
                            contentDescription = "Senden"
                        )
                    }

                    IconButton(
                        onClick = onSpeak
                    ) {

                        Icon(
                            Icons.Default.Mic,
                            contentDescription = "Sprechen"
                        )
                    }
                }
            }
        }
    }
}
