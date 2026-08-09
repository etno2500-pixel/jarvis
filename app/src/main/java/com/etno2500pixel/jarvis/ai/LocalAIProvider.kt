package com.etno2500pixel.jarvis.ai

import android.content.Context
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class LocalAIProvider(private val context: Context) : AIProvider {
    private val client = OkHttpClient()

    override suspend fun ask(prompt: String): String {
        val prefs = context.getSharedPreferences("jarvis_ai", Context.MODE_PRIVATE)
        val endpoint = prefs.getString("endpoint", "")?.trim().orEmpty()
        val apiKey = prefs.getString("api_key", "")?.trim().orEmpty()
        val model = prefs.getString("model", "")?.trim().orEmpty()

        if (endpoint.isBlank() || apiKey.isBlank() || model.isBlank()) {
            return "Die externe KI-Schnittstelle ist noch nicht eingerichtet. Öffne Einstellungen und hinterlege API-Endpunkt, Modell und API-Schlüssel."
        }

        return try {
            val body = JSONObject()
                .put("model", model)
                .put("messages", org.json.JSONArray().put(
                    JSONObject().put("role", "user").put("content", prompt)
                ))
                .put("temperature", 0.7)
                .toString()

            val request = Request.Builder()
                .url(endpoint)
                .addHeader("Authorization", "Bearer $apiKey")
                .addHeader("Content-Type", "application/json")
                .post(body.toRequestBody("application/json".toMediaType()))
                .build()

            client.newCall(request).execute().use { response ->
                val raw = response.body?.string().orEmpty()
                if (!response.isSuccessful) return "KI-Fehler ${response.code}: ${raw.take(180)}"
                val json = JSONObject(raw)
                json.optJSONArray("choices")?.optJSONObject(0)
                    ?.optJSONObject("message")?.optString("content")
                    ?.takeIf { it.isNotBlank() }
                    ?: json.optString("response").takeIf { it.isNotBlank() }
                    ?: "Die KI hat keine Antwort geliefert."
            }
        } catch (e: Exception) {
            "Verbindung zur externen KI fehlgeschlagen: ${e.message ?: "unbekannter Fehler"}"
        }
    }
}