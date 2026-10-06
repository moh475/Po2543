package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.PhoneAndroid
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material.icons.outlined.Widgets
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.IslandStateManager
import com.example.ui.screens.CustomizerScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.ModulesScreen
import com.example.ui.screens.SimulatorScreen
import com.example.ui.theme.AppleBlue
import com.example.ui.theme.DynamicIslandTheme
import com.example.ui.theme.IslandBlack

enum class AppTab(
    val titleRes: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val tag: String
) {
    DASHBOARD(
        R.string.tab_dashboard,
        Icons.Filled.Dashboard,
        Icons.Outlined.Dashboard,
        "tab_dashboard"
    ),
    SIMULATOR(
        R.string.tab_simulator,
        Icons.Filled.PhoneAndroid,
        Icons.Outlined.PhoneAndroid,
        "tab_simulator"
    ),
    CUSTOMIZER(
        R.string.tab_customizer,
        Icons.Filled.Tune,
        Icons.Outlined.Tune,
        "tab_customizer"
    ),
    MODULES(
        R.string.tab_modules,
        Icons.Filled.Widgets,
        Icons.Outlined.Widgets,
        "tab_modules"
    )
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val stateManager = IslandStateManager.getInstance(this)

        setContent {
            DynamicIslandTheme(darkTheme = true) {
                var currentTab by remember { mutableStateOf(AppTab.DASHBOARD) }

                val config by stateManager.config.collectAsState()
                val primaryEvent by stateManager.primaryEvent.collectAsState()
                val secondaryEvent by stateManager.secondaryEvent.collectAsState()
                val expansionState by stateManager.expansionState.collectAsState()

                BackHandler(enabled = currentTab != AppTab.DASHBOARD) {
                    currentTab = AppTab.DASHBOARD
                }

                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(IslandBlack),
                    bottomBar = {
                        NavigationBar(
                            containerColor = Color(0xFF10131A),
                            contentColor = Color.White,
                            tonalElevation = 8.dp
                        ) {
                            AppTab.values().forEach { tab ->
                                val selected = currentTab == tab
                                NavigationBarItem(
                                    selected = selected,
                                    onClick = { currentTab = tab },
                                    icon = {
                                        Icon(
                                            imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                            contentDescription = stringResource(tab.titleRes),
                                            modifier = Modifier.size(22.dp)
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = stringResource(tab.titleRes),
                                            fontSize = 11.sp,
                                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = AppleBlue,
                                        selectedTextColor = AppleBlue,
                                        unselectedIconColor = Color(0xFF8E8E93),
                                        unselectedTextColor = Color(0xFF8E8E93),
                                        indicatorColor = Color(0xFF1B2333)
                                    ),
                                    modifier = Modifier.testTag(tab.tag)
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
                        AnimatedContent(
                            targetState = currentTab,
                            transitionSpec = { fadeIn() togetherWith fadeOut() },
                            label = "tabContent"
                        ) { targetTab ->
                            when (targetTab) {
                                AppTab.DASHBOARD -> {
                                    DashboardScreen(
                                        stateManager = stateManager,
                                        config = config,
                                        primaryEvent = primaryEvent,
                                        secondaryEvent = secondaryEvent,
                                        expansionState = expansionState,
                                        onNavigateToSimulator = { currentTab = AppTab.SIMULATOR },
                                        onNavigateToCustomizer = { currentTab = AppTab.CUSTOMIZER }
                                    )
                                }
                                AppTab.SIMULATOR -> {
                                    SimulatorScreen(
                                        stateManager = stateManager,
                                        config = config,
                                        primaryEvent = primaryEvent,
                                        secondaryEvent = secondaryEvent,
                                        expansionState = expansionState
                                    )
                                }
                                AppTab.CUSTOMIZER -> {
                                    CustomizerScreen(
                                        stateManager = stateManager,
                                        config = config,
                                        primaryEvent = primaryEvent,
                                        secondaryEvent = secondaryEvent,
                                        expansionState = expansionState
                                    )
                                }
                                AppTab.MODULES -> {
                                    ModulesScreen(
                                        stateManager = stateManager,
                                        config = config,
                                        primaryEvent = primaryEvent,
                                        secondaryEvent = secondaryEvent,
                                        expansionState = expansionState
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
