package com.cybercert.viewmodel

import android.content.Context
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.cybercert.data.NewsRefreshInterval
import com.cybercert.data.NewsRefreshWorker
import com.cybercert.data.SettingsRepository
import com.cybercert.model.CAREER_PATHS
import com.cybercert.model.CatalogCert
import com.cybercert.model.CertRepository
import com.cybercert.model.CertStatus
import com.cybercert.model.Certification
import com.cybercert.model.StreakCalculator
import com.cybercert.model.StreakData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@Immutable
data class HomeStats(
    val totalTracked: Int,
    val completed: Int,
    val inProgress: Int,
    val totalStudyHours: Float
)

class HomeViewModel(
    private val repository: CertRepository,
    private val settings: SettingsRepository,
    private val context: Context
) : ViewModel() {

    val stats: StateFlow<HomeStats> = repository.allCerts.map { certs ->
        HomeStats(
            totalTracked = certs.size,
            completed = certs.count { it.status == CertStatus.COMPLETED },
            inProgress = certs.count { it.status == CertStatus.IN_PROGRESS },
            totalStudyHours = certs.sumOf { it.studyHoursTotal.toDouble() }.toFloat()
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeStats(0, 0, 0, 0f))

    val currentlyStudying: StateFlow<List<Certification>> = repository.allCerts.map { certs ->
        certs.filter { it.status == CertStatus.IN_PROGRESS }.take(3)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val streakData: StateFlow<StreakData> = repository.allSessionDates.map { dates ->
        StreakCalculator.calculate(dates)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), StreakData(0, 0, false))

    // Settings
    val isDarkTheme: StateFlow<Boolean> = settings.isDarkTheme
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)
    val newsRefreshInterval: StateFlow<NewsRefreshInterval> = settings.newsRefreshInterval
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), NewsRefreshInterval.MANUAL)
    val examRemindersEnabled: StateFlow<Boolean> = settings.examRemindersEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
    val examReminderDays: StateFlow<Int> = settings.examReminderDays
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 7)

    // Career path
    val selectedCareerPath: StateFlow<String> = settings.selectedCareerPath
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")
    val trackedCerts: StateFlow<List<Certification>> = repository.allCerts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _catalog = MutableStateFlow<List<CatalogCert>>(emptyList())
    val catalog: StateFlow<List<CatalogCert>> = _catalog

    init {
        _catalog.value = repository.loadCatalog(context)
    }

    fun setDarkTheme(enabled: Boolean) = viewModelScope.launch {
        settings.setDarkTheme(enabled)
    }

    fun setNewsRefreshInterval(interval: NewsRefreshInterval) = viewModelScope.launch {
        settings.setNewsRefreshInterval(interval)
        NewsRefreshWorker.schedule(context, interval.hours)
    }

    fun setExamRemindersEnabled(enabled: Boolean) = viewModelScope.launch {
        settings.setExamRemindersEnabled(enabled)
    }

    fun setExamReminderDays(days: Int) = viewModelScope.launch {
        settings.setExamReminderDays(days)
    }

    fun setSelectedCareerPath(path: String) = viewModelScope.launch {
        settings.setSelectedCareerPath(path)
    }

    fun addCertFromCatalogId(certId: String) = viewModelScope.launch {
        val cat = _catalog.value.firstOrNull { it.id == certId } ?: return@launch
        val existing = repository.getCertById(cat.id)
        if (existing == null) {
            repository.insert(repository.catalogCertToEntity(cat))
        }
    }

    fun clearAllData() = viewModelScope.launch {
        trackedCerts.value.forEach { repository.delete(it) }
    }

    val careerPaths = CAREER_PATHS

    class Factory(
        private val repository: CertRepository,
        private val settings: SettingsRepository,
        private val context: Context
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            HomeViewModel(repository, settings, context) as T
    }
}
