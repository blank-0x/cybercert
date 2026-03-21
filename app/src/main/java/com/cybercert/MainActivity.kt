package com.cybercert

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RssFeed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cybercert.model.CertRepository
import com.cybercert.ui.CertsScreen
import com.cybercert.ui.HomeScreen
import com.cybercert.ui.NewsScreen
import com.cybercert.viewmodel.CertsViewModel
import com.cybercert.viewmodel.HomeViewModel
import com.cybercert.viewmodel.NewsViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val repository = CertRepository(this)
        setContent {
            CyberCertTheme {
                CyberCertApp(repository = repository)
            }
        }
    }
}

@Composable
fun CyberCertTheme(content: @Composable () -> Unit) {
    val colorScheme = darkColorScheme(
        background = Color(0xFF0A0A0A),
        surface = Color(0xFF141414),
        primary = Color(0xFF00D4FF),
        secondary = Color(0xFFFF6B35),
        onBackground = Color.White,
        onSurface = Color.White
    )
    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}

enum class Tab { CERTS, NEWS, PROFILE }

@Composable
fun CyberCertApp(repository: CertRepository) {
    var selectedTab by remember { mutableStateOf(Tab.CERTS) }
    val context = androidx.compose.ui.platform.LocalContext.current

    val certsViewModel: CertsViewModel = viewModel(factory = CertsViewModel.Factory(repository, context))
    val newsViewModel: NewsViewModel = viewModel()
    val homeViewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory(repository))

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFF0A0A0A),
        bottomBar = {
            NavigationBar(containerColor = Color(0xFF0D0D0D)) {
                NavigationBarItem(
                    selected = selectedTab == Tab.CERTS,
                    onClick = { selectedTab = Tab.CERTS },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Certs") },
                    label = { Text("Certs") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF00D4FF),
                        selectedTextColor = Color(0xFF00D4FF),
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray,
                        indicatorColor = Color(0xFF00D4FF).copy(alpha = 0.15f)
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == Tab.NEWS,
                    onClick = { selectedTab = Tab.NEWS },
                    icon = { Icon(Icons.Default.RssFeed, contentDescription = "News") },
                    label = { Text("News") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF00D4FF),
                        selectedTextColor = Color(0xFF00D4FF),
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray,
                        indicatorColor = Color(0xFF00D4FF).copy(alpha = 0.15f)
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == Tab.PROFILE,
                    onClick = { selectedTab = Tab.PROFILE },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                    label = { Text("Profile") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF00D4FF),
                        selectedTextColor = Color(0xFF00D4FF),
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray,
                        indicatorColor = Color(0xFF00D4FF).copy(alpha = 0.15f)
                    )
                )
            }
        }
    ) { innerPadding ->
        when (selectedTab) {
            Tab.CERTS -> CertsScreen(
                viewModel = certsViewModel,
                modifier = Modifier.padding(innerPadding)
            )
            Tab.NEWS -> NewsScreen(
                viewModel = newsViewModel,
                modifier = Modifier.padding(innerPadding)
            )
            Tab.PROFILE -> HomeScreen(
                viewModel = homeViewModel,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}
