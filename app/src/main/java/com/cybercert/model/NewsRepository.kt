package com.cybercert.model

import com.cybercert.data.NewsItemDao
import com.cybercert.data.RssParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class NewsRepository(private val dao: NewsItemDao) {

    val newsFlow: Flow<List<NewsItem>> = dao.allNews()
    val bookmarksFlow: Flow<List<NewsItem>> = dao.bookmarkedNews()

    suspend fun refresh(): Long {
        val fetched = withContext(Dispatchers.IO) { RssParser.fetchAll() }
        val now = System.currentTimeMillis()
        val existingMap = dao.getAllOnce().associateBy { it.id }
        val merged = fetched.map { item ->
            val prev = existingMap[item.id]
            item.copy(
                isRead = prev?.isRead ?: false,
                isBookmarked = prev?.isBookmarked ?: false,
                cachedAt = now
            )
        }
        dao.upsertAll(merged)
        return now
    }

    suspend fun toggleBookmark(id: String) = dao.toggleBookmark(id)

    suspend fun markRead(id: String) = dao.markRead(id)

    suspend fun lastCachedAt(): Long? = dao.lastCachedAt()

    suspend fun isStale(): Boolean {
        val lastFetch = dao.lastCachedAt() ?: return true
        return System.currentTimeMillis() - lastFetch > 30 * 60 * 1000L
    }
}
