package com.cybercert.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cybercert.BuildConfig
import com.cybercert.data.NewsRefreshInterval
import com.cybercert.model.Certification
import com.cybercert.ui.theme.AppColors
import com.cybercert.viewmodel.HomeStats
import com.cybercert.viewmodel.HomeViewModel

@Composable
fun HomeScreen(viewModel: HomeViewModel, isDark: Boolean, modifier: Modifier = Modifier) {
    val c = AppColors(isDark)
    val stats by viewModel.stats.collectAsStateWithLifecycle()
    val currentlyStudying by viewModel.currentlyStudying.collectAsStateWithLifecycle()
    val newsRefreshInterval by viewModel.newsRefreshInterval.collectAsStateWithLifecycle()
    val examRemindersEnabled by viewModel.examRemindersEnabled.collectAsStateWithLifecycle()
    val examReminderDays by viewModel.examReminderDays.collectAsStateWithLifecycle()
    var showClearConfirm by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            "Profile",
            style = MaterialTheme.typography.headlineMedium,
            color = c.primaryText,
            fontWeight = FontWeight.Bold
        )

        StatsSection(stats = stats, c = c)

        if (currentlyStudying.isNotEmpty()) {
            CurrentlyStudyingSection(certs = currentlyStudying, c = c)
        }

        SettingsSection(
            isDark = isDark,
            c = c,
            newsRefreshInterval = newsRefreshInterval,
            examRemindersEnabled = examRemindersEnabled,
            examReminderDays = examReminderDays,
            onToggleTheme = { viewModel.setDarkTheme(!isDark) },
            onSetRefreshInterval = { viewModel.setNewsRefreshInterval(it) },
            onToggleReminders = { viewModel.setExamRemindersEnabled(it) },
            onSetReminderDays = { viewModel.setExamReminderDays(it) },
            onClearData = { showClearConfirm = true }
        )
    }

    if (showClearConfirm) {
        AlertDialog(
            onDismissRequest = { showClearConfirm = false },
            containerColor = c.dialogBg2,
            title = { Text("Clear all data?", color = c.primaryText) },
            text = { Text("This will delete all tracked certifications and study sessions. This cannot be undone.", color = c.secondaryText) },
            confirmButton = {
                TextButton(onClick = { viewModel.clearAllData(); showClearConfirm = false }) {
                    Text("Clear", color = c.orange)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirm = false }) {
                    Text("Cancel", color = c.secondaryText)
                }
            }
        )
    }
}

@Composable
fun StatsSection(stats: HomeStats, c: AppColors) {
    SectionLabel("Overview", c)
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        StatCard("Tracked",     stats.totalTracked.toString(), c.accent,                c, Modifier.weight(1f))
        StatCard("Completed",   stats.completed.toString(),    c.circleCompleted,       c, Modifier.weight(1f))
        StatCard("In Progress", stats.inProgress.toString(),   c.orange,                c, Modifier.weight(1f))
    }
    StatCard("Total Study Hours", "%.1fh".format(stats.totalStudyHours), c.accent, c, Modifier.fillMaxWidth())
}

@Composable
fun StatCard(label: String, value: String, valueColor: Color, c: AppColors, modifier: Modifier = Modifier) {
    Card(
        colors = CardDefaults.cardColors(containerColor = c.card),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = if (c.isDark) 0.dp else 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, color = valueColor, fontWeight = FontWeight.Bold, fontSize = 24.sp)
            Text(label, color = c.secondaryText, fontSize = 12.sp)
        }
    }
}

