package com.etno2500pixel.jarvis.`data`

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import javax.`annotation`.processing.Generated
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class MemoryDao_Impl(
  __db: RoomDatabase,
) : MemoryDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfMemoryEntity: EntityInsertAdapter<MemoryEntity>

  private val __deleteAdapterOfMemoryEntity: EntityDeleteOrUpdateAdapter<MemoryEntity>

  private val __updateAdapterOfMemoryEntity: EntityDeleteOrUpdateAdapter<MemoryEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfMemoryEntity = object : EntityInsertAdapter<MemoryEntity>() {
      protected override fun createQuery(): String = "INSERT OR ABORT INTO `memories` (`id`,`text`,`category`,`importance`,`source`,`createdAt`,`lastUsedAt`) VALUES (nullif(?, 0),?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: MemoryEntity) {
        statement.bindLong(1, entity.id)
        statement.bindText(2, entity.text)
        statement.bindText(3, entity.category)
        statement.bindLong(4, entity.importance.toLong())
        statement.bindText(5, entity.source)
        statement.bindLong(6, entity.createdAt)
        statement.bindLong(7, entity.lastUsedAt)
      }
    }
    this.__deleteAdapterOfMemoryEntity = object : EntityDeleteOrUpdateAdapter<MemoryEntity>() {
      protected override fun createQuery(): String = "DELETE FROM `memories` WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: MemoryEntity) {
        statement.bindLong(1, entity.id)
      }
    }
    this.__updateAdapterOfMemoryEntity = object : EntityDeleteOrUpdateAdapter<MemoryEntity>() {
      protected override fun createQuery(): String = "UPDATE OR ABORT `memories` SET `id` = ?,`text` = ?,`category` = ?,`importance` = ?,`source` = ?,`createdAt` = ?,`lastUsedAt` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: MemoryEntity) {
        statement.bindLong(1, entity.id)
        statement.bindText(2, entity.text)
        statement.bindText(3, entity.category)
        statement.bindLong(4, entity.importance.toLong())
        statement.bindText(5, entity.source)
        statement.bindLong(6, entity.createdAt)
        statement.bindLong(7, entity.lastUsedAt)
        statement.bindLong(8, entity.id)
      }
    }
  }

  public override suspend fun insert(memory: MemoryEntity): Long = performSuspending(__db, false, true) { _connection ->
    val _result: Long = __insertAdapterOfMemoryEntity.insertAndReturnId(_connection, memory)
    _result
  }

  public override suspend fun delete(memory: MemoryEntity): Unit = performSuspending(__db, false, true) { _connection ->
    __deleteAdapterOfMemoryEntity.handle(_connection, memory)
  }

  public override suspend fun update(memory: MemoryEntity): Unit = performSuspending(__db, false, true) { _connection ->
    __updateAdapterOfMemoryEntity.handle(_connection, memory)
  }

  public override fun observe(): Flow<List<MemoryEntity>> {
    val _sql: String = "SELECT * FROM memories ORDER BY importance DESC, lastUsedAt DESC LIMIT 50"
    return createFlow(__db, false, arrayOf("memories")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfText: Int = getColumnIndexOrThrow(_stmt, "text")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfImportance: Int = getColumnIndexOrThrow(_stmt, "importance")
        val _columnIndexOfSource: Int = getColumnIndexOrThrow(_stmt, "source")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfLastUsedAt: Int = getColumnIndexOrThrow(_stmt, "lastUsedAt")
        val _result: MutableList<MemoryEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: MemoryEntity
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpText: String
          _tmpText = _stmt.getText(_columnIndexOfText)
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpImportance: Int
          _tmpImportance = _stmt.getLong(_columnIndexOfImportance).toInt()
          val _tmpSource: String
          _tmpSource = _stmt.getText(_columnIndexOfSource)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpLastUsedAt: Long
          _tmpLastUsedAt = _stmt.getLong(_columnIndexOfLastUsedAt)
          _item = MemoryEntity(_tmpId,_tmpText,_tmpCategory,_tmpImportance,_tmpSource,_tmpCreatedAt,_tmpLastUsedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun search(query: String): List<MemoryEntity> {
    val _sql: String = "SELECT * FROM memories WHERE text LIKE '%' || ? || '%' ORDER BY importance DESC LIMIT 10"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, query)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfText: Int = getColumnIndexOrThrow(_stmt, "text")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfImportance: Int = getColumnIndexOrThrow(_stmt, "importance")
        val _columnIndexOfSource: Int = getColumnIndexOrThrow(_stmt, "source")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfLastUsedAt: Int = getColumnIndexOrThrow(_stmt, "lastUsedAt")
        val _result: MutableList<MemoryEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: MemoryEntity
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpText: String
          _tmpText = _stmt.getText(_columnIndexOfText)
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpImportance: Int
          _tmpImportance = _stmt.getLong(_columnIndexOfImportance).toInt()
          val _tmpSource: String
          _tmpSource = _stmt.getText(_columnIndexOfSource)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpLastUsedAt: Long
          _tmpLastUsedAt = _stmt.getLong(_columnIndexOfLastUsedAt)
          _item = MemoryEntity(_tmpId,_tmpText,_tmpCategory,_tmpImportance,_tmpSource,_tmpCreatedAt,_tmpLastUsedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
