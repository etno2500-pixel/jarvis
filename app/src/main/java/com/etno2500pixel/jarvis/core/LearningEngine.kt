package com.etno2500pixel.jarvis.core

import com.etno2500pixel.jarvis.data.MemoryDao
import com.etno2500pixel.jarvis.data.MemoryEntity

class LearningEngine(
    private val memoryDao: MemoryDao
) {

    private val sensitive = listOf(
        "passwort",
        "password",
        "pin",
        "bank",
        "kreditkarte",
        "api key",
        "apikey",
        "token",
        "geheim",
        "secret"
    )

    suspend fun learnFromUser(text: String) {

        val lower = text.lowercase()

        if (sensitive.any { lower.contains(it) }) {
            return
        }

        val patterns = listOf(
            "merk dir",
            "merke dir",
            "denk daran",
            "ich mag",
            "ich bevorzuge",
            "mir ist lieber",
            "immer wenn"
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

    suspend fun learnCorrection(
        original: String,
        correction: String
    ) {

        val combined =
            "${original.lowercase()} ${correction.lowercase()}"

        if (sensitive.any { combined.contains(it) }) {
            return
        }

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
