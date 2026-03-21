package com.cybercert.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cybercert.model.CAREER_PATHS
import com.cybercert.model.CERT_DISPLAY_NAMES
import com.cybercert.model.CERT_WHY_MAP
import com.cybercert.model.CertStatus
import com.cybercert.model.Certification
import com.cybercert.ui.theme.AppColors
import com.cybercert.viewmodel.HomeViewModel

private val progressGreen = Color(0xFF00C853)

@Composable
fun CareerScreen(
    viewModel: HomeViewModel,
    isDark: Boolean,
    listState: LazyListState = rememberLazyListState(),
    modifier: Modifier = Modifier
) {
    val c = AppColors(isDark)
    val selectedCareerPath by viewModel.selectedCareerPath.collectAsStateWithLifecycle()
    val trackedCerts by viewModel.trackedCerts.collectAsStateWithLifecycle()
    val catalog by viewModel.catalog.collectAsStateWithLifecycle()

    val trackedMap = trackedCerts.associateBy { it.id }
    val catalogIds = catalog.map { it.id }.toSet()
    val currentPath = CAREER_PATHS.firstOrNull { it.name == selectedCareerPath }
    var dropdownExpanded by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxSize()) {
        // Header
        Text(
            text = "Career Path",
            style = MaterialTheme.typography.headlineMedium,
            color = c.primaryText,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 4.dp)
        )
        Text(
            text = "Select your target role and track the required certifications.",
            color = c.secondaryText,
            fontSize = 13.sp,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Role dropdown
        Box(modifier = Modifier.padding(horizontal = 16.dp)) {
            OutlinedButton(
                onClick = { dropdownExpanded = true },
                border = BorderStroke(1.dp, c.inputBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (selectedCareerPath.isBlank()) "Select career path…" else selectedCareerPath,
                    color = if (selectedCareerPath.isBlank()) c.secondaryText else c.primaryText,
                    modifier = Modifier.weight(1f)
                )
                Icon(Icons.Default.ExpandMore, contentDescription = null, tint = c.secondaryText)
            }
            DropdownMenu(
                expanded = dropdownExpanded,
                onDismissRequest = { dropdownExpanded = false },
                modifier = Modifier.fillMaxWidth(0.85f)
            ) {
                CAREER_PATHS.forEach { path ->
                    DropdownMenuItem(
                        text = { Text(path.name) },
                        onClick = { viewModel.setSelectedCareerPath(path.name); dropdownExpanded = false }
                    )
                }
            }
        }

        // Role description subtitle
        if (currentPath != null) {
            Text(
                text = currentPath.description,
                color = c.secondaryText,
                fontSize = 14.sp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        if (currentPath == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Choose a career path above", color = c.secondaryText, fontSize = 15.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("to see your certification roadmap.", color = c.secondaryText, fontSize = 13.sp)
                }
            }
        } else {
            // Path progress bar
            val completedCount = currentPath.certIds.count { certId ->
                trackedMap[certId]?.status == CertStatus.COMPLETED
            }
            val totalCount = currentPath.certIds.size
            val progress = if (totalCount > 0) completedCount.toFloat() / totalCount else 0f

            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "$completedCount of $totalCount certs completed",
                        color = c.secondaryText,
                        fontSize = 13.sp
                    )
                    Text(
                        "${(progress * 100).toInt()}%",
                        color = progressGreen,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = progressGreen,
                    trackColor = c.progressTrack
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Roadmap list
            LazyColumn(
                state = listState,
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                itemsIndexed(currentPath.certIds, key = { _, id -> id }, contentType = { _, _ -> "career_cert" }) { index, certId ->
                    val tracked = trackedMap[certId]
                    val isInCatalog = certId in catalogIds
                    val isCompleted = tracked?.status == CertStatus.COMPLETED
                    val isInProgress = tracked?.status == CertStatus.IN_PROGRESS

                    CareerCertRow(
                        index = index,
                        certId = certId,
                        tracked = tracked,
                        isInCatalog = isInCatalog,
                        isCompleted = isCompleted,
                        isInProgress = isInProgress,
                        isLast = index == currentPath.certIds.size - 1,
                        c = c,
                        onAdd = { viewModel.addCertFromCatalogId(certId) }
                    )
                }
            }
        }
    }
}

@Composable
private fun CareerCertRow(
    index: Int,
    certId: String,
    tracked: Certification?,
    isInCatalog: Boolean,
    isCompleted: Boolean,
    isInProgress: Boolean,
    isLast: Boolean,
    c: AppColors,
    onAdd: () -> Unit
) {
    val circleColor = when {
        isCompleted  -> c.circleCompleted
        isInProgress -> c.circleInProgress
        else         -> c.circleDefault
    }
    val cardBg = when {
        isCompleted  -> c.circleCompleted.copy(alpha = 0.08f)
        isInProgress -> c.circleInProgress.copy(alpha = 0.08f)
        else         -> c.card
    }

    val displayName = tracked?.name
        ?: CERT_DISPLAY_NAMES[certId]
        ?: certId.replace("-", " ").split(" ").joinToString(" ") { it.replaceFirstChar(Char::uppercase) }

    val whyText = CERT_WHY_MAP[certId]
    var whyExpanded by remember { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(containerColor = cardBg),
        shape = RoundedCornerShape(10.dp),
        modifier = (if (c.isDark) Modifier else Modifier.border(1.dp, c.cardBorder, RoundedCornerShape(10.dp)))
            .fillMaxWidth()
            .padding(bottom = if (isLast) 0.dp else 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Step indicator circle
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(2.dp, circleColor, RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (isCompleted) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = c.circleCompleted, modifier = Modifier.size(18.dp))
                    } else {
                        Text(
                            text = "${index + 1}",
                            color = if (isInProgress) c.circleInProgress else c.circleDefaultText,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Cert info
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = displayName,
                        color = when {
                            isCompleted  -> c.circleCompleted
                            isInProgress -> c.circleInProgress
                            else         -> c.primaryText
                        },
                        fontWeight = if (isCompleted || isInProgress) FontWeight.SemiBold else FontWeight.Normal,
                        fontSize = 15.sp
                    )
                    if (tracked?.code != null) {
                        Text(tracked.code, color = c.secondaryText, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                    }
                    if (isInProgress) {
                        Text("${tracked?.progressPercent ?: 0}% complete", color = c.accent, fontSize = 11.sp)
                    }
                    if (isCompleted) {
                        Text("Completed", color = c.circleCompleted, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    }
                }

                // Add button for not-started certs in catalog
                if (!isCompleted && !isInProgress && isInCatalog && tracked == null) {
                    FilledTonalButton(
                        onClick = onAdd,
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = c.accent.copy(alpha = 0.15f),
                            contentColor = c.accent
                        )
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add", fontSize = 12.sp)
                    }
                }
            }

            // "Why this cert?" expandable row
            if (whyText != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { whyExpanded = !whyExpanded }
                        .padding(vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Why this cert?",
                        color = c.accent,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        if (whyExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = c.accent,
                        modifier = Modifier.size(14.dp)
                    )
                }
                AnimatedVisibility(visible = whyExpanded) {
                    Text(
                        text = whyText,
                        color = c.secondaryText,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}
