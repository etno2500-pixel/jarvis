package com.etno2500pixel.jarvis.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class ConversationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val role: String,

    val content: String,

    val createdAt: Long = System.currentTimeMillis()
)
