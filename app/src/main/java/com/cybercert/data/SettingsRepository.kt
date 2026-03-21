package com.cybercert.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "cybercert_settings")

object PrefKeys {
    val DARK_THEME = booleanPreferencesKey("dark_theme")
    val NEWS_REFRESH_INTERVAL = stringPreferencesKey("news_refresh_interval")
    val EXAM_REMINDERS_ENABLED = booleanPreferencesKey("exam_reminders_enabled")
    val EXAM_REMINDER_DAYS = intPreferencesKey("exam_reminder_days")
    val SELECTED_CAREER_PATH = stringPreferencesKey("selected_career_path")
}

enum class NewsRefreshInterval(val label: String, val hours: Long) {
    MANUAL("Manual only", 0),
    HOURLY("Every 1h", 1),
    SIX_HOURS("Every 6h", 6),
    DAILY("Every 24h", 24)
}

class SettingsRepository(private val context: Context) {

    val isDarkTheme: Flow<Boolean> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { it[PrefKeys.DARK_THEME] ?: true }

    val newsRefreshInterval: Flow<NewsRefreshInterval> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map {
            val name = it[PrefKeys.NEWS_REFRESH_INTERVAL] ?: NewsRefreshInterval.MANUAL.name
            NewsRefreshInterval.entries.firstOrNull { e -> e.name == name } ?: NewsRefreshInterval.MANUAL
        }

    val examRemindersEnabled: Flow<Boolean> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { it[PrefKeys.EXAM_REMINDERS_ENABLED] ?: false }

    val examReminderDays: Flow<Int> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { it[PrefKeys.EXAM_REMINDER_DAYS] ?: 7 }

    val selectedCareerPath: Flow<String> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { it[PrefKeys.SELECTED_CAREER_PATH] ?: "" }

    suspend fun setDarkTheme(enabled: Boolean) {
        context.dataStore.edit { it[PrefKeys.DARK_THEME] = enabled }
    }

    suspend fun setNewsRefreshInterval(interval: NewsRefreshInterval) {
        context.dataStore.edit { it[PrefKeys.NEWS_REFRESH_INTERVAL] = interval.name }
    }

    suspend fun setExamRemindersEnabled(enabled: Boolean) {
        context.dataStore.edit { it[PrefKeys.EXAM_REMINDERS_ENABLED] = enabled }
    }

    suspend fun setExamReminderDays(days: Int) {
        context.dataStore.edit { it[PrefKeys.EXAM_REMINDER_DAYS] = days }
    }

    suspend fun setSelectedCareerPath(path: String) {
        context.dataStore.edit { it[PrefKeys.SELECTED_CAREER_PATH] = path }
    }
}
