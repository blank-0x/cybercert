package com.cybercert.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class CertStatus { NOT_STARTED, IN_PROGRESS, COMPLETED }

@Entity(tableName = "certifications")
data class Certification(
    @PrimaryKey val id: String,
    val name: String,
    val code: String,
    val provider: String,
    val category: String,
    val description: String,
    val examUrl: String,
    val resourceUrls: String, // JSON-serialized list
    val prerequisites: String, // JSON-serialized list
    val validityYears: Int,
    val color: String,
    val status: CertStatus = CertStatus.NOT_STARTED,
    val progressPercent: Int = 0,
    val studyHoursTotal: Float = 0f,
    val examDate: Long? = null,
    val completedDate: Long? = null,
    val notes: String = ""
)

data class CatalogCert(
    val id: String,
    val name: String,
    val code: String,
    val provider: String,
    val category: String,
    val description: String,
    val examUrl: String,
    val resourceUrls: List<String>,
    val prerequisites: List<String>,
    val validityYears: Int,
    val color: String
)