@Composable
fun CurrentlyStudyingSection(certs: List<Certification>, c: AppColors) {
    SectionLabel("Currently Studying", c)
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        certs.forEach { cert ->
            Card(
                colors = CardDefaults.cardColors(containerColor = c.card),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = if (c.isDark) 0.dp else 1.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(cert.name, color = c.primaryText, fontWeight = FontWeight.Medium)
                        Text(cert.provider, color = c.secondaryText, fontSize = 12.sp)
                    }
                    Text("${cert.progressPercent}%", color = c.accent, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsSection(
    isDark: Boolean,
    c: AppColors,
    newsRefreshInterval: NewsRefreshInterval,
    examRemindersEnabled: Boolean,
    examReminderDays: Int,
    onToggleTheme: () -> Unit,
    onSetRefreshInterval: (NewsRefreshInterval) -> Unit,
    onToggleReminders: (Boolean) -> Unit,
    onSetReminderDays: (Int) -> Unit,
    onClearData: () -> Unit
) {
    var showRefreshMenu by remember { mutableStateOf(false) }
    var showReminderDaysMenu by remember { mutableStateOf(false) }

    SectionLabel("Settings", c)

    Card(
        colors = CardDefaults.cardColors(containerColor = c.card),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = if (c.isDark) 0.dp else 1.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            // Dark mode toggle
            SettingsToggleRow(
                label = "Dark Mode",
                sublabel = "App appearance",
                checked = isDark,
                c = c,
                onToggle = { onToggleTheme() }
            )
            HorizontalDivider(color = c.divider)

            // News refresh
            SettingsMenuRow(
                label = "News Refresh",
                sublabel = "Background fetch interval",
                value = newsRefreshInterval.label,
                c = c,
                onClick = { showRefreshMenu = true }
            )
            if (showRefreshMenu) {
                DropdownMenu(expanded = true, onDismissRequest = { showRefreshMenu = false }) {
                    NewsRefreshInterval.entries.forEach { interval ->
                        DropdownMenuItem(
                            text = { Text(interval.label) },
                            onClick = { onSetRefreshInterval(interval); showRefreshMenu = false }
                        )
                    }
                }
            }
            HorizontalDivider(color = c.divider)

            // Exam reminders
            SettingsToggleRow(
                label = "Exam Reminders",
                sublabel = "Notify before exam date",
                checked = examRemindersEnabled,
                c = c,
                onToggle = onToggleReminders
            )
            if (examRemindersEnabled) {
                HorizontalDivider(color = c.divider)
                SettingsMenuRow(
                    label = "Remind me",
                    sublabel = null,
                    value = "${examReminderDays}d before",
                    c = c,
                    onClick = { showReminderDaysMenu = true }
                )
                if (showReminderDaysMenu) {
                    DropdownMenu(expanded = true, onDismissRequest = { showReminderDaysMenu = false }) {
                        listOf(7, 14, 30).forEach { days ->
                            DropdownMenuItem(
                                text = { Text("$days days before") },
                                onClick = { onSetReminderDays(days); showReminderDaysMenu = false }
                            )
                        }
                    }
                }
            }
            HorizontalDivider(color = c.divider)

            // Clear all data
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Clear All Data", color = c.primaryText)
                    Text("Delete all certs and sessions", color = c.secondaryText, fontSize = 12.sp)
                }
                TextButton(onClick = onClearData) { Text("Clear", color = c.orange) }
            }
            HorizontalDivider(color = c.divider)

            // Version
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 14.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("App Version", color = c.primaryText)
                Text(BuildConfig.VERSION_NAME, color = c.secondaryText)
            }
        }
    }

    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
private fun SettingsToggleRow(label: String, sublabel: String, checked: Boolean, c: AppColors, onToggle: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(label, color = c.primaryText)
            Text(sublabel, color = c.secondaryText, fontSize = 12.sp)
        }
        Switch(
            checked = checked,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor = c.accent,
                checkedTrackColor = c.accent.copy(alpha = 0.3f)
            )
        )
    }
}

@Composable
private fun SettingsMenuRow(label: String, sublabel: String?, value: String, c: AppColors, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(label, color = c.primaryText)
            if (sublabel != null) Text(sublabel, color = c.secondaryText, fontSize = 12.sp)
        }
        TextButton(onClick = onClick) { Text(value, color = c.accent) }
    }
}

@Composable
fun SectionLabel(text: String, c: AppColors) {
    Text(
        text = text.uppercase(),
        color = c.accent,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp,
        letterSpacing = 1.sp
    )
}
