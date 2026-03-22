package com.cybercert.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cybercert.ui.theme.AppColors
import com.cybercert.viewmodel.NewsViewModel

@Composable
fun BookmarksScreen(
    viewModel: NewsViewModel,
    isDark: Boolean,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val c = AppColors(isDark)
    val bookmarks by viewModel.bookmarks.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = c.primaryText
                )
            }
            Text(
                text = "Bookmarks",
                style = MaterialTheme.typography.headlineMedium,
                color = c.primaryText,
                fontWeight = FontWeight.Bold
            )
        }

        if (bookmarks.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "No bookmarks yet.\nTap the bookmark icon on any article.",
                    color = c.secondaryText,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(bookmarks, key = { it.id }, contentType = { "news_item" }) { item ->
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
            }
        }
    }
}
