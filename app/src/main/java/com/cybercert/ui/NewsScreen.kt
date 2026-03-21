package com.cybercert.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.cybercert.R
import com.cybercert.model.NewsItem
import com.cybercert.ui.theme.AppColors
import com.cybercert.viewmodel.NewsViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

private const val PAGE_SIZE = 30

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsScreen(
    viewModel: NewsViewModel,
    isDark: Boolean,
    listState: LazyListState = rememberLazyListState(),
    modifier: Modifier = Modifier
) {
    val c = AppColors(isDark)
    val news by viewModel.news.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val lastRefreshed by viewModel.lastRefreshed.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Pagination: show first PAGE_SIZE items, expand on "Load more"
    var visibleCount by remember { mutableIntStateOf(PAGE_SIZE) }
    // Reset pagination when news list changes (e.g. after refresh)
    LaunchedEffect(news.size) { if (visibleCount > news.size) visibleCount = maxOf(PAGE_SIZE, news.size) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 16.dp)
    ) {
        Text(
            text = "Security News",
            style = MaterialTheme.typography.headlineMedium,
            color = c.primaryText,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )
        if (lastRefreshed != null && lastRefreshed!! > 0L) {
            Text(
                text = "Updated ${timeAgo(lastRefreshed!!)}",
                color = c.secondaryText,
                fontSize = 12.sp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))

        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { viewModel.refresh() },
            modifier = Modifier.fillMaxSize()
        ) {
            if (news.isEmpty() && !isRefreshing) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Pull down to load news", color = c.secondaryText, style = MaterialTheme.typography.bodyLarge)
                }
            } else {
                val visibleNews = news.take(visibleCount)
                LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(visibleNews, key = { it.id }, contentType = { "news_item" }) { item ->
                        NewsCard(
                            item = item,
                            c = c,
                            onBookmark = { viewModel.toggleBookmark(item.id) },
                            onClick = {
                                viewModel.markRead(item.id)
                                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(item.url)))
                            }
                        )
                    }
                    // Load more button
                    if (news.size > visibleCount) {
                        item(key = "load_more", contentType = "load_more") {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                OutlinedButton(onClick = { visibleCount += PAGE_SIZE }) {
                                    Text(
                                        "Load more (${news.size - visibleCount} remaining)",
                                        color = c.accent
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NewsCard(item: NewsItem, c: AppColors, onBookmark: () -> Unit, onClick: () -> Unit) {
    val cardMod = if (c.isDark) {
        Modifier.fillMaxWidth().alpha(if (item.isRead) 0.7f else 1f).clickable { onClick() }
    } else {
        Modifier.fillMaxWidth().alpha(if (item.isRead) 0.7f else 1f)
            .border(1.dp, c.cardBorder, RoundedCornerShape(12.dp))
            .clickable { onClick() }
    }
    Card(
        modifier = cardMod,
        colors = CardDefaults.cardColors(containerColor = c.card),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (c.isDark) 0.dp else 1.dp)
    ) {
        Column {
            item.imageUrl?.let { url ->
                AsyncImage(
                    model = url,
                    contentDescription = null,
                    placeholder = painterResource(R.drawable.ic_launcher_background),
                    error = painterResource(R.drawable.ic_launcher_background),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                    contentScale = ContentScale.Crop
                )
            }
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.title,
                            color = c.primaryText,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = item.description,
                            color = c.secondaryText,
                            fontSize = 13.sp,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    IconButton(onClick = onBookmark) {
                        Icon(
                            if (item.isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = "Bookmark",
                            tint = if (item.isBookmarked) c.accent else c.secondaryText
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text(item.source, color = c.accent, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    Text(timeAgo(item.publishedAt), color = c.secondaryText, fontSize = 12.sp)
                }
            }
        }
    }
}

private fun timeAgo(millis: Long): String {
    val diff = System.currentTimeMillis() - millis
    return when {
        diff < TimeUnit.MINUTES.toMillis(1) -> "just now"
        diff < TimeUnit.HOURS.toMillis(1)   -> "${TimeUnit.MILLISECONDS.toMinutes(diff)}m ago"
        diff < TimeUnit.DAYS.toMillis(1)    -> "${TimeUnit.MILLISECONDS.toHours(diff)}h ago"
        diff < TimeUnit.DAYS.toMillis(7)    -> "${TimeUnit.MILLISECONDS.toDays(diff)}d ago"
        else -> SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(millis))
    }
}
