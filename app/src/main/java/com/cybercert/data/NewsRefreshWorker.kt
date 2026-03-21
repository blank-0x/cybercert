package com.cybercert.data

import android.content.Context
import androidx.work.*
import com.cybercert.model.NewsRepository
import java.util.concurrent.TimeUnit

class NewsRefreshWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {
    override suspend fun doWork(): Result {
        return try {
            val dao = AppDatabase.getInstance(applicationContext).newsItemDao()
            NewsRepository(dao).refresh()
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    companion object {
        const val WORK_NAME = "news_refresh"

        fun schedule(context: Context, intervalHours: Long) {
            val wm = WorkManager.getInstance(context)
            if (intervalHours == 0L) {
                wm.cancelUniqueWork(WORK_NAME)
                return
            }
            val request = PeriodicWorkRequestBuilder<NewsRefreshWorker>(
                intervalHours, TimeUnit.HOURS
            ).setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            ).build()

            wm.enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                request
            )
        }
    }
}
