package com.cybercert.`data`

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.cybercert.model.CertStatus
import com.cybercert.model.Certification
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
public class CertDao_Impl(
  __db: RoomDatabase,
) : CertDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfCertification: EntityInsertAdapter<Certification>

  private val __converters: Converters = Converters()

  private val __deleteAdapterOfCertification: EntityDeleteOrUpdateAdapter<Certification>

  private val __updateAdapterOfCertification: EntityDeleteOrUpdateAdapter<Certification>
  init {
    this.__db = __db
    this.__insertAdapterOfCertification = object : EntityInsertAdapter<Certification>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `certifications` (`id`,`name`,`code`,`provider`,`category`,`description`,`examUrl`,`resourceUrls`,`prerequisites`,`validityYears`,`color`,`status`,`progressPercent`,`studyHoursTotal`,`examDate`,`completedDate`,`notes`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: Certification) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.name)
        statement.bindText(3, entity.code)
        statement.bindText(4, entity.provider)
        statement.bindText(5, entity.category)
        statement.bindText(6, entity.description)
        statement.bindText(7, entity.examUrl)
        statement.bindText(8, entity.resourceUrls)
        statement.bindText(9, entity.prerequisites)
        statement.bindLong(10, entity.validityYears.toLong())
        statement.bindText(11, entity.color)
        val _tmp: String = __converters.fromCertStatus(entity.status)
        statement.bindText(12, _tmp)
        statement.bindLong(13, entity.progressPercent.toLong())
        statement.bindDouble(14, entity.studyHoursTotal.toDouble())
        val _tmpExamDate: Long? = entity.examDate
        if (_tmpExamDate == null) {
          statement.bindNull(15)
        } else {
          statement.bindLong(15, _tmpExamDate)
        }
        val _tmpCompletedDate: Long? = entity.completedDate
        if (_tmpCompletedDate == null) {
          statement.bindNull(16)
        } else {
          statement.bindLong(16, _tmpCompletedDate)
        }
        statement.bindText(17, entity.notes)
      }
    }
    this.__deleteAdapterOfCertification = object : EntityDeleteOrUpdateAdapter<Certification>() {
      protected override fun createQuery(): String = "DELETE FROM `certifications` WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: Certification) {
        statement.bindText(1, entity.id)
      }
    }
    this.__updateAdapterOfCertification = object : EntityDeleteOrUpdateAdapter<Certification>() {
      protected override fun createQuery(): String =
          "UPDATE OR ABORT `certifications` SET `id` = ?,`name` = ?,`code` = ?,`provider` = ?,`category` = ?,`description` = ?,`examUrl` = ?,`resourceUrls` = ?,`prerequisites` = ?,`validityYears` = ?,`color` = ?,`status` = ?,`progressPercent` = ?,`studyHoursTotal` = ?,`examDate` = ?,`completedDate` = ?,`notes` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: Certification) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.name)
        statement.bindText(3, entity.code)
        statement.bindText(4, entity.provider)
        statement.bindText(5, entity.category)
        statement.bindText(6, entity.description)
        statement.bindText(7, entity.examUrl)
        statement.bindText(8, entity.resourceUrls)
        statement.bindText(9, entity.prerequisites)
        statement.bindLong(10, entity.validityYears.toLong())
        statement.bindText(11, entity.color)
        val _tmp: String = __converters.fromCertStatus(entity.status)
        statement.bindText(12, _tmp)
        statement.bindLong(13, entity.progressPercent.toLong())
        statement.bindDouble(14, entity.studyHoursTotal.toDouble())
        val _tmpExamDate: Long? = entity.examDate
        if (_tmpExamDate == null) {
          statement.bindNull(15)
        } else {
          statement.bindLong(15, _tmpExamDate)
        }
        val _tmpCompletedDate: Long? = entity.completedDate
        if (_tmpCompletedDate == null) {
          statement.bindNull(16)
        } else {
          statement.bindLong(16, _tmpCompletedDate)
        }
        statement.bindText(17, entity.notes)
        statement.bindText(18, entity.id)
      }
    }
  }

  public override suspend fun insert(cert: Certification): Unit = performSuspending(__db, false,
      true) { _connection ->
    __insertAdapterOfCertification.insert(_connection, cert)
  }

  public override suspend fun delete(cert: Certification): Unit = performSuspending(__db, false,
      true) { _connection ->
    __deleteAdapterOfCertification.handle(_connection, cert)
  }

  public override suspend fun update(cert: Certification): Unit = performSuspending(__db, false,
      true) { _connection ->
    __updateAdapterOfCertification.handle(_connection, cert)
  }

  public override fun getAllCerts(): Flow<List<Certification>> {
    val _sql: String = "SELECT * FROM certifications ORDER BY name ASC"
    return createFlow(__db, false, arrayOf("certifications")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfCode: Int = getColumnIndexOrThrow(_stmt, "code")
        val _columnIndexOfProvider: Int = getColumnIndexOrThrow(_stmt, "provider")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfExamUrl: Int = getColumnIndexOrThrow(_stmt, "examUrl")
        val _columnIndexOfResourceUrls: Int = getColumnIndexOrThrow(_stmt, "resourceUrls")
        val _columnIndexOfPrerequisites: Int = getColumnIndexOrThrow(_stmt, "prerequisites")
        val _columnIndexOfValidityYears: Int = getColumnIndexOrThrow(_stmt, "validityYears")
        val _columnIndexOfColor: Int = getColumnIndexOrThrow(_stmt, "color")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfProgressPercent: Int = getColumnIndexOrThrow(_stmt, "progressPercent")
        val _columnIndexOfStudyHoursTotal: Int = getColumnIndexOrThrow(_stmt, "studyHoursTotal")
        val _columnIndexOfExamDate: Int = getColumnIndexOrThrow(_stmt, "examDate")
        val _columnIndexOfCompletedDate: Int = getColumnIndexOrThrow(_stmt, "completedDate")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _result: MutableList<Certification> = mutableListOf()
        while (_stmt.step()) {
          val _item: Certification
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpCode: String
          _tmpCode = _stmt.getText(_columnIndexOfCode)
          val _tmpProvider: String
          _tmpProvider = _stmt.getText(_columnIndexOfProvider)
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpExamUrl: String
          _tmpExamUrl = _stmt.getText(_columnIndexOfExamUrl)
          val _tmpResourceUrls: String
          _tmpResourceUrls = _stmt.getText(_columnIndexOfResourceUrls)
          val _tmpPrerequisites: String
          _tmpPrerequisites = _stmt.getText(_columnIndexOfPrerequisites)
          val _tmpValidityYears: Int
          _tmpValidityYears = _stmt.getLong(_columnIndexOfValidityYears).toInt()
          val _tmpColor: String
          _tmpColor = _stmt.getText(_columnIndexOfColor)
          val _tmpStatus: CertStatus
          val _tmp: String
          _tmp = _stmt.getText(_columnIndexOfStatus)
          _tmpStatus = __converters.toCertStatus(_tmp)
          val _tmpProgressPercent: Int
          _tmpProgressPercent = _stmt.getLong(_columnIndexOfProgressPercent).toInt()
          val _tmpStudyHoursTotal: Float
          _tmpStudyHoursTotal = _stmt.getDouble(_columnIndexOfStudyHoursTotal).toFloat()
          val _tmpExamDate: Long?
          if (_stmt.isNull(_columnIndexOfExamDate)) {
            _tmpExamDate = null
          } else {
            _tmpExamDate = _stmt.getLong(_columnIndexOfExamDate)
          }
          val _tmpCompletedDate: Long?
          if (_stmt.isNull(_columnIndexOfCompletedDate)) {
            _tmpCompletedDate = null
          } else {
            _tmpCompletedDate = _stmt.getLong(_columnIndexOfCompletedDate)
          }
          val _tmpNotes: String
          _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          _item =
              Certification(_tmpId,_tmpName,_tmpCode,_tmpProvider,_tmpCategory,_tmpDescription,_tmpExamUrl,_tmpResourceUrls,_tmpPrerequisites,_tmpValidityYears,_tmpColor,_tmpStatus,_tmpProgressPercent,_tmpStudyHoursTotal,_tmpExamDate,_tmpCompletedDate,_tmpNotes)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getCertById(id: String): Certification? {
    val _sql: String = "SELECT * FROM certifications WHERE id = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfCode: Int = getColumnIndexOrThrow(_stmt, "code")
        val _columnIndexOfProvider: Int = getColumnIndexOrThrow(_stmt, "provider")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfExamUrl: Int = getColumnIndexOrThrow(_stmt, "examUrl")
        val _columnIndexOfResourceUrls: Int = getColumnIndexOrThrow(_stmt, "resourceUrls")
        val _columnIndexOfPrerequisites: Int = getColumnIndexOrThrow(_stmt, "prerequisites")
        val _columnIndexOfValidityYears: Int = getColumnIndexOrThrow(_stmt, "validityYears")
        val _columnIndexOfColor: Int = getColumnIndexOrThrow(_stmt, "color")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfProgressPercent: Int = getColumnIndexOrThrow(_stmt, "progressPercent")
        val _columnIndexOfStudyHoursTotal: Int = getColumnIndexOrThrow(_stmt, "studyHoursTotal")
        val _columnIndexOfExamDate: Int = getColumnIndexOrThrow(_stmt, "examDate")
        val _columnIndexOfCompletedDate: Int = getColumnIndexOrThrow(_stmt, "completedDate")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _result: Certification?
        if (_stmt.step()) {
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpCode: String
          _tmpCode = _stmt.getText(_columnIndexOfCode)
          val _tmpProvider: String
          _tmpProvider = _stmt.getText(_columnIndexOfProvider)
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpExamUrl: String
          _tmpExamUrl = _stmt.getText(_columnIndexOfExamUrl)
          val _tmpResourceUrls: String
          _tmpResourceUrls = _stmt.getText(_columnIndexOfResourceUrls)
          val _tmpPrerequisites: String
          _tmpPrerequisites = _stmt.getText(_columnIndexOfPrerequisites)
          val _tmpValidityYears: Int
          _tmpValidityYears = _stmt.getLong(_columnIndexOfValidityYears).toInt()
          val _tmpColor: String
          _tmpColor = _stmt.getText(_columnIndexOfColor)
          val _tmpStatus: CertStatus
          val _tmp: String
          _tmp = _stmt.getText(_columnIndexOfStatus)
          _tmpStatus = __converters.toCertStatus(_tmp)
          val _tmpProgressPercent: Int
          _tmpProgressPercent = _stmt.getLong(_columnIndexOfProgressPercent).toInt()
          val _tmpStudyHoursTotal: Float
          _tmpStudyHoursTotal = _stmt.getDouble(_columnIndexOfStudyHoursTotal).toFloat()
          val _tmpExamDate: Long?
          if (_stmt.isNull(_columnIndexOfExamDate)) {
            _tmpExamDate = null
          } else {
            _tmpExamDate = _stmt.getLong(_columnIndexOfExamDate)
          }
          val _tmpCompletedDate: Long?
          if (_stmt.isNull(_columnIndexOfCompletedDate)) {
            _tmpCompletedDate = null
          } else {
            _tmpCompletedDate = _stmt.getLong(_columnIndexOfCompletedDate)
          }
          val _tmpNotes: String
          _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          _result =
              Certification(_tmpId,_tmpName,_tmpCode,_tmpProvider,_tmpCategory,_tmpDescription,_tmpExamUrl,_tmpResourceUrls,_tmpPrerequisites,_tmpValidityYears,_tmpColor,_tmpStatus,_tmpProgressPercent,_tmpStudyHoursTotal,_tmpExamDate,_tmpCompletedDate,_tmpNotes)
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
