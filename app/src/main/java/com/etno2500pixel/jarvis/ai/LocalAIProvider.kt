package com.etno2500pixel.jarvis.ai

class LocalAIProvider : AIProvider {

    override suspend fun ask(prompt: String): String {
        return when {
            prompt.isBlank() ->
                "Ich höre."

            prompt.contains("hallo", true) ->
                "Hallo. JARVIS ist online."

            prompt.contains("wer bist du", true) ->
                "Ich bin JARVIS, dein persönlicher KI-Assistent."

            else ->
                "Ich habe deine Anfrage erhalten. Die externe KI-Schnittstelle kann jetzt angeschlossen werden."
        }
    }
}
