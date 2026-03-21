package com.cybercert.ui

import android.app.DatePickerDialog
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cybercert.model.CatalogCert
import com.cybercert.model.CertStatus
import com.cybercert.model.Certification
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cybercert.ui.theme.AppColors
import com.cybercert.viewmodel.CertsViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

@Composable
fun CertsScreen(
    viewModel: CertsViewModel,
    isDark: Boolean,
    listState: LazyListState = rememberLazyListState(),
    modifier: Modifier = Modifier
) {
    val c = AppColors(isDark)
    val certs by viewModel.certs.collectAsStateWithLifecycle()
    val catalog by viewModel.catalog.collectAsStateWithLifecycle()
    val selectedFilter by viewModel.selectedFilter.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }
    var showDetailDialog by remember { mutableStateOf<Certification?>(null) }

    val filters = listOf("All", "In Progress", "Completed")

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = c.accent
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add certification", tint = c.accentOnFill)
            }
        },
        containerColor = c.bg
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Text(
                text = "My Certifications",
                style = MaterialTheme.typography.headlineMedium,
                color = c.primaryText,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)
            )

            // Filter chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                items(filters) { filter ->
                    val isSelected = selectedFilter == filter
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setFilter(filter) },
                        label = { Text(filter, color = if (isSelected) c.chipSelText else c.chipUnselText) },
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = c.chipBorder,
                            selectedBorderColor = Color.Transparent
                        ),
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = c.chipUnselBg,
                            selectedContainerColor = c.chipSelBg,
                            labelColor = c.chipUnselText,
                            selectedLabelColor = c.chipSelText
                        )
                    )
                }
            }

            val filtered = viewModel.filteredCerts()
            if (filtered.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "No certifications yet.\nTap + to add one!",
                        color = c.secondaryText,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filtered, key = { it.id }, contentType = { "cert" }) { cert ->
                        CertCard(cert = cert, c = c, onClick = { showDetailDialog = cert })
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddCertDialog(
            catalog = catalog,
            trackedIds = certs.map { it.id }.toSet(),
            c = c,
            onAdd = { cat ->
                viewModel.addCertFromCatalog(cat)
                showAddDialog = false
            },
            onDismiss = { showAddDialog = false }
        )
    }

    showDetailDialog?.let { cert ->
        CertDetailDialog(
            cert = cert,
            c = c,
            onUpdate = { updated ->
                viewModel.updateCert(updated)
                showDetailDialog = updated
            },
            onDelete = { viewModel.deleteCert(cert); showDetailDialog = null },
            onLogSession = { minutes -> viewModel.logStudySession(cert.id, minutes) },
            onDismiss = { showDetailDialog = null }
        )
    }
}

