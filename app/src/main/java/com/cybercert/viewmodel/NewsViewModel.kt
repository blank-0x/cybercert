package com.cybercert.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.cybercert.data.NewsItemDao
import com.cybercert.model.NewsItem
import com.cybercert.model.NewsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NewsViewModel(private val repository: NewsRepository) : ViewModel() {

    val news: StateFlow<List<NewsItem>> = repository.newsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    private val _lastRefreshed = MutableStateFlow<Long?>(null)
    val lastRefreshed: StateFlow<Long?> = _lastRefreshed

    init {
        viewModelScope.launch {
            _lastRefreshed.value = repository.lastCachedAt()
        }
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                val time = repository.refresh()
                _lastRefreshed.value = time
            } catch (_: Exception) { }
            _isRefreshing.value = false
        }
    }

    fun toggleBookmark(id: String) {
        viewModelScope.launch { repository.toggleBookmark(id) }
    }

    fun markRead(id: String) {
        viewModelScope.launch { repository.markRead(id) }
    }

    class Factory(private val dao: NewsItemDao) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            NewsViewModel(NewsRepository(dao)) as T
    }
}
