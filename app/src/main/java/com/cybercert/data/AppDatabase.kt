package com.cybercert.data

import android.content.Context
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.cybercert.model.Certification
import com.cybercert.model.CertStatus
import com.cybercert.model.NewsCategory
import com.cybercert.model.NewsItem
import com.cybercert.model.StudySession

class Converters {
    @TypeConverter
    fun fromCertStatus(value: CertStatus): String = value.name

    @TypeConverter
    fun toCertStatus(value: String): CertStatus = CertStatus.valueOf(value)

    @TypeConverter
    fun fromNewsCategory(value: NewsCategory): String = value.name

    @TypeConverter
    fun toNewsCategory(value: String): NewsCategory =
        try { NewsCategory.valueOf(value) } catch (_: Exception) { NewsCategory.GENERAL }
}

private val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE news_items ADD COLUMN category TEXT NOT NULL DEFAULT 'GENERAL'")
    }
}

@Database(
    entities = [Certification::class, StudySession::class, NewsItem::class],
    version = 3,
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
                .addMigrations(MIGRATION_2_3)
                .fallbackToDestructiveMigration(dropAllTables = true)
                .build().also { INSTANCE = it }
            }
        }
    }
}
