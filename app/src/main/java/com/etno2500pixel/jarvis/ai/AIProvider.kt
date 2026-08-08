package com.etno2500pixel.jarvis.ai

interface AIProvider {
    suspend fun ask(prompt: String): String
}
