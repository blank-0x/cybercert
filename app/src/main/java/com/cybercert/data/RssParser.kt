package com.cybercert.data

import android.util.Xml
import com.cybercert.model.NewsItem
import okhttp3.OkHttpClient
import okhttp3.Request
import org.xmlpull.v1.XmlPullParser
import java.io.StringReader
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.UUID
import java.util.concurrent.TimeUnit

object RssParser {

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    private val dateFormats = listOf(
        SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH),
        SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH),
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.ENGLISH)
    )

    private val feeds = listOf(
        "https://feeds.feedburner.com/TheHackersNews" to "The Hacker News",
        "https://krebsonsecurity.com/feed/" to "Krebs on Security",
        "https://isc.sans.edu/rssfeed_full.xml" to "SANS ISC"
    )

    suspend fun fetchAll(): List<NewsItem> {
        val results = mutableListOf<NewsItem>()
        for ((url, source) in feeds) {
            try {
                results.addAll(fetchFeed(url, source))
            } catch (e: Exception) {
                // Silently ignore failures for individual feeds
            }
        }
        return results.sortedByDescending { it.publishedAt }
    }

    private fun fetchFeed(url: String, source: String): List<NewsItem> {
        val request = Request.Builder().url(url).build()
        val responseBody = client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) return emptyList()
            response.body?.string() ?: return emptyList()
        }
        return parseRss(responseBody, source)
    }

    private fun parseRss(xml: String, source: String): List<NewsItem> {
        val items = mutableListOf<NewsItem>()
        try {
            val parser: XmlPullParser = Xml.newPullParser()
            parser.setInput(StringReader(xml))

            var inItem = false
            var title = ""
            var description = ""
            var link = ""
            var pubDate = ""
            var imageUrl: String? = null

            var eventType = parser.eventType
            while (eventType != XmlPullParser.END_DOCUMENT) {
                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        when (parser.name) {
                            "item" -> {
                                inItem = true
                                title = ""; description = ""; link = ""; pubDate = ""; imageUrl = null
                            }
                            "title" -> if (inItem) title = parser.nextText()
                            "description" -> if (inItem) description = stripHtml(parser.nextText())
                            "link" -> if (inItem) link = parser.nextText()
                            "pubDate" -> if (inItem) pubDate = parser.nextText()
                            "enclosure" -> if (inItem) {
                                val type = parser.getAttributeValue(null, "type") ?: ""
                                if (type.startsWith("image")) {
                                    imageUrl = parser.getAttributeValue(null, "url")
                                }
                            }
                            "media:thumbnail", "media:content" -> if (inItem) {
                                parser.getAttributeValue(null, "url")?.let { imageUrl = it }
                            }
                        }
                    }
                    XmlPullParser.END_TAG -> {
                        if (parser.name == "item" && inItem) {
                            inItem = false
                            if (title.isNotBlank() && link.isNotBlank()) {
                                items.add(
                                    NewsItem(
                                        id = UUID.nameUUIDFromBytes(link.toByteArray()).toString(),
                                        title = title.trim(),
                                        description = description.take(300).trim(),
                                        url = link.trim(),
                                        imageUrl = imageUrl,
                                        source = source,
                                        publishedAt = parseDate(pubDate)
                                    )
                                )
                            }
                        }
                    }
                }
                eventType = parser.next()
            }
        } catch (e: Exception) {
            // Return whatever was parsed
        }
        return items
    }

    private fun parseDate(dateStr: String): Long {
        if (dateStr.isBlank()) return System.currentTimeMillis()
        for (fmt in dateFormats) {
            try {
                return fmt.parse(dateStr.trim())?.time ?: continue
            } catch (_: Exception) {}
        }
        return System.currentTimeMillis()
    }

    private fun stripHtml(html: String): String =
        html.replace(Regex("<[^>]*>"), "").replace("&amp;", "&")
            .replace("&lt;", "<").replace("&gt;", ">")
            .replace("&quot;", "\"").replace("&#39;", "'")
            .replace("&nbsp;", " ").trim()
}
