package com.cybercert.`data`

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.cybercert.model.StudySession
import javax.`annotation`.processing.Generated
import kotlin.Float
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
public class StudySessionDao_Impl(
  __db: RoomDatabase,
) : StudySessionDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfStudySession: EntityInsertAdapter<StudySession>
  init {
    this.__db = __db
    this.__insertAdapterOfStudySession = object : EntityInsertAdapter<StudySession>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `study_sessions` (`id`,`certId`,`date`,`durationMinutes`,`notes`) VALUES (nullif(?, 0),?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: StudySession) {
        statement.bindLong(1, entity.id)
        statement.bindText(2, entity.certId)
        statement.bindLong(3, entity.date)
        statement.bindLong(4, entity.durationMinutes.toLong())
        statement.bindText(5, entity.notes)
      }
    }
  }

  public override suspend fun insert(session: StudySession): Unit = performSuspending(__db, false,
      true) { _connection ->
    __insertAdapterOfStudySession.insert(_connection, session)
  }

  public override fun getSessionsForCert(certId: String): Flow<List<StudySession>> {
    val _sql: String = "SELECT * FROM study_sessions WHERE certId = ? ORDER BY date DESC"
    return createFlow(__db, false, arrayOf("study_sessions")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, certId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfCertId: Int = getColumnIndexOrThrow(_stmt, "certId")
        val _columnIndexOfDate: Int = getColumnIndexOrThrow(_stmt, "date")
        val _columnIndexOfDurationMinutes: Int = getColumnIndexOrThrow(_stmt, "durationMinutes")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _result: MutableList<StudySession> = mutableListOf()
        while (_stmt.step()) {
          val _item: StudySession
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpCertId: String
          _tmpCertId = _stmt.getText(_columnIndexOfCertId)
          val _tmpDate: Long
          _tmpDate = _stmt.getLong(_columnIndexOfDate)
          val _tmpDurationMinutes: Int
          _tmpDurationMinutes = _stmt.getLong(_columnIndexOfDurationMinutes).toInt()
          val _tmpNotes: String
          _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          _item = StudySession(_tmpId,_tmpCertId,_tmpDate,_tmpDurationMinutes,_tmpNotes)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getTotalHoursForCert(certId: String): Float? {
    val _sql: String = "SELECT SUM(durationMinutes) / 60.0 FROM study_sessions WHERE certId = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, certId)
        val _result: Float?
        if (_stmt.step()) {
          val _tmp: Float?
          if (_stmt.isNull(0)) {
            _tmp = null
          } else {
            _tmp = _stmt.getDouble(0).toFloat()
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

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
