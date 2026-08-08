package com.etno2500pixel.jarvis.`data`

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
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class ConversationDao_Impl(
  __db: RoomDatabase,
) : ConversationDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfConversationEntity: EntityInsertAdapter<ConversationEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfConversationEntity = object : EntityInsertAdapter<ConversationEntity>() {
      protected override fun createQuery(): String = "INSERT OR ABORT INTO `messages` (`id`,`role`,`content`,`createdAt`) VALUES (nullif(?, 0),?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: ConversationEntity) {
        statement.bindLong(1, entity.id)
        statement.bindText(2, entity.role)
        statement.bindText(3, entity.content)
        statement.bindLong(4, entity.createdAt)
      }
    }
  }

  public override suspend fun insert(message: ConversationEntity): Long = performSuspending(__db, false, true) { _connection ->
    val _result: Long = __insertAdapterOfConversationEntity.insertAndReturnId(_connection, message)
    _result
  }

  public override fun observe(): Flow<List<ConversationEntity>> {
    val _sql: String = "SELECT * FROM messages ORDER BY createdAt ASC LIMIT 100"
    return createFlow(__db, false, arrayOf("messages")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfRole: Int = getColumnIndexOrThrow(_stmt, "role")
        val _columnIndexOfContent: Int = getColumnIndexOrThrow(_stmt, "content")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _result: MutableList<ConversationEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ConversationEntity
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpRole: String
          _tmpRole = _stmt.getText(_columnIndexOfRole)
          val _tmpContent: String
          _tmpContent = _stmt.getText(_columnIndexOfContent)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          _item = ConversationEntity(_tmpId,_tmpRole,_tmpContent,_tmpCreatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun recent(): List<ConversationEntity> {
    val _sql: String = "SELECT * FROM messages ORDER BY createdAt DESC LIMIT 20"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfRole: Int = getColumnIndexOrThrow(_stmt, "role")
        val _columnIndexOfContent: Int = getColumnIndexOrThrow(_stmt, "content")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _result: MutableList<ConversationEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ConversationEntity
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpRole: String
          _tmpRole = _stmt.getText(_columnIndexOfRole)
          val _tmpContent: String
          _tmpContent = _stmt.getText(_columnIndexOfContent)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          _item = ConversationEntity(_tmpId,_tmpRole,_tmpContent,_tmpCreatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun clear() {
    val _sql: String = "DELETE FROM messages"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
