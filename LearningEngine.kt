package com.etno2500pixel.jarvis.core

import com.etno2500pixel.jarvis.data.MemoryDao
import com.etno2500pixel.jarvis.data.MemoryEntity

/**
 * Converts explicit corrections and low-risk preferences into durable memories.
 * It intentionally does NOT store secrets, passwords, payment data or security keys.
 */
class LearningEngine(private val memoryDao: MemoryDao) {

    private val sensitive = listOf(
        "passwort", "password", "pin", "bank", "kreditkarte",
        "api key", "apikey", "token", "geheim", "secret"
    )

    suspend fun learnFromUser(text: String) {
        val lower = text.lowercase()
        if (sensitive.any { lower.contains(it) }) return

        val patterns = listOf(
            "merk dir", "merke dir", "denk daran", "ich mag",
            "ich bevorzuge", "mir ist lieber", "immer wenn"
        )

        if (patterns.any { lower.contains(it) }) {
            memoryDao.insert(
                MemoryEntity(
                    text = text.trim(),
                    category = "preference",
                    importance = 3,
                    source = "explicit"
                )
            )
        }
    }

    suspend fun learnCorrection(original: String, correction: String) {
        if (sensitive.any { original.lowercase().contains(it) || correction.lowercase().contains(it) }) return
        memoryDao.insert(
            MemoryEntity(
                text = "Korrektur: \"$original\" -> \"$correction\"",
                category = "correction",
                importance = 4,
                source = "feedback"
            )
        )
    }
}
