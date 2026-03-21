package com.cybercert.data

import android.content.Context
import androidx.room.*
import com.cybercert.model.Certification
import com.cybercert.model.CertStatus
import com.cybercert.model.NewsItem
import com.cybercert.model.StudySession

class Converters {
    @TypeConverter
    fun fromCertStatus(value: CertStatus): String = value.name

    @TypeConverter
    fun toCertStatus(value: String): CertStatus = CertStatus.valueOf(value)
}

@Database(
    entities = [Certification::class, StudySession::class, NewsItem::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun certDao(): CertDao
    abstract fun studySessionDao(): StudySessionDao
    abstract fun newsItemDao(): NewsItemDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "cybercert.db"
                )
                .fallbackToDestructiveMigration(dropAllTables = true)
                .build().also { INSTANCE = it }
            }
        }
    }
}
