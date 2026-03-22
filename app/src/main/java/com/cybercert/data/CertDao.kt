package com.cybercert.data

import androidx.room.*
import com.cybercert.model.Certification
import kotlinx.coroutines.flow.Flow

@Dao
interface CertDao {
    @Query("SELECT * FROM certifications ORDER BY name ASC")
    fun getAllCerts(): Flow<List<Certification>>

    @Query("SELECT * FROM certifications ORDER BY name ASC")
    suspend fun getAllCertsOnce(): List<Certification>

    @Query("SELECT * FROM certifications WHERE id = :id")
    suspend fun getCertById(id: String): Certification?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(cert: Certification)

    @Update
    suspend fun update(cert: Certification)

    @Delete
    suspend fun delete(cert: Certification)
}
