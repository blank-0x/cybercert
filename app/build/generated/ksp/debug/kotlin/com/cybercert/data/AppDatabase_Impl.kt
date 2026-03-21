package com.cybercert.`data`

import androidx.room.InvalidationTracker
import androidx.room.RoomOpenDelegate
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.room.util.TableInfo
import androidx.room.util.TableInfo.Companion.read
import androidx.room.util.dropFtsSyncTriggers
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import javax.`annotation`.processing.Generated
import kotlin.Lazy
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableList
import kotlin.collections.MutableMap
import kotlin.collections.MutableSet
import kotlin.collections.Set
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf
import kotlin.collections.mutableSetOf
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class AppDatabase_Impl : AppDatabase() {
  private val _certDao: Lazy<CertDao> = lazy {
    CertDao_Impl(this)
  }

  private val _studySessionDao: Lazy<StudySessionDao> = lazy {
    StudySessionDao_Impl(this)
  }

  private val _newsItemDao: Lazy<NewsItemDao> = lazy {
    NewsItemDao_Impl(this)
  }

  protected override fun createOpenDelegate(): RoomOpenDelegate {
    val _openDelegate: RoomOpenDelegate = object : RoomOpenDelegate(2,
        "a91afd37bb58130255d634834316831b", "103aea180f074c62d1a1898cc6befa05") {
      public override fun createAllTables(connection: SQLiteConnection) {
        connection.execSQL("CREATE TABLE IF NOT EXISTS `certifications` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, `code` TEXT NOT NULL, `provider` TEXT NOT NULL, `category` TEXT NOT NULL, `description` TEXT NOT NULL, `examUrl` TEXT NOT NULL, `resourceUrls` TEXT NOT NULL, `prerequisites` TEXT NOT NULL, `validityYears` INTEGER NOT NULL, `color` TEXT NOT NULL, `status` TEXT NOT NULL, `progressPercent` INTEGER NOT NULL, `studyHoursTotal` REAL NOT NULL, `examDate` INTEGER, `completedDate` INTEGER, `notes` TEXT NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `study_sessions` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `certId` TEXT NOT NULL, `date` INTEGER NOT NULL, `durationMinutes` INTEGER NOT NULL, `notes` TEXT NOT NULL)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `news_items` (`id` TEXT NOT NULL, `title` TEXT NOT NULL, `description` TEXT NOT NULL, `url` TEXT NOT NULL, `imageUrl` TEXT, `source` TEXT NOT NULL, `publishedAt` INTEGER NOT NULL, `isRead` INTEGER NOT NULL, `isBookmarked` INTEGER NOT NULL, `cachedAt` INTEGER NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)")
        connection.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'a91afd37bb58130255d634834316831b')")
      }

      public override fun dropAllTables(connection: SQLiteConnection) {
        connection.execSQL("DROP TABLE IF EXISTS `certifications`")
        connection.execSQL("DROP TABLE IF EXISTS `study_sessions`")
        connection.execSQL("DROP TABLE IF EXISTS `news_items`")
      }

      public override fun onCreate(connection: SQLiteConnection) {
      }

      public override fun onOpen(connection: SQLiteConnection) {
        internalInitInvalidationTracker(connection)
      }

      public override fun onPreMigrate(connection: SQLiteConnection) {
        dropFtsSyncTriggers(connection)
      }

      public override fun onPostMigrate(connection: SQLiteConnection) {
      }

      public override fun onValidateSchema(connection: SQLiteConnection):
          RoomOpenDelegate.ValidationResult {
        val _columnsCertifications: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsCertifications.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCertifications.put("name", TableInfo.Column("name", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCertifications.put("code", TableInfo.Column("code", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCertifications.put("provider", TableInfo.Column("provider", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCertifications.put("category", TableInfo.Column("category", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCertifications.put("description", TableInfo.Column("description", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCertifications.put("examUrl", TableInfo.Column("examUrl", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCertifications.put("resourceUrls", TableInfo.Column("resourceUrls", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCertifications.put("prerequisites", TableInfo.Column("prerequisites", "TEXT", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCertifications.put("validityYears", TableInfo.Column("validityYears", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCertifications.put("color", TableInfo.Column("color", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCertifications.put("status", TableInfo.Column("status", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCertifications.put("progressPercent", TableInfo.Column("progressPercent", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCertifications.put("studyHoursTotal", TableInfo.Column("studyHoursTotal", "REAL",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCertifications.put("examDate", TableInfo.Column("examDate", "INTEGER", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCertifications.put("completedDate", TableInfo.Column("completedDate", "INTEGER",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCertifications.put("notes", TableInfo.Column("notes", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysCertifications: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesCertifications: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoCertifications: TableInfo = TableInfo("certifications", _columnsCertifications,
            _foreignKeysCertifications, _indicesCertifications)
        val _existingCertifications: TableInfo = read(connection, "certifications")
        if (!_infoCertifications.equals(_existingCertifications)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |certifications(com.cybercert.model.Certification).
              | Expected:
              |""".trimMargin() + _infoCertifications + """
              |
              | Found:
              |""".trimMargin() + _existingCertifications)
        }
        val _columnsStudySessions: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsStudySessions.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsStudySessions.put("certId", TableInfo.Column("certId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsStudySessions.put("date", TableInfo.Column("date", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsStudySessions.put("durationMinutes", TableInfo.Column("durationMinutes", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsStudySessions.put("notes", TableInfo.Column("notes", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysStudySessions: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesStudySessions: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoStudySessions: TableInfo = TableInfo("study_sessions", _columnsStudySessions,
            _foreignKeysStudySessions, _indicesStudySessions)
        val _existingStudySessions: TableInfo = read(connection, "study_sessions")
        if (!_infoStudySessions.equals(_existingStudySessions)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |study_sessions(com.cybercert.model.StudySession).
              | Expected:
              |""".trimMargin() + _infoStudySessions + """
              |
              | Found:
              |""".trimMargin() + _existingStudySessions)
        }
        val _columnsNewsItems: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsNewsItems.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsNewsItems.put("title", TableInfo.Column("title", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsNewsItems.put("description", TableInfo.Column("description", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsNewsItems.put("url", TableInfo.Column("url", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsNewsItems.put("imageUrl", TableInfo.Column("imageUrl", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsNewsItems.put("source", TableInfo.Column("source", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsNewsItems.put("publishedAt", TableInfo.Column("publishedAt", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsNewsItems.put("isRead", TableInfo.Column("isRead", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsNewsItems.put("isBookmarked", TableInfo.Column("isBookmarked", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsNewsItems.put("cachedAt", TableInfo.Column("cachedAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysNewsItems: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesNewsItems: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoNewsItems: TableInfo = TableInfo("news_items", _columnsNewsItems,
            _foreignKeysNewsItems, _indicesNewsItems)
        val _existingNewsItems: TableInfo = read(connection, "news_items")
        if (!_infoNewsItems.equals(_existingNewsItems)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |news_items(com.cybercert.model.NewsItem).
              | Expected:
              |""".trimMargin() + _infoNewsItems + """
              |
              | Found:
              |""".trimMargin() + _existingNewsItems)
        }
        return RoomOpenDelegate.ValidationResult(true, null)
      }
    }
    return _openDelegate
  }

  protected override fun createInvalidationTracker(): InvalidationTracker {
    val _shadowTablesMap: MutableMap<String, String> = mutableMapOf()
    val _viewTables: MutableMap<String, Set<String>> = mutableMapOf()
    return InvalidationTracker(this, _shadowTablesMap, _viewTables, "certifications",
        "study_sessions", "news_items")
  }

  public override fun clearAllTables() {
    super.performClear(false, "certifications", "study_sessions", "news_items")
  }

  protected override fun getRequiredTypeConverterClasses(): Map<KClass<*>, List<KClass<*>>> {
    val _typeConvertersMap: MutableMap<KClass<*>, List<KClass<*>>> = mutableMapOf()
    _typeConvertersMap.put(CertDao::class, CertDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(StudySessionDao::class, StudySessionDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(NewsItemDao::class, NewsItemDao_Impl.getRequiredConverters())
    return _typeConvertersMap
  }

  public override fun getRequiredAutoMigrationSpecClasses(): Set<KClass<out AutoMigrationSpec>> {
    val _autoMigrationSpecsSet: MutableSet<KClass<out AutoMigrationSpec>> = mutableSetOf()
    return _autoMigrationSpecsSet
  }

  public override
      fun createAutoMigrations(autoMigrationSpecs: Map<KClass<out AutoMigrationSpec>, AutoMigrationSpec>):
      List<Migration> {
    val _autoMigrations: MutableList<Migration> = mutableListOf()
    return _autoMigrations
  }

  public override fun certDao(): CertDao = _certDao.value

  public override fun studySessionDao(): StudySessionDao = _studySessionDao.value

  public override fun newsItemDao(): NewsItemDao = _newsItemDao.value
}
