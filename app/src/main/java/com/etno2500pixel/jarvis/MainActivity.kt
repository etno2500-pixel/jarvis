package com.etno2500pixel.jarvis

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
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
    private var voice: VoiceManager? = null
    private var agent: JarvisAgent? = null
    private var tools: AndroidTools? = null
    private val prefs by lazy { getSharedPreferences("jarvis_settings", MODE_PRIVATE) }
    private val speech = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        result.data?.getStringArrayListExtra("android.speech.extra.RESULTS")
            ?.firstOrNull()
            ?.takeIf { it.isNotBlank() }
            ?.let(::handle)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_DEFAULT
        }

        // Rendering must not depend on Room/TTS initialization succeeding.
        runCatching { voice = VoiceManager(applicationContext) }
        runCatching { tools = AndroidTools(this) }
        runCatching {
            val db = Room.databaseBuilder(applicationContext, JarvisDatabase::class.java, "jarvis.db").build()
            agent = JarvisAgent(
                db.memoryDao(),
                db.conversationDao(),
                LearningEngine(db.memoryDao()),
                RemoteAIProvider(apiKeyProvider = { prefs.getString("openai_api_key", "").orEmpty() })
            )
        }

        setContent {
            JarvisTestApp(
                onSend = { text, result -> handle(text, result) },
                onSpeak = {
                    requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), 10)
                    voice?.let { speech.launch(it.recognitionIntent()) }
                }
            )
        }
    }

    private fun handle(text: String, onResult: (String) -> Unit = {}) = MainScope().launch {
        val response = runCatching {
            val localTools = tools
            val localAgent = agent
            when {
                localTools == null || localAgent == null ->
                    "JARVIS: Systeminitialisierung noch nicht verfügbar."
                localTools.executeSafeCommand(text) ->
                    "Erledigt."
                else ->
                    localAgent.respond(text)
            }
        }.getOrElse {
            "JARVIS: Ein interner Fehler ist aufgetreten."
        }
        onResult(response)
        voice?.speak(response)
    }

    override fun onDestroy() {
        runCatching { voice?.release() }
        voice = null
        super.onDestroy()
    }
}
