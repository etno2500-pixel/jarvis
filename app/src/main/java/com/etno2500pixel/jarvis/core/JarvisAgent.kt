package com.etno2500pixel.jarvis.core

import com.etno2500pixel.jarvis.ai.LocalAIProvider
import com.etno2500pixel.jarvis.data.ConversationDao
import com.etno2500pixel.jarvis.data.ConversationEntity
import com.etno2500pixel.jarvis.data.MemoryDao

class JarvisAgent(
    private val memoryDao: MemoryDao,
    private val conversationDao: ConversationDao,
    private val learningEngine: LearningEngine,
    private val aiProvider: LocalAIProvider
) {
    suspend fun respond(input: String): String {
        val text = input.trim()
        if (text.isEmpty()) return "Ich habe nichts gehört."
        conversationDao.insert(ConversationEntity(role = "user", content = text))
        learningEngine.learnFromUser(text)
        val memory = memoryDao.search(text.take(80))
        val response = when {
            text.contains("wer bist du", true) -> "Ich bin JARVIS. Mein Gedächtnis und mein Lernsystem sind aktiv."
            text.contains("was weißt du über mich", true) -> if (memory.isEmpty()) "Noch nichts Relevantes zu dieser Frage." else memory.joinToString("\n") { "• ${it.text}" }
            text.contains("merk dir", true) || text.contains("merke dir", true) -> "Verstanden. Ich habe diese Information als Erinnerung gespeichert."
            text.contains("lerne", true) -> "Ich lerne aus Korrekturen, Präferenzen und wiederkehrenden Mustern."
            text.equals("hallo", true) || text.startsWith("hallo ", true) -> "Hallo. JARVIS ist online."
            else -> aiProvider.ask(text)
        }
        conversationDao.insert(ConversationEntity(role = "assistant", content = response))
        return response
    }
}