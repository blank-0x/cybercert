package com.cybercert.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.cybercert.data.NewsItemDao
import com.cybercert.model.NewsCategory
import com.cybercert.model.NewsItem
import com.cybercert.model.NewsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NewsViewModel(private val repository: NewsRepository) : ViewModel() {

    private val _selectedCategory = MutableStateFlow<NewsCategory?>(null)
    val selectedCategory: StateFlow<NewsCategory?> = _selectedCategory

    val news: StateFlow<List<NewsItem>> = repository.newsFlow
        .combine(_selectedCategory) { newsList, category ->
            if (category == null) newsList else newsList.filter { it.category == category }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val bookmarks: StateFlow<List<NewsItem>> = repository.bookmarksFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    private val _lastRefreshed = MutableStateFlow<Long?>(null)
    val lastRefreshed: StateFlow<Long?> = _lastRefreshed

    init {
        viewModelScope.launch {
            val lastFetch = repository.lastCachedAt()
            _lastRefreshed.value = lastFetch
            // Auto-refresh on app open only if cache is stale (> 30 min old)
            if (repository.isStale()) {
                doRefresh()
            }
        }
    }

    fun refresh() {
        viewModelScope.launch { doRefresh() }
    }

    fun forceRefresh() {
        viewModelScope.launch { doRefresh() }
    }

    fun resetFilter() {
        _selectedCategory.value = null
    }

    private suspend fun doRefresh() {
        _isRefreshing.value = true
        try {
            val time = repository.refresh()
            _lastRefreshed.value = time
        } catch (_: Exception) { }
        _isRefreshing.value = false
    }

    fun setCategory(category: NewsCategory?) {
        _selectedCategory.value = category
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
