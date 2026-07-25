package com.shanacoder.breathly

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.shanacoder.breathly.ui.theme.BreathlyTheme
import com.shanacoder.breathly.ui.screens.HomeScreen
import com.shanacoder.breathly.ui.screens.CustomScreen
import com.shanacoder.breathly.ui.screens.ProgressScreen
import com.shanacoder.breathly.ui.screens.SessionScreen
import com.shanacoder.breathly.util.PlayReviewHelper

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            BreathlyTheme {
                BreathlyApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BreathlyApp() {
    val navController = rememberNavController()
    val viewModel: BreathlyViewModel = viewModel()
    val context = LocalContext.current

    val requestReview = {
        PlayReviewHelper.launchReviewFlow(context)
    }

    Scaffold(
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            val isSessionScreen = currentDestination?.route?.startsWith("session") == true

            if (!isSessionScreen) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 24.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                            .shadow(elevation = 12.dp, shape = RoundedCornerShape(20.dp), spotColor = Color.Black.copy(alpha = 0.08f))
                            .background(Color.White, RoundedCornerShape(20.dp))
                            .padding(horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        val items = listOf(
                            Triple("Home", "home", Icons.Filled.GridView),
                            Triple("Custom", "custom", Icons.Filled.AddCircle),
                            Triple("Progress", "progress", Icons.Filled.BarChart)
                        )
                        items.forEach { (name, route, icon) ->
                            val isSelected = currentDestination?.hierarchy?.any { it.route == route } == true
                            val activeColor = Color(0xFF4CAF8A)
                            val inactiveColor = Color.Gray.copy(alpha = 0.6f)

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .clickable(
                                        interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        navController.navigate(route) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = name,
                                        tint = if (isSelected) activeColor else inactiveColor,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = name,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) activeColor else inactiveColor
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.fillMaxSize()
        ) {
            composable("home") {
                HomeScreen(
                    viewModel = viewModel,
                    bottomPadding = innerPadding.calculateBottomPadding(),
                    onNavigateToSession = { patternName, inhale, hold1, exhale, hold2, cycles ->
                        navController.navigate("session/$patternName/$inhale/$hold1/$exhale/$hold2/$cycles")
                    },
                    onNavigateToSettings = { navController.navigate("settings") },
                    onRequestReview = requestReview
                )
            }
            composable("custom") {
                CustomScreen(
                    viewModel = viewModel,
                    bottomPadding = innerPadding.calculateBottomPadding(),
                    onNavigateHome = {
                        navController.navigate("home") {
                            popUpTo(navController.graph.findStartDestination().id) {
                                inclusive = false
                            }
                        }
                    }
                )
            }
            composable("progress") {
                ProgressScreen(
                    viewModel = viewModel,
                    bottomPadding = innerPadding.calculateBottomPadding(),
                    onNavigateToSettings = { navController.navigate("settings") },
                    onRequestReview = requestReview
                )
            }
            composable("settings") {
                com.shanacoder.breathly.ui.screens.SettingsScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    bottomPadding = innerPadding.calculateBottomPadding(),
                    onRequestReview = requestReview
                )
            }
            composable("session/{name}/{inhale}/{hold1}/{exhale}/{hold2}/{cycles}") { backStackEntry ->
                val name = backStackEntry.arguments?.getString("name") ?: ""
                val inhale = backStackEntry.arguments?.getString("inhale")?.toFloatOrNull() ?: 4f
                val hold1 = backStackEntry.arguments?.getString("hold1")?.toFloatOrNull() ?: 0f
                val exhale = backStackEntry.arguments?.getString("exhale")?.toFloatOrNull() ?: 4f
                val hold2 = backStackEntry.arguments?.getString("hold2")?.toFloatOrNull() ?: 0f
                val cycles = backStackEntry.arguments?.getString("cycles")?.toIntOrNull() ?: 5

                SessionScreen(
                    name = name,
                    inhale = inhale,
                    hold1 = hold1,
                    exhale = exhale,
                    hold2 = hold2,
                    cycles = cycles,
                    onSessionComplete = { durationSeconds ->
                        viewModel.recordSession(durationSeconds, name)
                        navController.popBackStack()
                    },
                    onNavigateBack = { navController.popBackStack() },
                    onRequestReview = requestReview
                )
            }
        }
    }
}
