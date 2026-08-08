package com.etno2500pixel.jarvis.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        MemoryEntity::class,
        ConversationEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class JarvisDatabase : RoomDatabase() {

    abstract fun memoryDao(): MemoryDao

    abstract fun conversationDao(): ConversationDao
}
