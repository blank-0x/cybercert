package com.cybercert.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import com.cybercert.MainActivity
import com.cybercert.R
import com.cybercert.data.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class ExamCountdownWidget : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        val pending = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = AppDatabase.getInstance(context)
                val certs = db.certDao().getAllCertsOnce()
                val now = System.currentTimeMillis()
                val nextExam = certs
                    .filter { it.examDate != null && it.examDate > now }
                    .minByOrNull { it.examDate!! }

                for (appWidgetId in appWidgetIds) {
                    val views = RemoteViews(context.packageName, R.layout.widget_exam_countdown)

                    if (nextExam != null) {
                        val daysLeft = TimeUnit.MILLISECONDS.toDays(nextExam.examDate!! - now)
                        views.setTextViewText(R.id.widget_cert_name, nextExam.name)
                        views.setTextViewText(R.id.widget_days_count, daysLeft.toString())
                        views.setTextViewText(R.id.widget_days_label, "days to exam")
                        views.setViewVisibility(R.id.widget_days_count, View.VISIBLE)
                        views.setViewVisibility(R.id.widget_days_label, View.VISIBLE)
                    } else {
                        views.setTextViewText(R.id.widget_cert_name, "No exam scheduled")
                        views.setViewVisibility(R.id.widget_days_count, View.GONE)
                        views.setViewVisibility(R.id.widget_days_label, View.GONE)
                    }

                    val launchIntent = Intent(context, MainActivity::class.java)
                    val pendingIntent = PendingIntent.getActivity(
                        context, 0, launchIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                    views.setOnClickPendingIntent(R.id.widget_root, pendingIntent)

                    appWidgetManager.updateAppWidget(appWidgetId, views)
                }
            } finally {
                pending.finish()
            }
        }
    }
}
