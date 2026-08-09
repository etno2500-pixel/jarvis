package com.etno2500pixel.jarvis

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.room.Room
import com.etno2500pixel.jarvis.ai.RemoteAIProvider
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
    private val prefs by lazy { getSharedPreferences("jarvis_settings", MODE_PRIVATE) }
    private val speech = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        result.data?.getStringArrayListExtra("android.speech.extra.RESULTS")?.firstOrNull()?.takeIf { it.isNotBlank() }?.let(::handle)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Die Android-Zurück/Home/Letzte-Apps-Leiste soll das JARVIS-Abbild nicht überdecken.
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        val db = Room.databaseBuilder(applicationContext, JarvisDatabase::class.java, "jarvis.db").build()
        voice = VoiceManager(this)
        tools = AndroidTools(this)
        agent = JarvisAgent(
            db.memoryDao(),
            db.conversationDao(),
            LearningEngine(db.memoryDao()),
            RemoteAIProvider(apiKeyProvider = { prefs.getString("openai_api_key", "").orEmpty() })
        )
        setContent {
            JarvisTestApp(
                onSend = { text, result -> handle(text, result) },
                onSpeak = {
                    requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), 10)
                    speech.launch(voice.recognitionIntent())
                }
            )
        }
    }

    private fun handle(text: String, onResult: (String) -> Unit = {}) = MainScope().launch {
        val response = if (!tools.executeSafeCommand(text)) agent.respond(text) else "Erledigt."
        onResult(response)
        voice.speak(response)
    }

    override fun onDestroy() {
        voice.release()
        super.onDestroy()
    }
}
