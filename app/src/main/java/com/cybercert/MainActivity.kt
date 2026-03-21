package com.cybercert

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RssFeed
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cybercert.data.AppDatabase
import com.cybercert.data.SettingsRepository
import com.cybercert.model.CertRepository
import com.cybercert.ui.CareerScreen
import com.cybercert.ui.CertsScreen
import com.cybercert.ui.HomeScreen
import com.cybercert.ui.NewsScreen
import com.cybercert.ui.theme.AppColors
import com.cybercert.viewmodel.CertsViewModel
import com.cybercert.viewmodel.HomeViewModel
import com.cybercert.viewmodel.NewsViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val repository = CertRepository(this)
        val settingsRepository = SettingsRepository(this)
        setContent {
            CyberCertApp(repository = repository, settingsRepository = settingsRepository)
        }
    }
}

@Composable
fun CyberCertTheme(isDark: Boolean, content: @Composable () -> Unit) {
    val colorScheme = if (isDark) {
        darkColorScheme(
            background = Color(0xFF0A0A0A),
            surface = Color(0xFF141414),
            primary = Color(0xFF00D4FF),
            secondary = Color(0xFFFF6B35),
            onBackground = Color.White,
            onSurface = Color.White
        )
    } else {
        lightColorScheme(
            background = Color(0xFFF0F2F5),
            surface = Color(0xFFFFFFFF),
            primary = Color(0xFF0099BB),
            secondary = Color(0xFFE55A00),
            onBackground = Color(0xFF0A0A0A),
            onSurface = Color(0xFF0A0A0A),
            surfaceVariant = Color(0xFFE8EAF0),
            outline = Color(0xFFE0E0E0)
        )
    }
    MaterialTheme(colorScheme = colorScheme, content = content)
}

enum class Tab { CERTS, NEWS, CAREER, PROFILE }

@Composable
fun CyberCertApp(repository: CertRepository, settingsRepository: SettingsRepository) {
    var selectedTab by remember { mutableStateOf(Tab.CERTS) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val homeViewModel: HomeViewModel = viewModel(
        factory = HomeViewModel.Factory(repository, settingsRepository, context)
    )
    val isDark by homeViewModel.isDarkTheme.collectAsStateWithLifecycle()

    CyberCertTheme(isDark = isDark) {
        val certsViewModel: CertsViewModel = viewModel(
            factory = CertsViewModel.Factory(repository, context)
        )
        val newsViewModel: NewsViewModel = viewModel(
            factory = NewsViewModel.Factory(AppDatabase.getInstance(context).newsItemDao())
        )
        val c = AppColors(isDark)

        // Hoisted list states for double-tap scroll-to-top
        val certsListState = rememberLazyListState()
        val careerListState = rememberLazyListState()
        val newsListState = rememberLazyListState()

        // Last-tap timestamps for double-tap detection (400ms window)
        var lastCertsTap by remember { mutableLongStateOf(0L) }
        var lastNewsTap  by remember { mutableLongStateOf(0L) }
        var lastCareerTap by remember { mutableLongStateOf(0L) }

        val navItemColors = NavigationBarItemDefaults.colors(
            selectedIconColor = c.accent,
            selectedTextColor = c.accent,
            unselectedIconColor = c.secondaryText,
            unselectedTextColor = c.secondaryText,
            indicatorColor = c.accent.copy(alpha = 0.15f)
        )

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = c.bg,
            bottomBar = {
                NavigationBar(containerColor = c.navBg) {
                    NavigationBarItem(
                        selected = selectedTab == Tab.CERTS,
                        onClick = {
                            val now = System.currentTimeMillis()
                            if (now - lastCertsTap < 400) {
                                coroutineScope.launch { certsListState.animateScrollToItem(0) }
                            } else {
                                selectedTab = Tab.CERTS
                            }
                            lastCertsTap = now
                        },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Certs") },
                        label = { Text("Certs") },
                        colors = navItemColors
                    )
                    NavigationBarItem(
                        selected = selectedTab == Tab.NEWS,
                        onClick = {
                            val now = System.currentTimeMillis()
                            if (now - lastNewsTap < 400) {
                                newsViewModel.refresh()
                            } else {
                                selectedTab = Tab.NEWS
                            }
                            lastNewsTap = now
                        },
                        icon = { Icon(Icons.Default.RssFeed, contentDescription = "News") },
                        label = { Text("News") },
                        colors = navItemColors
                    )
                    NavigationBarItem(
                        selected = selectedTab == Tab.CAREER,
                        onClick = {
                            val now = System.currentTimeMillis()
                            if (now - lastCareerTap < 400) {
                                coroutineScope.launch { careerListState.animateScrollToItem(0) }
                            } else {
                                selectedTab = Tab.CAREER
                            }
                            lastCareerTap = now
                        },
                        icon = { Icon(Icons.Default.School, contentDescription = "Career") },
                        label = { Text("Career") },
                        colors = navItemColors
                    )
                    NavigationBarItem(
                        selected = selectedTab == Tab.PROFILE,
                        onClick = { selectedTab = Tab.PROFILE },
                        icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                        label = { Text("Profile") },
                        colors = navItemColors
                    )
                }
            }
        ) { innerPadding ->
            when (selectedTab) {
                Tab.CERTS -> CertsScreen(
                    viewModel = certsViewModel,
                    isDark = isDark,
                    listState = certsListState,
                    modifier = Modifier.padding(innerPadding)
                )
                Tab.NEWS -> NewsScreen(
                    viewModel = newsViewModel,
                    isDark = isDark,
                    listState = newsListState,
                    modifier = Modifier.padding(innerPadding)
                )
                Tab.CAREER -> CareerScreen(
                    viewModel = homeViewModel,
                    isDark = isDark,
                    listState = careerListState,
                    modifier = Modifier.padding(innerPadding)
                )
                Tab.PROFILE -> HomeScreen(
                    viewModel = homeViewModel,
                    isDark = isDark,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}
