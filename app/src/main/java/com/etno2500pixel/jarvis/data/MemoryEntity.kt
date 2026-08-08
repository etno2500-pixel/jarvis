package com.etno2500pixel.jarvis.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "memories")
data class MemoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val text: String,

    val category: String,

    val importance: Int = 1,

    val source: String = "user",

    val createdAt: Long = System.currentTimeMillis(),

    val lastUsedAt: Long = System.currentTimeMillis()
)
