package com.cybercert.ui

import androidx.compose.foundation.background
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
import com.cybercert.model.CertStatus
import com.cybercert.model.Certification
import com.cybercert.viewmodel.HomeStats
import com.cybercert.viewmodel.HomeViewModel

@Composable
fun HomeScreen(viewModel: HomeViewModel, modifier: Modifier = Modifier) {
    val stats by viewModel.stats.collectAsState()
    val currentlyStudying by viewModel.currentlyStudying.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = "Profile",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )

        StatsSection(stats = stats)

        if (currentlyStudying.isNotEmpty()) {
            CurrentlyStudyingSection(certs = currentlyStudying)
        }

        SettingsPlaceholder()
    }
}

@Composable
fun StatsSection(stats: HomeStats) {
    Text("Overview", color = Color(0xFF00D4FF), fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard("Tracked", stats.totalTracked.toString(), Color(0xFF00D4FF), Modifier.weight(1f))
        StatCard("Completed", stats.completed.toString(), Color(0xFF4CAF50), Modifier.weight(1f))
        StatCard("In Progress", stats.inProgress.toString(), Color(0xFFFF6B35), Modifier.weight(1f))
    }
    StatCard(
        label = "Total Study Hours",
        value = "%.1f".format(stats.totalStudyHours) + "h",
        color = Color(0xFF00D4FF),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun StatCard(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF141414)),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, color = color, fontWeight = FontWeight.Bold, fontSize = 24.sp)
            Text(label, color = Color.Gray, fontSize = 12.sp)
        }
    }
}

@Composable
fun CurrentlyStudyingSection(certs: List<Certification>) {
    Text("Currently Studying", color = Color(0xFF00D4FF), fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        certs.forEach { cert ->
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF141414)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(cert.name, color = Color.White, fontWeight = FontWeight.Medium)
                        Text(cert.provider, color = Color.Gray, fontSize = 12.sp)
                    }
                    Text("${cert.progressPercent}%", color = Color(0xFF00D4FF), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun SettingsPlaceholder() {
    Text("Settings", color = Color(0xFF00D4FF), fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF141414)),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SettingsRow("Dark Mode", "Enabled")
            HorizontalDivider(color = Color(0xFF2A2A2A))
            SettingsRow("Notifications", "Coming soon")
            HorizontalDivider(color = Color(0xFF2A2A2A))
            SettingsRow("Export Data", "Coming soon")
            HorizontalDivider(color = Color(0xFF2A2A2A))
            SettingsRow("App Version", "0.1")
        }
    }
}

@Composable
fun SettingsRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.White)
        Text(value, color = Color.Gray)
    }
}
