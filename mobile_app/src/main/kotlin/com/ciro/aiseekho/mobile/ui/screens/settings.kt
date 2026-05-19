package com.ciro.aiseekho.mobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ciro.aiseekho.mobile.ui.viewmodel.CiroViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController, viewModel: CiroViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val user = uiState.currentUser

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // ── Profile Section ──
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Avatar with initials
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = user?.name?.firstOrNull()?.uppercase() ?: "U",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            user?.name ?: "Guest",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            user?.email ?: "Not logged in",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Appearance ──
            Text(
                "APPEARANCE",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (uiState.isDarkMode) Icons.Filled.DarkMode else Icons.Filled.LightMode,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            if (uiState.isDarkMode) "Dark Mode" else "Light Mode",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    Switch(
                        checked = uiState.isDarkMode,
                        onCheckedChange = { viewModel.toggleDarkMode() }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── System ──
            Text(
                "SYSTEM",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Card(modifier = Modifier.fillMaxWidth()) {
                Column {
                    // Agent Logs
                    ListItem(
                        headlineContent = { Text("Agent Logs") },
                        supportingContent = { Text("View pipeline execution traces") },
                        leadingContent = {
                            Icon(Icons.Filled.Memory, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Divider(modifier = Modifier.padding(horizontal = 16.dp))

                    // Server Info
                    ListItem(
                        headlineContent = { Text("Backend Server") },
                        supportingContent = { Text(uiState.serverUrl) },
                        leadingContent = {
                            Icon(Icons.Filled.Storage, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        },
                        trailingContent = {
                            val dotColor = if (uiState.isConnected) MaterialTheme.colorScheme.primary
                                          else MaterialTheme.colorScheme.error
                            Surface(shape = CircleShape, color = dotColor, modifier = Modifier.size(10.dp)) {}
                        }
                    )
                    Divider(modifier = Modifier.padding(horizontal = 16.dp))
                    Text(
                        "Select Platform:",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 16.dp)) {
                        RadioButton(
                            selected = uiState.serverUrl.contains("ciro-xi28m"),
                            onClick = { viewModel.connectToServer("https://ciro-xi28m.ondigitalocean.app/") }
                        )
                        Text("Platform 1 - Backup ($5)", style = MaterialTheme.typography.bodyMedium)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 8.dp)) {
                        RadioButton(
                            selected = uiState.serverUrl.contains("coral-app"),
                            onClick = { viewModel.connectToServer("https://coral-app-r3jy3.ondigitalocean.app/") }
                        )
                        Text("Platform 2 - Primary ($24)", style = MaterialTheme.typography.bodyMedium)
                    }
                    Divider(modifier = Modifier.padding(horizontal = 16.dp))

                    // App Version
                    ListItem(
                        headlineContent = { Text("App Version") },
                        supportingContent = { Text("v1.0.0 — AISeekho2026 Hackathon") },
                        leadingContent = {
                            Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Agent Logs Button ──
            OutlinedButton(
                onClick = { navController.navigate("agent_logs") },
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Icon(Icons.Filled.Memory, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("View Agent Logs")
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ── Logout ──
            OutlinedButton(
                onClick = {
                    viewModel.logout()
                    navController.navigate("login") { popUpTo(0) { inclusive = true } }
                },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(Icons.Filled.Logout, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Logout")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
