package com.etno2500pixel.jarvis.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ConversationDao {

    @Query(
        "SELECT * FROM messages " +
        "ORDER BY createdAt ASC LIMIT 100"
    )
    fun observe(): Flow<List<ConversationEntity>>

    @Query(
        "SELECT * FROM messages " +
        "ORDER BY createdAt DESC LIMIT 20"
    )
    suspend fun recent(): List<ConversationEntity>

    @Insert
    suspend fun insert(message: ConversationEntity): Long

    @Query("DELETE FROM messages")
    suspend fun clear()
}
