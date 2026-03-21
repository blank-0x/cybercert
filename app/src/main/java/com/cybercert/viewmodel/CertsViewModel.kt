package com.cybercert.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.cybercert.model.CatalogCert
import com.cybercert.model.CertRepository
import com.cybercert.model.CertStatus
import com.cybercert.model.Certification
import com.cybercert.model.StudySession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CertsViewModel(private val repository: CertRepository, context: Context) : ViewModel() {

    val certs: StateFlow<List<Certification>> = repository.allCerts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _catalog = MutableStateFlow<List<CatalogCert>>(emptyList())
    val catalog: StateFlow<List<CatalogCert>> = _catalog

    private val _selectedFilter = MutableStateFlow("All")
    val selectedFilter: StateFlow<String> = _selectedFilter

    init {
        _catalog.value = repository.loadCatalog(context)
    }

    fun setFilter(filter: String) { _selectedFilter.value = filter }

    fun filteredCerts(): List<Certification> {
        return when (_selectedFilter.value) {
            "In Progress" -> certs.value.filter { it.status == CertStatus.IN_PROGRESS }
            "Completed" -> certs.value.filter { it.status == CertStatus.COMPLETED }
            else -> certs.value
        }
    }

    fun addCertFromCatalog(cat: CatalogCert) = viewModelScope.launch {
        val existing = repository.getCertById(cat.id)
        if (existing == null) {
            repository.insert(repository.catalogCertToEntity(cat))
        }
    }

    fun updateCert(cert: Certification) = viewModelScope.launch {
        repository.update(cert)
    }

    fun deleteCert(cert: Certification) = viewModelScope.launch {
        repository.delete(cert)
    }

    fun logStudySession(certId: String, durationMinutes: Int, notes: String = "") {
        viewModelScope.launch {
            val session = StudySession(
                certId = certId,
                date = System.currentTimeMillis(),
                durationMinutes = durationMinutes,
                notes = notes
            )
            repository.addStudySession(session)
            val cert = repository.getCertById(certId)
            if (cert != null) {
                val totalHours = repository.getTotalHoursForCert(certId)
                repository.update(cert.copy(studyHoursTotal = totalHours))
            }
        }
    }

    class Factory(private val repository: CertRepository, private val context: Context) :
        ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            CertsViewModel(repository, context) as T
    }
}
