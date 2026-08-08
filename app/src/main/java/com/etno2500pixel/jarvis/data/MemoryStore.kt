package com.etno2500pixel.jarvis.data

class MemoryStore {

    private val memories = mutableListOf<String>()

    fun remember(text: String) {
        if (text.isNotBlank()) {
            memories.add(text.trim())
        }
    }

    fun search(query: String): List<String> {
        if (query.isBlank()) return memories.toList()

        return memories.filter {
            it.contains(query, ignoreCase = true)
        }
    }

    fun all(): List<String> = memories.toList()

    fun clear() {
        memories.clear()
    }
}
