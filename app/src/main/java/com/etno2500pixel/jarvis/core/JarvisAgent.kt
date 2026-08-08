package com.etno2500pixel.jarvis.core
import com.etno2500pixel.jarvis.ai.LocalAIProvider


import com.etno2500pixel.jarvis.data.ConversationDao
import com.etno2500pixel.jarvis.data.ConversationEntity
import com.etno2500pixel.jarvis.data.MemoryDao

class JarvisAgent(
    private val memoryDao: MemoryDao,
    private val conversationDao: ConversationDao,
    private val learningEngine: LearningEngine
) {

    suspend fun respond(input: String): String {

        val text = input.trim()

        if (text.isEmpty()) {
            return "Ich habe nichts gehört."
        }

        conversationDao.insert(
            ConversationEntity(
                role = "user",
                content = text
            )
        )

        learningEngine.learnFromUser(text)

        val memory =
            memoryDao.search(text.take(80))

        val response = when {

            text.contains("wer bist du", ignoreCase = true) ->
                "Ich bin JARVIS. Mein Gedächtnis und mein Lernsystem sind aktiv."

            text.contains(
                "was weißt du über mich",
                ignoreCase = true
            ) ->
                if (memory.isEmpty()) {
                    "Noch nichts Relevantes zu dieser Frage."
                } else {
                    memory.joinToString("\n") {
                        "• ${it.text}"
                    }
                }

            text.contains("merk dir", ignoreCase = true) ||
            text.contains("merke dir", ignoreCase = true) ->
                "Verstanden. Ich habe diese Information als Erinnerung gespeichert."

            text.contains("lerne", ignoreCase = true) ->
                "Ich lerne aus Korrekturen, Präferenzen und wiederkehrenden Mustern."

            text.equals("hallo", ignoreCase = true) ||
            text.startsWith("hallo ", ignoreCase = true) ->
                "Hallo. Ich bin bereit."

            else ->
                LocalAIProvider().ask(text)
        }

        conversationDao.insert(
            ConversationEntity(
                role = "assistant",
                content = response
            )
        )

        return response
    }
}
