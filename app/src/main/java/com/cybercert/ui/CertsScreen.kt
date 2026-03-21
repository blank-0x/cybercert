package com.cybercert.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cybercert.model.CatalogCert
import com.cybercert.model.CertStatus
import com.cybercert.model.Certification
import com.cybercert.viewmodel.CertsViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

@Composable
fun CertsScreen(viewModel: CertsViewModel, modifier: Modifier = Modifier) {
    val certs by viewModel.certs.collectAsState()
    val catalog by viewModel.catalog.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var showDetailDialog by remember { mutableStateOf<Certification?>(null) }

    val filters = listOf("All", "In Progress", "Completed")

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = Color(0xFF00D4FF)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add certification", tint = Color.Black)
            }
        },
        containerColor = Color(0xFF0A0A0A)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Text(
                text = "My Certifications",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )

            // Filter chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                items(filters) { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { viewModel.setFilter(filter) },
                        label = { Text(filter) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF00D4FF),
                            selectedLabelColor = Color.Black
                        )
                    )
                }
            }

            val filtered = viewModel.filteredCerts()
            if (filtered.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No certifications yet.\nTap + to add one!",
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filtered, key = { it.id }) { cert ->
                        CertCard(cert = cert, onClick = { showDetailDialog = cert })
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddCertDialog(
            catalog = catalog,
            trackedIds = certs.map { it.id }.toSet(),
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
            onUpdate = { updated -> viewModel.updateCert(updated) },
            onDelete = { viewModel.deleteCert(cert); showDetailDialog = null },
            onLogSession = { minutes -> viewModel.logStudySession(cert.id, minutes) },
            onDismiss = { showDetailDialog = null }
        )
    }
}

@Composable
fun CertCard(cert: Certification, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF141414)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = cert.provider,
                        color = Color(0xFF00D4FF),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = cert.name,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Text(
                        text = cert.code,
                        color = Color.Gray,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp
                    )
                }
                StatusBadge(cert.status)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Progress bar
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Progress", color = Color.Gray, fontSize = 12.sp)
                    Text("${cert.progressPercent}%", color = Color.Gray, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { cert.progressPercent / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = Color(0xFF00D4FF),
                    trackColor = Color(0xFF2A2A2A)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Schedule,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "%.1fh studied".format(cert.studyHoursTotal),
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }
                cert.examDate?.let { examMs ->
                    val daysLeft = TimeUnit.MILLISECONDS.toDays(examMs - System.currentTimeMillis())
                    val color = if (daysLeft < 30) Color(0xFFFF6B35) else Color.Gray
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = color,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (daysLeft >= 0) "${daysLeft}d to exam" else "Exam passed",
                            color = color,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatusBadge(status: CertStatus) {
    val (text, color) = when (status) {
        CertStatus.NOT_STARTED -> "Not Started" to Color(0xFF444444)
        CertStatus.IN_PROGRESS -> "In Progress" to Color(0xFF00D4FF)
        CertStatus.COMPLETED -> "Completed" to Color(0xFF4CAF50)
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.2f))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(text = text, color = color, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun AddCertDialog(
    catalog: List<CatalogCert>,
    trackedIds: Set<String>,
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
        containerColor = Color(0xFF141414),
        title = {
            Column {
                Text("Add Certification", color = Color.White, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = search,
                    onValueChange = { search = it },
                    placeholder = { Text("Search certifications...", color = Color.Gray) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF00D4FF),
                        unfocusedBorderColor = Color(0xFF333333),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
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
                            containerColor = if (alreadyTracked) Color(0xFF1A1A1A) else Color(0xFF1E1E1E)
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
                                    color = if (alreadyTracked) Color.Gray else Color.White,
                                    fontWeight = FontWeight.Medium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "${cat.provider} · ${cat.code}",
                                    color = Color(0xFF00D4FF),
                                    fontSize = 12.sp
                                )
                            }
                            if (alreadyTracked) {
                                Text("Added", color = Color.Gray, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color(0xFF00D4FF))
            }
        }
    )
}

@Composable
fun CertDetailDialog(
    cert: Certification,
    onUpdate: (Certification) -> Unit,
    onDelete: () -> Unit,
    onLogSession: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var progress by remember { mutableStateOf(cert.progressPercent.toString()) }
    var status by remember { mutableStateOf(cert.status) }
    var notes by remember { mutableStateOf(cert.notes) }
    var sessionMinutes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF141414),
        title = {
            Column {
                Text(cert.name, color = Color.White, fontWeight = FontWeight.Bold)
                Text(cert.code, color = Color.Gray, fontFamily = FontFamily.Monospace, fontSize = 13.sp)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(cert.description, color = Color.Gray, fontSize = 13.sp, maxLines = 3, overflow = TextOverflow.Ellipsis)

                // Status selector
                Text("Status", color = Color(0xFF00D4FF), fontSize = 12.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    CertStatus.values().forEach { s ->
                        FilterChip(
                            selected = status == s,
                            onClick = { status = s },
                            label = { Text(s.name.replace("_", " "), fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF00D4FF),
                                selectedLabelColor = Color.Black
                            )
                        )
                    }
                }

                // Progress
                OutlinedTextField(
                    value = progress,
                    onValueChange = { if (it.length <= 3) progress = it },
                    label = { Text("Progress %", color = Color.Gray) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF00D4FF),
                        unfocusedBorderColor = Color(0xFF333333),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Log session
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = sessionMinutes,
                        onValueChange = { sessionMinutes = it },
                        label = { Text("Study minutes", color = Color.Gray) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF00D4FF),
                            unfocusedBorderColor = Color(0xFF333333),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    Button(
                        onClick = {
                            sessionMinutes.toIntOrNull()?.let { onLogSession(it) }
                            sessionMinutes = ""
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00D4FF))
                    ) {
                        Text("Log", color = Color.Black)
                    }
                }

                Text("${cert.studyHoursTotal.let { "%.1f".format(it) }}h total studied", color = Color.Gray, fontSize = 12.sp)
            }
        },
        confirmButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextButton(onClick = onDelete) {
                    Text("Delete", color = Color(0xFFFF6B35))
                }
                TextButton(
                    onClick = {
                        onUpdate(cert.copy(
                            status = status,
                            progressPercent = progress.toIntOrNull()?.coerceIn(0, 100) ?: cert.progressPercent,
                            notes = notes
                        ))
                        onDismiss()
                    }
                ) {
                    Text("Save", color = Color(0xFF00D4FF))
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = Color.Gray)
            }
        }
    )
}
