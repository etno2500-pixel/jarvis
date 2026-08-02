package com.etno2500pixel.jarvis.core

import com.etno2500pixel.jarvis.data.ConversationDao
import com.etno2500pixel.jarvis.data.ConversationEntity
import com.etno2500pixel.jarvis.data.MemoryDao

class JarvisAgent(
    private val memoryDao: MemoryDao,
    private val conversationDao: ConversationDao,
    private val learningEngine: LearningEngine
) {
    suspend fun respond(input: String): String {
        conversationDao.insert(ConversationEntity(role = "user", content = input))
        learningEngine.learnFromUser(input)

        val memory = memoryDao.search(input.take(80))
        val response = when {
            input.contains("wer bist du", true) ->
                "Ich bin JARVIS. Mein Gedächtnis und mein Lernsystem sind aktiv."
            input.contains("was weißt du über mich", true) ->
                if (memory.isEmpty()) "Noch nichts Relevantes zu dieser Frage."
                else memory.joinToString("\n") { "• ${it.text}" }
            input.contains("merk dir", true) ->
                "Verstanden. Ich habe diese Information als Erinnerung gespeichert."
            input.contains("lerne", true) ->
                "Ich lerne aus Korrekturen, Präferenzen und wiederkehrenden Mustern."
            else ->
                "Ich habe deinen Befehl verstanden. Die KI-Schnittstelle kann als nächster Schritt angeschlossen werden."
        }

        conversationDao.insert(ConversationEntity(role = "assistant", content = response))
        return response
    }
}
