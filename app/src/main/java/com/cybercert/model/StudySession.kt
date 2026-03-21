package com.cybercert.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "study_sessions")
data class StudySession(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val certId: String,
    val date: Long,
    val durationMinutes: Int,
    val notes: String = ""
)
