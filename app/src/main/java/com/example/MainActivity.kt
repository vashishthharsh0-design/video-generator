package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.IosShare
import androidx.compose.material.icons.filled.MovieFilter
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.CloudDone
import androidx.compose.material.icons.outlined.GraphicEq
import androidx.compose.material.icons.outlined.IosShare
import androidx.compose.material.icons.outlined.MovieFilter
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.AudioNoiseReductionScreen
import com.example.ui.screens.ExportScreen
import com.example.ui.screens.GenerateScreen
import com.example.ui.screens.ProjectsCloudScreen
import com.example.ui.screens.StudioScreen
import com.example.ui.theme.CinemaAccentMagenta
import com.example.ui.theme.CinemaPrimaryCyan
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.CineFlowViewModel

enum class CineFlowTab(
    val title: String,
    val selectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val unselectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val testTag: String
) {
    STUDIO("Studio", Icons.Filled.MovieFilter, Icons.Outlined.MovieFilter, "nav_tab_studio"),
    GENERATE("Generate", Icons.Filled.AutoAwesome, Icons.Outlined.AutoAwesome, "nav_tab_generate"),
    AUDIO_AI("Audio AI", Icons.Filled.GraphicEq, Icons.Outlined.GraphicEq, "nav_tab_audio"),
    CLOUD("Projects", Icons.Filled.CloudDone, Icons.Outlined.CloudDone, "nav_tab_cloud"),
    EXPORT("Export", Icons.Filled.IosShare, Icons.Outlined.IosShare, "nav_tab_export")
}

class MainActivity : ComponentActivity() {

    private val viewModel: CineFlowViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isDarkMode by viewModel.isDarkMode.collectAsState()

            MyApplicationTheme(darkTheme = isDarkMode) {
                MainAppContent(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppContent(viewModel: CineFlowViewModel) {
    var currentTab by rememberSaveable { mutableStateOf(CineFlowTab.STUDIO) }

    // If on a secondary tab, pressing Back returns to the Studio timeline
    if (currentTab != CineFlowTab.STUDIO) {
        BackHandler {
            currentTab = CineFlowTab.STUDIO
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF090A0F)),
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF0C0E16),
                contentColor = Color.White,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .navigationBarsPadding()
                    .testTag("cineflow_bottom_nav")
            ) {
                CineFlowTab.values().forEach { tab ->
                    val isSelected = currentTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentTab = tab },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                contentDescription = tab.title,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        label = {
                            Text(
                                text = tab.title,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF090A0F),
                            selectedTextColor = CinemaPrimaryCyan,
                            indicatorColor = CinemaPrimaryCyan,
                            unselectedIconColor = Color(0xFF94A3B8),
                            unselectedTextColor = Color(0xFF94A3B8)
                        ),
                        modifier = Modifier.testTag(tab.testTag)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                CineFlowTab.STUDIO -> StudioScreen(
                    viewModel = viewModel,
                    onNavigateToGenerate = { currentTab = CineFlowTab.GENERATE }
                )
                CineFlowTab.GENERATE -> GenerateScreen(
                    viewModel = viewModel,
                    onNavigateToStudio = { currentTab = CineFlowTab.STUDIO }
                )
                CineFlowTab.AUDIO_AI -> AudioNoiseReductionScreen(
                    viewModel = viewModel
                )
                CineFlowTab.CLOUD -> ProjectsCloudScreen(
                    viewModel = viewModel,
                    onNavigateToStudio = { currentTab = CineFlowTab.STUDIO }
                )
                CineFlowTab.EXPORT -> ExportScreen(
                    viewModel = viewModel
                )
            }
        }
    }
}
