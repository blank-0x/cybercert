package com.cybercert.data

import android.util.Log
import android.util.Xml
import com.cybercert.model.NewsCategory
import com.cybercert.model.NewsItem
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import okhttp3.OkHttpClient
import okhttp3.Request
import org.xmlpull.v1.XmlPullParser
import java.io.StringReader
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.UUID
import java.util.concurrent.TimeUnit

private const val TAG = "CyberCertRSS"

object RssParser {

    private val client = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    private val dateFormats = listOf(
        SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH),
        SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH),
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.ENGLISH)
    )

    /**
     * Each entry is (primary_url, fallback_url_or_null, display_name).
     * The parser tries primary first; if it fails, tries fallback.
     */
    private data class FeedConfig(val primary: String, val fallback: String?, val name: String)

    private val ALWAYS_ON_FEEDS = listOf(
        FeedConfig("https://feeds.feedburner.com/TheHackersNews",                    null,                                                           "The Hacker News"),
        FeedConfig("https://krebsonsecurity.com/feed/",                              null,                                                           "Krebs on Security"),
        FeedConfig("https://www.cisa.gov/feeds/hns.xml",                             "https://www.cisa.gov/uscert/ncas/alerts.xml",                  "CISA"),
        FeedConfig("https://www.ncsc.gov.uk/api/1/services/v1/report-rss-feed.xml",  "https://www.ncsc.gov.uk/feeds/news.xml",                       "NCSC UK"),
        FeedConfig("https://www.bleepingcomputer.com/feed/",                         null,                                                           "BleepingComputer"),
        FeedConfig("https://feeds.feedburner.com/Securityweek",                      null,                                                           "SecurityWeek"),
        FeedConfig("https://www.darkreading.com/rss.xml",                            null,                                                           "Dark Reading"),
        FeedConfig("https://threatpost.com/feed/",                                   null,                                                           "Threatpost"),
        FeedConfig("https://grahamcluley.com/feed/",                                 null,                                                           "Graham Cluley")
    )

    private val ES_FEEDS = listOf(
        FeedConfig("https://www.ccn-cert.cni.es/rss.xml", null, "CCN-CERT")
    )

    suspend fun fetchAll(): List<NewsItem> = coroutineScope {
        val feeds = ALWAYS_ON_FEEDS + if (Locale.getDefault().language == "es") ES_FEEDS else emptyList()

        val deferreds = feeds.map { config ->
            async {
                try {
                    fetchWithFallback(config)
                } catch (e: Exception) {
                    Log.e(TAG, "Unhandled error for ${config.name}: ${e.message}")
                    emptyList()
                }
            }
        }

        deferreds.awaitAll()
            .flatten()
            .distinctBy { it.url }
            .sortedByDescending { it.publishedAt }
    }

    private fun fetchWithFallback(config: FeedConfig): List<NewsItem> {
        val primary = tryFetchFeed(config.primary, config.name)
        if (primary.isNotEmpty()) return primary

        val fallback = config.fallback ?: return emptyList()
        Log.w(TAG, "${config.name}: primary failed, trying fallback: $fallback")
        return tryFetchFeed(fallback, config.name)
    }

    private fun tryFetchFeed(url: String, source: String): List<NewsItem> {
        return try {
            val request = Request.Builder().url(url).build()
            val (code, body) = client.newCall(request).execute().use { response ->
                response.code to (response.body?.string() ?: "")
            }
            if (code !in 200..299) {
                Log.w(TAG, "$source [$url] HTTP $code — body[:200]: ${body.take(200)}")
                return emptyList()
            }
            val items = parseRss(body, source)
            Log.i(TAG, "$source [$url] OK — ${items.size} items")
            items
        } catch (e: Exception) {
            Log.w(TAG, "$source [$url] exception: ${e.javaClass.simpleName}: ${e.message}")
            emptyList()
        }
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
                            "item", "entry" -> {
                                inItem = true
                                title = ""; description = ""; link = ""; pubDate = ""; imageUrl = null
                            }
                            "title"   -> if (inItem) title = parser.nextText()
                            "description", "summary" -> if (inItem) description = stripHtml(parser.nextText())
                            "link"    -> if (inItem) {
                                // Atom feeds use <link href="..."/> attribute; RSS uses text content
                                val href = parser.getAttributeValue(null, "href")
                                if (href != null) link = href else link = parser.nextText()
                            }
                            "pubDate", "published", "updated" -> if (inItem) pubDate = parser.nextText()
                            "enclosure" -> if (inItem) {
                                val type = parser.getAttributeValue(null, "type") ?: ""
                                if (type.startsWith("image")) imageUrl = parser.getAttributeValue(null, "url")
                            }
                            "media:thumbnail", "media:content" -> if (inItem) {
                                parser.getAttributeValue(null, "url")?.let { imageUrl = it }
                            }
                        }
                    }
                    XmlPullParser.END_TAG -> {
                        if ((parser.name == "item" || parser.name == "entry") && inItem) {
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
                                        publishedAt = parseDate(pubDate),
                                        category = detectCategory(title.trim(), description.take(300).trim())
                                    )
                                )
                            }
                        }
                    }
                }
                eventType = parser.next()
            }
        } catch (_: Exception) {
            // Return whatever was parsed before the error
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

    private fun detectCategory(title: String, description: String): NewsCategory {
        val text = (title + " " + description).lowercase()
        return when {
            text.contains(Regex("vulnerability|cve|patch|exploit")) -> NewsCategory.VULNERABILITIES
            text.contains(Regex("malware|ransomware|trojan|virus")) -> NewsCategory.MALWARE
            text.contains(Regex("breach|leak|stolen")) -> NewsCategory.BREACHES
            text.contains(Regex("phishing|social engineering|scam")) -> NewsCategory.PHISHING
            text.contains(Regex("tool|framework|release|open source")) -> NewsCategory.TOOLS
            else -> NewsCategory.GENERAL
        }
    }

    private fun stripHtml(html: String): String =
        html.replace(Regex("<[^>]*>"), "").replace("&amp;", "&")
            .replace("&lt;", "<").replace("&gt;", ">")
            .replace("&quot;", "\"").replace("&#39;", "'")
            .replace("&nbsp;", " ").trim()
}
