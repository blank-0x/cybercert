package com.cybercert.data

import androidx.room.*
import com.cybercert.model.NewsItem
import kotlinx.coroutines.flow.Flow

@Dao
interface NewsItemDao {

    @Query("SELECT * FROM news_items ORDER BY publishedAt DESC")
    fun allNews(): Flow<List<NewsItem>>

    @Query("SELECT * FROM news_items ORDER BY publishedAt DESC")
    suspend fun getAllOnce(): List<NewsItem>

    @Upsert
    suspend fun upsertAll(items: List<NewsItem>)

    @Query("UPDATE news_items SET isRead = 1 WHERE id = :id")
    suspend fun markRead(id: String)

    @Query("UPDATE news_items SET isBookmarked = CASE WHEN isBookmarked = 0 THEN 1 ELSE 0 END WHERE id = :id")
    suspend fun toggleBookmark(id: String)

    @Query("SELECT MAX(cachedAt) FROM news_items")
    suspend fun lastCachedAt(): Long?

    @Query("SELECT * FROM news_items WHERE isBookmarked = 1 ORDER BY publishedAt DESC")
    fun bookmarkedNews(): Flow<List<NewsItem>>
}
