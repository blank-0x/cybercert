package com.cybercert.model

data class NewsItem(
    val id: String,
    val title: String,
    val description: String,
    val url: String,
    val imageUrl: String?,
    val source: String,
    val publishedAt: Long,
    val isRead: Boolean = false,
    val isBookmarked: Boolean = false
)
