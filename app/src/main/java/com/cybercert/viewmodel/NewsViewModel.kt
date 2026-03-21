package com.cybercert.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cybercert.model.NewsItem
import com.cybercert.model.NewsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NewsViewModel : ViewModel() {
    private val repository = NewsRepository()

    private val _news = MutableStateFlow<List<NewsItem>>(emptyList())
    val news: StateFlow<List<NewsItem>> = _news

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    init { refresh() }

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            _news.value = repository.fetchNews()
            _isRefreshing.value = false
        }
    }

    fun toggleBookmark(id: String) {
        repository.toggleBookmark(id)
        _news.value = repository.getCached()
    }

    fun markRead(id: String) {
        repository.markRead(id)
        _news.value = repository.getCached()
    }
}
