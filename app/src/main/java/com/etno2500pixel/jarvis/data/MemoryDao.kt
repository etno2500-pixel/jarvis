package com.etno2500pixel.jarvis.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MemoryDao {

    @Query(
        "SELECT * FROM memories " +
        "ORDER BY importance DESC, lastUsedAt DESC LIMIT 50"
    )
    fun observe(): Flow<List<MemoryEntity>>

    @Query(
        "SELECT * FROM memories " +
        "WHERE text LIKE '%' || :query || '%' " +
        "ORDER BY importance DESC LIMIT 10"
    )
    suspend fun search(query: String): List<MemoryEntity>

    @Insert
    suspend fun insert(memory: MemoryEntity): Long

    @Update
    suspend fun update(memory: MemoryEntity)

    @Delete
    suspend fun delete(memory: MemoryEntity)
}
