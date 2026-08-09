package com.etno2500pixel.jarvis.ai

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class RemoteAIProvider(
    private val apiKeyProvider: () -> String,
    private val model: String = "gpt-5-mini"
) : AIProvider {
    override suspend fun ask(prompt: String): String = withContext(Dispatchers.IO) {
        val apiKey = apiKeyProvider().trim()
        if (apiKey.isBlank()) {
            return@withContext "JARVIS: Bitte zuerst den KI-API-Key in den Einstellungen hinterlegen."
        }

        val body = JSONObject()
            .put("model", model)
            .put("input", JSONArray().put(
                JSONObject()
                    .put("role", "user")
                    .put("content", JSONArray().put(
                        JSONObject()
                            .put("type", "input_text")
                            .put("text", prompt)
                    ))
            ))

        try {
            val connection = (URL("https://api.openai.com/v1/responses").openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                connectTimeout = 15_000
                readTimeout = 30_000
                doOutput = true
                setRequestProperty("Authorization", "Bearer $apiKey")
                setRequestProperty("Content-Type", "application/json")
            }

            connection.outputStream.use { output ->
                output.write(body.toString().toByteArray(Charsets.UTF_8))
            }

            val status = connection.responseCode
            val stream = if (status in 200..299) connection.inputStream else connection.errorStream
            val result = stream?.bufferedReader()?.use { it.readText() }.orEmpty()
            connection.disconnect()

            if (status !in 200..299) {
                return@withContext "JARVIS: KI-Fehler ($status)."
            }

            JSONObject(result).optString("output_text").ifBlank {
                "JARVIS: Die KI hat keine Antwort geliefert."
            }
        } catch (_: Exception) {
            "JARVIS: Verbindung zur externen KI fehlgeschlagen."
        }
    }
}
