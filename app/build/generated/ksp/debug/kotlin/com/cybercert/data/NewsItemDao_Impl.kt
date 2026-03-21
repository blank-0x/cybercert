package com.cybercert.`data`

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.EntityUpsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.cybercert.model.NewsItem
import javax.`annotation`.processing.Generated
import kotlin.Boolean
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
public class NewsItemDao_Impl(
  __db: RoomDatabase,
) : NewsItemDao {
  private val __db: RoomDatabase

  private val __upsertAdapterOfNewsItem: EntityUpsertAdapter<NewsItem>
  init {
    this.__db = __db
    this.__upsertAdapterOfNewsItem = EntityUpsertAdapter<NewsItem>(object :
        EntityInsertAdapter<NewsItem>() {
      protected override fun createQuery(): String =
          "INSERT INTO `news_items` (`id`,`title`,`description`,`url`,`imageUrl`,`source`,`publishedAt`,`isRead`,`isBookmarked`,`cachedAt`) VALUES (?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: NewsItem) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.title)
        statement.bindText(3, entity.description)
        statement.bindText(4, entity.url)
        val _tmpImageUrl: String? = entity.imageUrl
        if (_tmpImageUrl == null) {
          statement.bindNull(5)
        } else {
          statement.bindText(5, _tmpImageUrl)
        }
        statement.bindText(6, entity.source)
        statement.bindLong(7, entity.publishedAt)
        val _tmp: Int = if (entity.isRead) 1 else 0
        statement.bindLong(8, _tmp.toLong())
        val _tmp_1: Int = if (entity.isBookmarked) 1 else 0
        statement.bindLong(9, _tmp_1.toLong())
        statement.bindLong(10, entity.cachedAt)
      }
    }, object : EntityDeleteOrUpdateAdapter<NewsItem>() {
      protected override fun createQuery(): String =
          "UPDATE `news_items` SET `id` = ?,`title` = ?,`description` = ?,`url` = ?,`imageUrl` = ?,`source` = ?,`publishedAt` = ?,`isRead` = ?,`isBookmarked` = ?,`cachedAt` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: NewsItem) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.title)
        statement.bindText(3, entity.description)
        statement.bindText(4, entity.url)
        val _tmpImageUrl: String? = entity.imageUrl
        if (_tmpImageUrl == null) {
          statement.bindNull(5)
        } else {
          statement.bindText(5, _tmpImageUrl)
        }
        statement.bindText(6, entity.source)
        statement.bindLong(7, entity.publishedAt)
        val _tmp: Int = if (entity.isRead) 1 else 0
        statement.bindLong(8, _tmp.toLong())
        val _tmp_1: Int = if (entity.isBookmarked) 1 else 0
        statement.bindLong(9, _tmp_1.toLong())
        statement.bindLong(10, entity.cachedAt)
        statement.bindText(11, entity.id)
      }
    })
  }

  public override suspend fun upsertAll(items: List<NewsItem>): Unit = performSuspending(__db,
      false, true) { _connection ->
    __upsertAdapterOfNewsItem.upsert(_connection, items)
  }

  public override fun allNews(): Flow<List<NewsItem>> {
    val _sql: String = "SELECT * FROM news_items ORDER BY publishedAt DESC"
    return createFlow(__db, false, arrayOf("news_items")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfUrl: Int = getColumnIndexOrThrow(_stmt, "url")
        val _columnIndexOfImageUrl: Int = getColumnIndexOrThrow(_stmt, "imageUrl")
        val _columnIndexOfSource: Int = getColumnIndexOrThrow(_stmt, "source")
        val _columnIndexOfPublishedAt: Int = getColumnIndexOrThrow(_stmt, "publishedAt")
        val _columnIndexOfIsRead: Int = getColumnIndexOrThrow(_stmt, "isRead")
        val _columnIndexOfIsBookmarked: Int = getColumnIndexOrThrow(_stmt, "isBookmarked")
        val _columnIndexOfCachedAt: Int = getColumnIndexOrThrow(_stmt, "cachedAt")
        val _result: MutableList<NewsItem> = mutableListOf()
        while (_stmt.step()) {
          val _item: NewsItem
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpUrl: String
          _tmpUrl = _stmt.getText(_columnIndexOfUrl)
          val _tmpImageUrl: String?
          if (_stmt.isNull(_columnIndexOfImageUrl)) {
            _tmpImageUrl = null
          } else {
            _tmpImageUrl = _stmt.getText(_columnIndexOfImageUrl)
          }
          val _tmpSource: String
          _tmpSource = _stmt.getText(_columnIndexOfSource)
          val _tmpPublishedAt: Long
          _tmpPublishedAt = _stmt.getLong(_columnIndexOfPublishedAt)
          val _tmpIsRead: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsRead).toInt()
          _tmpIsRead = _tmp != 0
          val _tmpIsBookmarked: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfIsBookmarked).toInt()
          _tmpIsBookmarked = _tmp_1 != 0
          val _tmpCachedAt: Long
          _tmpCachedAt = _stmt.getLong(_columnIndexOfCachedAt)
          _item =
              NewsItem(_tmpId,_tmpTitle,_tmpDescription,_tmpUrl,_tmpImageUrl,_tmpSource,_tmpPublishedAt,_tmpIsRead,_tmpIsBookmarked,_tmpCachedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getAllOnce(): List<NewsItem> {
    val _sql: String = "SELECT * FROM news_items ORDER BY publishedAt DESC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfUrl: Int = getColumnIndexOrThrow(_stmt, "url")
        val _columnIndexOfImageUrl: Int = getColumnIndexOrThrow(_stmt, "imageUrl")
        val _columnIndexOfSource: Int = getColumnIndexOrThrow(_stmt, "source")
        val _columnIndexOfPublishedAt: Int = getColumnIndexOrThrow(_stmt, "publishedAt")
        val _columnIndexOfIsRead: Int = getColumnIndexOrThrow(_stmt, "isRead")
        val _columnIndexOfIsBookmarked: Int = getColumnIndexOrThrow(_stmt, "isBookmarked")
        val _columnIndexOfCachedAt: Int = getColumnIndexOrThrow(_stmt, "cachedAt")
        val _result: MutableList<NewsItem> = mutableListOf()
        while (_stmt.step()) {
          val _item: NewsItem
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpUrl: String
          _tmpUrl = _stmt.getText(_columnIndexOfUrl)
          val _tmpImageUrl: String?
          if (_stmt.isNull(_columnIndexOfImageUrl)) {
            _tmpImageUrl = null
          } else {
            _tmpImageUrl = _stmt.getText(_columnIndexOfImageUrl)
          }
          val _tmpSource: String
          _tmpSource = _stmt.getText(_columnIndexOfSource)
          val _tmpPublishedAt: Long
          _tmpPublishedAt = _stmt.getLong(_columnIndexOfPublishedAt)
          val _tmpIsRead: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsRead).toInt()
          _tmpIsRead = _tmp != 0
          val _tmpIsBookmarked: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfIsBookmarked).toInt()
          _tmpIsBookmarked = _tmp_1 != 0
          val _tmpCachedAt: Long
          _tmpCachedAt = _stmt.getLong(_columnIndexOfCachedAt)
          _item =
              NewsItem(_tmpId,_tmpTitle,_tmpDescription,_tmpUrl,_tmpImageUrl,_tmpSource,_tmpPublishedAt,_tmpIsRead,_tmpIsBookmarked,_tmpCachedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun lastCachedAt(): Long? {
    val _sql: String = "SELECT MAX(cachedAt) FROM news_items"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _result: Long?
        if (_stmt.step()) {
          val _tmp: Long?
          if (_stmt.isNull(0)) {
            _tmp = null
          } else {
            _tmp = _stmt.getLong(0)
          }
          _result = _tmp
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun markRead(id: String) {
    val _sql: String = "UPDATE news_items SET isRead = 1 WHERE id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, id)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun toggleBookmark(id: String) {
    val _sql: String =
        "UPDATE news_items SET isBookmarked = CASE WHEN isBookmarked = 0 THEN 1 ELSE 0 END WHERE id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, id)
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
