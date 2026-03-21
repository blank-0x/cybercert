package com.cybercert.model

import com.cybercert.data.RssParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NewsRepository {
    private var cachedNews: List<NewsItem> = emptyList()
    private val bookmarked = mutableSetOf<String>()
    private val read = mutableSetOf<String>()

    suspend fun fetchNews(): List<NewsItem> {
        val fetched = withContext(Dispatchers.IO) { RssParser.fetchAll() }
        cachedNews = fetched.map { item ->
            item.copy(
                isBookmarked = item.id in bookmarked,
                isRead = item.id in read
            )
        }
        return cachedNews
    }

    fun toggleBookmark(id: String) {
        if (id in bookmarked) bookmarked.remove(id) else bookmarked.add(id)
        cachedNews = cachedNews.map { if (it.id == id) it.copy(isBookmarked = id in bookmarked) else it }
    }

    fun markRead(id: String) {
        read.add(id)
        cachedNews = cachedNews.map { if (it.id == id) it.copy(isRead = true) else it }
    }

    fun getCached(): List<NewsItem> = cachedNews
}
