package com.cybercert.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.cybercert.model.CertRepository
import com.cybercert.model.CertStatus
import com.cybercert.model.Certification
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class HomeStats(
    val totalTracked: Int,
    val completed: Int,
    val inProgress: Int,
    val totalStudyHours: Float
)

class HomeViewModel(repository: CertRepository) : ViewModel() {

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

    class Factory(private val repository: CertRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            HomeViewModel(repository) as T
    }
}
