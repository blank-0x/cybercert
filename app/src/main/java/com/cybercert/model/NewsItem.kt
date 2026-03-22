package com.cybercert.model

import androidx.compose.runtime.Immutable
import androidx.room.Entity
import androidx.room.PrimaryKey

enum class NewsCategory {
    VULNERABILITIES, MALWARE, BREACHES, PHISHING, TOOLS, GENERAL
}

@Immutable
@Entity(tableName = "news_items")
data class NewsItem(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val url: String,
    val imageUrl: String?,
    val source: String,
    val publishedAt: Long,
    val isRead: Boolean = false,
    val isBookmarked: Boolean = false,
    val cachedAt: Long = 0L,
    val category: NewsCategory = NewsCategory.GENERAL
)
