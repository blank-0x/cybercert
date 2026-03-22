package com.cybercert.model

import android.content.Context
import com.cybercert.data.AppDatabase
import com.cybercert.data.CertDao
import com.cybercert.data.StudySessionDao
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow

class CertRepository(context: Context) {
    private val certDao: CertDao = AppDatabase.getInstance(context).certDao()
    private val sessionDao: StudySessionDao = AppDatabase.getInstance(context).studySessionDao()
    private val gson = Gson()

    val allCerts: Flow<List<Certification>> = certDao.getAllCerts()
    val allSessionDates: Flow<List<Long>> = sessionDao.getAllSessionDatesFlow()

    suspend fun insert(cert: Certification) = certDao.insert(cert)
    suspend fun update(cert: Certification) = certDao.update(cert)
    suspend fun delete(cert: Certification) = certDao.delete(cert)
    suspend fun getCertById(id: String) = certDao.getCertById(id)

    suspend fun addStudySession(session: StudySession) = sessionDao.insert(session)
    fun getSessionsForCert(certId: String) = sessionDao.getSessionsForCert(certId)
    suspend fun getTotalHoursForCert(certId: String) = sessionDao.getTotalHoursForCert(certId) ?: 0f

    fun loadCatalog(context: Context): List<CatalogCert> {
        return try {
            val json = context.assets.open("certifications.json")
                .bufferedReader().readText()
            val type = object : TypeToken<List<CatalogCert>>() {}.type
            gson.fromJson(json, type)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun catalogCertToEntity(cat: CatalogCert): Certification = Certification(
        id = cat.id,
        name = cat.name,
        code = cat.code,
        provider = cat.provider,
        category = cat.category,
        description = cat.description,
        examUrl = cat.examUrl,
        resourceUrls = gson.toJson(cat.resourceUrls),
        prerequisites = gson.toJson(cat.prerequisites),
        validityYears = cat.validityYears,
        color = cat.color
    )
}
