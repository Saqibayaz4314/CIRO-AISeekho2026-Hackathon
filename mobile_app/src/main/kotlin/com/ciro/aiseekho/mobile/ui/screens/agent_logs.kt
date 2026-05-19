package com.ciro.aiseekho.mobile.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ciro.aiseekho.mobile.ui.viewmodel.CiroViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgentLogsScreen(navController: NavController, viewModel: CiroViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val analysis = uiState.currentAnalysis

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Agent Logs") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp)) {
            if (analysis == null) {
                item {
                    Text(
                        "No pipeline data available. Submit a crisis report first.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            } else {
                // Pipeline Summary
                item {
                    Card(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("PIPELINE SUMMARY", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Incident: ${analysis.incidentId}", style = MaterialTheme.typography.bodyMedium)
                            Text("Trace: ${analysis.agentTraceId}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                            Text("Total Time: ${analysis.processingTimeMs}ms", style = MaterialTheme.typography.bodyMedium)
                            Text("Status: ${analysis.status}", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF2E7D32))
                        }
                    }
                }

                // Agent Steps
                val agents = listOf(
                    Triple("Signal Collector", "Collected ${analysis.signalsUsed.size} signals from multiple sources", "COMPLETE"),
                    Triple("Crisis Detector", "Detected: ${analysis.crisis.type.replace("_", " ")} at ${analysis.crisis.location}", "COMPLETE"),
                    Triple("Situation Analyzer", "Severity: ${analysis.crisis.severity}, Confidence: ${(analysis.crisis.confidence * 100).toInt()}%", "COMPLETE"),
                    Triple("Action Planner", "Generated ${analysis.actions.size} response actions", "COMPLETE"),
                    Triple("Executor", "Simulated response, congestion ${analysis.simulation.before.congestionLevel} -> ${analysis.simulation.after.congestionLevel}", "COMPLETE")
                )

                items(agents) { (name, detail, status) ->
                    Card(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(name, style = MaterialTheme.typography.titleSmall)
                                Text(detail, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Surface(shape = MaterialTheme.shapes.small, color = Color(0xFF2E7D32).copy(alpha = 0.15f)) {
                                Text(status, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), color = Color(0xFF2E7D32), style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }

                // Signals breakdown
                item {
                    Text("SIGNAL SOURCES", modifier = Modifier.padding(top = 16.dp, bottom = 8.dp), style = MaterialTheme.typography.titleSmall)
                }
                items(analysis.signalsUsed) { signal ->
                    Card(modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp)) {
                        Row(modifier = Modifier.padding(12.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(signal.source, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                                Text(signal.text.take(80), style = MaterialTheme.typography.bodySmall)
                            }
                            Text("${(signal.credibility * 100).toInt()}%", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}
