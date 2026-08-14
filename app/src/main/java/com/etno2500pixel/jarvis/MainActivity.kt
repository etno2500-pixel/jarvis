package com.etno2500pixel.jarvis

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.etno2500pixel.jarvis.ai.RemoteAIProvider
import com.etno2500pixel.jarvis.core.JarvisAgent
import com.etno2500pixel.jarvis.core.LearningEngine
import com.etno2500pixel.jarvis.data.JarvisDatabase
import com.etno2500pixel.jarvis.tools.AndroidTools
import com.etno2500pixel.jarvis.voice.VoiceManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

        // First draw must not depend on Room, TTS or other optional services.
        // Android recommends keeping expensive initialization out of the critical
        // launch path; the actual services are initialized after the UI is shown.
        setContent {
            JarvisTestApp(
                onSend = { text, result -> handle(text, result) },
                onSpeak = {
                    requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), 10)
                    ensureVoice()?.let { speech.launch(it.recognitionIntent()) }
                }
            )
        }

        lifecycleScope.launch(Dispatchers.IO) {
            val initialized = runCatching {
                val db = Room.databaseBuilder(
                    applicationContext,
                    JarvisDatabase::class.java,
                    "jarvis.db"
                ).build()
                val localTools = AndroidTools(applicationContext)
                val localAgent = JarvisAgent(
                    db.memoryDao(),
                    db.conversationDao(),
                    LearningEngine(db.memoryDao()),
                    RemoteAIProvider(apiKeyProvider = {
                        prefs.getString("openai_api_key", "").orEmpty()
                    })
                )
                Triple(db, localTools, localAgent)
            }

            initialized.onSuccess { (_, localTools, localAgent) ->
                tools = localTools
                agent = localAgent
            }
        }
    }

    private fun ensureVoice(): VoiceManager? {
        if (voice == null) {
            voice = runCatching { VoiceManager(applicationContext) }.getOrNull()
        }
        return voice
    }

    private fun handle(text: String, onResult: (String) -> Unit = {}) {
        lifecycleScope.launch {
            val response = withContext(Dispatchers.IO) {
                runCatching {
                    val localTools = tools
                    val localAgent = agent
                    when {
                        localTools == null || localAgent == null ->
                            "JARVIS: System wird noch initialisiert."
                        localTools.executeSafeCommand(text) ->
                            "Erledigt."
                        else ->
                            localAgent.respond(text)
                    }
                }.getOrElse {
                    "JARVIS: Ein interner Fehler ist aufgetreten."
                }
            }
            onResult(response)
            ensureVoice()?.speak(response)
        }
    }

    override fun onDestroy() {
        runCatching { voice?.release() }
        voice = null
        super.onDestroy()
    }
}
