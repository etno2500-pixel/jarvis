package com.etno2500pixel.jarvis.ai

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

class RemoteAIProvider(
    private val apiKeyProvider: () -> String,
    private val model: String = "gpt-5-mini"
) : AIProvider {
    private val client = OkHttpClient()

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

        val request = Request.Builder()
            .url("https://api.openai.com/v1/responses")
            .header("Authorization", "Bearer $apiKey")
            .header("Content-Type", "application/json")
            .post(body.toString().toRequestBody("application/json".toMediaType()))
            .build()

        try {
            client.newCall(request).execute().use { response ->
                val result = response.body?.string().orEmpty()
                if (!response.isSuccessful) {
                    return@withContext "JARVIS: KI-Fehler (${response.code})."
                }
                JSONObject(result).optString("output_text").ifBlank {
                    "JARVIS: Die KI hat keine Antwort geliefert."
                }
            }
        } catch (_: Exception) {
            "JARVIS: Verbindung zur externen KI fehlgeschlagen."
        }
    }
}
