package com.ciro.aiseekho.mobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ciro.aiseekho.mobile.ui.theme.*
import com.ciro.aiseekho.mobile.ui.viewmodel.CiroViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimulationExecutionScreen(viewModel: CiroViewModel, navController: NavController? = null) {
    val uiState by viewModel.uiState.collectAsState()
    val analysis = uiState.currentAnalysis
    val simulation = analysis?.simulation

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF1E293B)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("⚡", fontSize = 18.sp)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                "Simulation",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground,
                                fontSize = 18.sp
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
                    actions = {
                        IconButton(onClick = {}) {
                            Icon(Icons.Default.Search, contentDescription = "Search", tint = MaterialTheme.colorScheme.onBackground)
                        }
                        // Notifications removed
                    }
                )
            }
        ) { padding ->
            if (analysis == null || simulation == null) {
                // No analysis — show prompt
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(32.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("🔬", fontSize = 48.sp)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "No Simulation Data",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onBackground,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Submit a crisis report from the Dashboard to run the CIRO simulation engine and see before/after comparisons.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                lineHeight = 22.sp
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            Button(
                                onClick = { navController?.navigate("dashboard") },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Text("Go to Dashboard", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            } else {
                val before = simulation.before
                val after = simulation.after

                Column(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    // ══════════════════════════════════════════
                    //  RESPONSE TIME COMPARISON
                    // ══════════════════════════════════════════
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "RESPONSE TIME IMPROVEMENT",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                // Before
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("BEFORE", style = MaterialTheme.typography.labelSmall, color = ErrorRed, letterSpacing = 1.sp)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Box(
                                        modifier = Modifier
                                            .size(80.dp)
                                            .clip(CircleShape)
                                            .background(ErrorRed.copy(alpha = 0.15f))
                                            .border(2.dp, ErrorRed.copy(alpha = 0.4f), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(
                                                "${after.responseTimeBeforeMin}",
                                                style = MaterialTheme.typography.headlineMedium,
                                                color = ErrorRed,
                                                fontWeight = FontWeight.Black
                                            )
                                            Text("min", style = MaterialTheme.typography.labelSmall, color = ErrorRed)
                                        }
                                    }
                                }

                                // Arrow
                                Column(
                                    modifier = Modifier.padding(top = 24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("→", fontSize = 28.sp, color = MaterialTheme.colorScheme.primary)
                                    val improvement = if (after.responseTimeBeforeMin > 0) {
                                        ((after.responseTimeBeforeMin - after.responseTimeAfterMin).toFloat() / after.responseTimeBeforeMin * 100).toInt()
                                    } else 0
                                    Text(
                                        "-${improvement}%",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = SuccessGreen,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                // After
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("AFTER", style = MaterialTheme.typography.labelSmall, color = SuccessGreen, letterSpacing = 1.sp)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Box(
                                        modifier = Modifier
                                            .size(80.dp)
                                            .clip(CircleShape)
                                            .background(SuccessGreen.copy(alpha = 0.15f))
                                            .border(2.dp, SuccessGreen.copy(alpha = 0.4f), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(
                                                "${after.responseTimeAfterMin}",
                                                style = MaterialTheme.typography.headlineMedium,
                                                color = SuccessGreen,
                                                fontWeight = FontWeight.Black
                                            )
                                            Text("min", style = MaterialTheme.typography.labelSmall, color = SuccessGreen)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // ══════════════════════════════════════════
                    //  CONGESTION COMPARISON
                    // ══════════════════════════════════════════
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        CongestionCard(
                            label = "BEFORE",
                            level = before.congestionLevel,
                            color = ErrorRed,
                            modifier = Modifier.weight(1f)
                        )
                        CongestionCard(
                            label = "AFTER",
                            level = after.congestionLevel,
                            color = SuccessGreen,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // ══════════════════════════════════════════
                    //  ROUTES — BEFORE
                    // ══════════════════════════════════════════
                    if (before.routes.isNotEmpty()) {
                        SectionCard(
                            title = "ROUTES — BEFORE CIRO",
                            emoji = "🚗",
                            color = ErrorRed
                        ) {
                            before.routes.forEach { (route, status) ->
                                RouteRow(route = route, status = status.toString(), isBefore = true)
                            }
                        }
                    }

                    // ══════════════════════════════════════════
                    //  ROUTES — AFTER
                    // ══════════════════════════════════════════
                    if (after.routes.isNotEmpty()) {
                        SectionCard(
                            title = "ROUTES — AFTER CIRO",
                            emoji = "✅",
                            color = SuccessGreen
                        ) {
                            after.routes.forEach { (route, status) ->
                                RouteRow(route = route, status = status.toString(), isBefore = false)
                            }
                        }
                    }

                    // ══════════════════════════════════════════
                    //  EMERGENCY TICKETS
                    // ══════════════════════════════════════════
                    if (before.emergencyTickets.isNotEmpty() || after.emergencyTickets.isNotEmpty()) {
                        SectionCard(
                            title = "EMERGENCY TICKETS",
                            emoji = "🎫",
                            color = WarningOrange
                        ) {
                            if (before.emergencyTickets.isNotEmpty()) {
                                Text("Before:", style = MaterialTheme.typography.labelSmall, color = ErrorRed, fontWeight = FontWeight.Bold)
                                before.emergencyTickets.forEach { ticket ->
                                    Text("• $ticket", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 20.sp)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                            if (after.emergencyTickets.isNotEmpty()) {
                                Text("After:", style = MaterialTheme.typography.labelSmall, color = SuccessGreen, fontWeight = FontWeight.Bold)
                                after.emergencyTickets.forEach { ticket ->
                                    Text("• $ticket", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 20.sp)
                                }
                            }
                        }
                    }

                    // ══════════════════════════════════════════
                    //  ACTIVE ALERTS
                    // ══════════════════════════════════════════
                    if (before.activeAlerts.isNotEmpty() || after.activeAlerts.isNotEmpty()) {
                        SectionCard(
                            title = "ACTIVE ALERTS",
                            emoji = "🔔",
                            color = MaterialTheme.colorScheme.primary
                        ) {
                            if (before.activeAlerts.isNotEmpty()) {
                                Text("Before (${before.activeAlerts.size}):", style = MaterialTheme.typography.labelSmall, color = ErrorRed, fontWeight = FontWeight.Bold)
                                before.activeAlerts.forEach { alert ->
                                    Text("• $alert", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 20.sp)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                            if (after.activeAlerts.isNotEmpty()) {
                                Text("After (${after.activeAlerts.size}):", style = MaterialTheme.typography.labelSmall, color = SuccessGreen, fontWeight = FontWeight.Bold)
                                after.activeAlerts.forEach { alert ->
                                    Text("• $alert", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 20.sp)
                                }
                            }
                        }
                    }

                    // ══════════════════════════════════════════
                    //  PIPELINE INFO
                    // ══════════════════════════════════════════
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "SIMULATION METADATA",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Incident ID", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(analysis.incidentId.take(20), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onBackground)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Processing", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("${analysis.processingTimeMs}ms", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

@Composable
private fun CongestionCard(label: String, level: Int, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = color, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "$level%",
                style = MaterialTheme.typography.headlineMedium,
                color = color,
                fontWeight = FontWeight.Black
            )
            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = level / 100f,
                modifier = Modifier.fillMaxWidth().height(5.dp).clip(RoundedCornerShape(3.dp)),
                color = color,
                trackColor = Color(0xFF2A3A4A)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text("Congestion", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    emoji: String,
    color: Color,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(emoji, fontSize = 16.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    title,
                    style = MaterialTheme.typography.labelSmall,
                    color = color,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
private fun RouteRow(route: String, status: String, isBefore: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            route,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.weight(1f)
        )
        Surface(
            shape = RoundedCornerShape(6.dp),
            color = if (isBefore) ErrorRed.copy(alpha = 0.15f) else SuccessGreen.copy(alpha = 0.15f)
        ) {
            Text(
                status,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                color = if (isBefore) ErrorRed else SuccessGreen,
                style = MaterialTheme.typography.labelSmall,
                fontSize = 10.sp
            )
        }
    }
}