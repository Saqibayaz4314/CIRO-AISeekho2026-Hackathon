package com.ciro.aiseekho.mobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import com.ciro.aiseekho.mobile.ui.theme.AISeekhoTheme
import com.ciro.aiseekho.mobile.ui.viewmodel.CiroViewModel

// Bottom nav items — shorter labels for small screens
private data class NavItem(val route: String, val label: String, val icon: ImageVector)
private val bottomNavItems = listOf(
    NavItem("dashboard", "Home",       Icons.Default.GridView),
    NavItem("feed",      "Feed",       Icons.Default.Wifi),
    NavItem("analysis",  "Analysis",   Icons.Default.Psychology),
    NavItem("command",   "Command",    Icons.Default.Shield),
    NavItem("simulation","Simulate",   Icons.Default.Monitor),
)

// Routes that hide the bottom bar
private val authRoutes = setOf("login", "signup", "verify", "forgot_password", "reset_password", "settings", "agent_logs", "social")

@Composable
fun MainScreen(viewModel: CiroViewModel = viewModel()) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomBar = currentRoute !in authRoutes

    // Determine start destination based on auth state — prevents login screen flash
    val uiState by viewModel.uiState.collectAsState()
    val startDest = remember { if (uiState.isLoggedIn) "dashboard" else "login" }

    AISeekhoTheme(darkTheme = uiState.isDarkMode) {
        val bgColor = MaterialTheme.colorScheme.background
        val navBarColor = if (uiState.isDarkMode) Color(0xFF0D1520) else Color(0xFFF1F5F9)
        val navBarContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        val selectedColor = MaterialTheme.colorScheme.primary
        val unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant

        Scaffold(
            containerColor = bgColor,
            bottomBar = {
                if (showBottomBar) {
                    NavigationBar(
                        containerColor = navBarColor,
                        contentColor = navBarContentColor,
                        tonalElevation = 0.dp,
                        modifier = Modifier.height(72.dp)
                    ) {
                        bottomNavItems.forEach { item ->
                            val selected = currentRoute == item.route
                            NavigationBarItem(
                                icon = {
                                    Icon(
                                        item.icon,
                                        contentDescription = item.label,
                                        modifier = Modifier.size(20.dp)
                                    )
                                },
                                label = {
                                    Text(
                                        item.label,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontSize = 9.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        letterSpacing = 0.sp
                                    )
                                },
                                selected = selected,
                                onClick = {
                                    navController.navigate(item.route) {
                                        popUpTo("dashboard") { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = selectedColor,
                                    selectedTextColor = selectedColor,
                                    indicatorColor = Color.Transparent,
                                    unselectedIconColor = unselectedColor,
                                    unselectedTextColor = unselectedColor
                                )
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = startDest,
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .background(bgColor)
            ) {
                // Auth routes
                composable("login")           { LoginScreen(navController, viewModel) }
                composable("signup")          { SignupScreen(navController, viewModel) }
                composable("verify")          { VerifyEmailScreen(navController, viewModel) }
                composable("forgot_password") { ForgotPasswordScreen(navController, viewModel) }
                composable("reset_password")  { ResetPasswordScreen(navController, viewModel) }
                composable("settings")        { SettingsScreen(navController, viewModel) }
                composable("agent_logs")      { AgentLogsScreen(navController, viewModel) }

                // Main 5 tabs (matching PDF bottom nav)
                composable("dashboard")  { DashboardScreen(viewModel, navController) }
                composable("feed")       { IncomingSignalFeedScreen(viewModel, navController) }
                composable("analysis")   { AnalysisVerificationScreen(viewModel, navController) }
                composable("command")    { CommandCenterScreen(viewModel, navController) }
                composable("simulation") { SimulationExecutionScreen(viewModel, navController) }

                // Social — accessible but not in bottom bar
                composable("social")     { SocialMediaScreen(viewModel) }
            }
        }
    }
}