@Composable
fun CertCard(cert: Certification, c: AppColors, onClick: () -> Unit) {
    val cardMod = if (c.isDark) {
        Modifier.fillMaxWidth()
    } else {
        Modifier.fillMaxWidth().border(1.dp, c.cardBorder, RoundedCornerShape(12.dp))
    }
    Card(
        onClick = onClick,
        modifier = cardMod,
        colors = CardDefaults.cardColors(containerColor = c.card),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (c.isDark) 0.dp else 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(cert.provider, color = c.accent, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    Text(cert.name, color = c.primaryText, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text(cert.code, color = c.secondaryText, fontFamily = FontFamily.Monospace, fontSize = 13.sp)
                }
                StatusBadge(cert.status, c)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Column {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Progress", color = c.secondaryText, fontSize = 12.sp)
                    Text("${cert.progressPercent}%", color = c.secondaryText, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { cert.progressPercent / 100f },
                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                    color = c.accent,
                    trackColor = c.progressTrack
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Schedule, contentDescription = null, tint = c.secondaryText, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("%.1fh studied".format(cert.studyHoursTotal), color = c.secondaryText, fontSize = 12.sp)
                }
                cert.examDate?.let { examMs ->
                    val daysLeft = TimeUnit.MILLISECONDS.toDays(examMs - System.currentTimeMillis())
                    val urgent = daysLeft in 0..29
                    val color = if (urgent) c.orange else c.primaryText
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = color, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (daysLeft >= 0) "${daysLeft}d to exam" else "Exam passed",
                            color = color,
                            fontSize = 12.sp,
                            fontWeight = if (urgent) FontWeight.SemiBold else FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatusBadge(status: CertStatus, c: AppColors) {
    val (text, textColor, borderColor) = when (status) {
        CertStatus.NOT_STARTED -> Triple("Not Started", c.badgeNotStartedText, c.badgeNotStartedBorder)
        CertStatus.IN_PROGRESS -> Triple("In Progress", c.badgeInProgressText, c.badgeInProgressBorder)
        CertStatus.COMPLETED   -> Triple("Completed",   c.badgeCompletedText,  c.badgeCompletedBorder)
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .border(1.dp, borderColor, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(text = text, color = textColor, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun AddCertDialog(
    catalog: List<CatalogCert>,
    trackedIds: Set<String>,
    c: AppColors,
    onAdd: (CatalogCert) -> Unit,
    onDismiss: () -> Unit
) {
    var search by remember { mutableStateOf("") }
    val filtered = catalog.filter {
        it.name.contains(search, ignoreCase = true) ||
        it.provider.contains(search, ignoreCase = true) ||
        it.code.contains(search, ignoreCase = true)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = c.dialogBg,
        title = {
            Column {
                Text("Add Certification", color = c.primaryText, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = search,
                    onValueChange = { search = it },
                    placeholder = { Text("Search certifications...", color = c.secondaryText) },
                    colors = outlinedFieldColors(c),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier.heightIn(max = 400.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(filtered, key = { it.id }) { cat ->
                    val alreadyTracked = cat.id in trackedIds
                    Card(
                        onClick = { if (!alreadyTracked) onAdd(cat) },
                        colors = CardDefaults.cardColors(
                            containerColor = if (alreadyTracked) c.subtleCard else c.card
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = cat.name,
                                    color = if (alreadyTracked) c.secondaryText else c.primaryText,
                                    fontWeight = FontWeight.Medium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text("${cat.provider} · ${cat.code}", color = c.accent, fontSize = 12.sp)
                            }
                            if (alreadyTracked) {
                                Text("Added", color = c.secondaryText, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = c.accent) }
        }
    )
}

@Composable
fun CertDetailDialog(
    cert: Certification,
    c: AppColors,
    onUpdate: (Certification) -> Unit,
    onDelete: () -> Unit,
    onLogSession: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var progress by remember { mutableStateOf(cert.progressPercent.toString()) }
    var progressTouched by remember { mutableStateOf(false) }
    var status by remember { mutableStateOf(cert.status) }
    var notes by remember { mutableStateOf(cert.notes) }
    var sessionMinutes by remember { mutableStateOf("") }
    var examDate by remember { mutableStateOf(cert.examDate) }
    var showConfirmDelete by remember { mutableStateOf(false) }
    val progressError = progressTouched && (progress.toIntOrNull()?.let { it !in 0..100 } ?: true)
    val dateFormatter = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())

    fun showDatePicker() {
        val cal = Calendar.getInstance()
        examDate?.let { cal.timeInMillis = it }
        DatePickerDialog(
            context,
            { _, year, month, day ->
                val picked = Calendar.getInstance().also {
                    it.set(year, month, day, 0, 0, 0)
                    it.set(Calendar.MILLISECOND, 0)
                }
                examDate = picked.timeInMillis
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    if (showConfirmDelete) {
        AlertDialog(
            onDismissRequest = { showConfirmDelete = false },
            containerColor = c.dialogBg2,
            title = { Text("Delete certification?", color = c.primaryText) },
            text = { Text("This will remove ${cert.name} from your tracker.", color = c.secondaryText) },
            confirmButton = {
                TextButton(onClick = { onDelete(); showConfirmDelete = false }) {
                    Text("Delete", color = c.orange)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDelete = false }) {
                    Text("Cancel", color = c.secondaryText)
                }
            }
        )
        return
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = c.dialogBg,
        title = {
            Column {
                Text(cert.name, color = c.primaryText, fontWeight = FontWeight.Bold)
                Text(cert.code, color = c.secondaryText, fontFamily = FontFamily.Monospace, fontSize = 13.sp)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(cert.description, color = c.secondaryText, fontSize = 13.sp, maxLines = 3, overflow = TextOverflow.Ellipsis)

                // Status chips
                Text("Status", color = c.accent, fontSize = 12.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    CertStatus.entries.forEach { s ->
                        val sel = status == s
                        FilterChip(
                            selected = sel,
                            onClick = { status = s },
                            label = { Text(s.name.replace("_", " "), fontSize = 10.sp, color = if (sel) c.chipSelText else c.chipUnselText) },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = c.chipUnselBg,
                                selectedContainerColor = c.chipSelBg
                            )
                        )
                    }
                }

                // Progress
                OutlinedTextField(
                    value = progress,
                    onValueChange = {
                        if (it.length <= 3) {
                            progress = it.filter(Char::isDigit)
                            progressTouched = true
                        }
                    },
                    label = { Text("Progress %", color = c.secondaryText) },
                    isError = progressError,
                    supportingText = if (progressError) {
                        { Text("Enter a value between 0 and 100", color = MaterialTheme.colorScheme.error) }
                    } else null,
                    colors = outlinedFieldColors(c),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Exam date
                Text("Exam Date", color = c.accent, fontSize = 12.sp)
                OutlinedButton(
                    onClick = { showDatePicker() },
                    border = BorderStroke(1.dp, c.inputBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = c.secondaryText, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = examDate?.let { dateFormatter.format(Date(it)) } ?: "Set exam date",
                        color = if (examDate != null) c.primaryText else c.secondaryText
                    )
                    if (examDate != null) {
                        Spacer(modifier = Modifier.weight(1f))
                        TextButton(onClick = { examDate = null }, contentPadding = PaddingValues(0.dp)) {
                            Text("Clear", color = c.secondaryText, fontSize = 11.sp)
                        }
                    }
                }

                // Study session log
                Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = sessionMinutes,
                        onValueChange = { v ->
                            val digits = v.filter(Char::isDigit)
                            sessionMinutes = if (digits.isEmpty()) "" else minOf(digits.toInt(), 999).toString()
                        },
                        label = { Text("Study minutes", color = c.secondaryText) },
                        supportingText = { Text("Max 999 min per session", color = c.secondaryText, fontSize = 11.sp) },
                        colors = outlinedFieldColors(c),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    Button(
                        onClick = {
                            sessionMinutes.toIntOrNull()?.let { onLogSession(it) }
                            sessionMinutes = ""
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = c.accent)
                    ) {
                        Text("Log", color = c.accentOnFill)
                    }
                }

                Text("%.1fh total studied".format(cert.studyHoursTotal), color = c.secondaryText, fontSize = 12.sp)
            }
        },
        confirmButton = {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                TextButton(onClick = { showConfirmDelete = true }) {
                    Text("Delete", color = c.deleteText, fontSize = 13.sp)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(onClick = onDismiss) { Text("Cancel", color = c.secondaryText) }
                    Button(
                        onClick = {
                            onUpdate(cert.copy(
                                status = status,
                                progressPercent = progress.toIntOrNull()?.coerceIn(0, 100) ?: cert.progressPercent,
                                notes = notes,
                                examDate = examDate
                            ))
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = c.accent)
                    ) {
                        Text("Save", color = c.accentOnFill, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        },
        dismissButton = {}
    )
}

@Composable
private fun outlinedFieldColors(c: AppColors) = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = c.accent,
    unfocusedBorderColor = c.inputBorder,
    focusedTextColor = c.inputText,
    unfocusedTextColor = c.inputText,
    focusedLabelColor = c.accent,
    unfocusedLabelColor = c.secondaryText,
    cursorColor = c.accent
)
