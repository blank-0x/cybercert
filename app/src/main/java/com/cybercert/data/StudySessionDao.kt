package com.cybercert.data

import androidx.room.*
import com.cybercert.model.StudySession
import kotlinx.coroutines.flow.Flow

@Dao
interface StudySessionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(session: StudySession)

    @Query("SELECT * FROM study_sessions WHERE certId = :certId ORDER BY date DESC")
    fun getSessionsForCert(certId: String): Flow<List<StudySession>>

    @Query("SELECT SUM(durationMinutes) / 60.0 FROM study_sessions WHERE certId = :certId")
    suspend fun getTotalHoursForCert(certId: String): Float?
}
