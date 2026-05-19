package com.ciro.aiseekho.mobile.data.model

import com.google.gson.annotations.SerializedName

// ═══════════════════════════════════════════════════════════════
// Request Models
// ═══════════════════════════════════════════════════════════════

data class AnalyzeRequest(
    val text: String,
    val location: String? = null,
    @SerializedName("include_mock_signals") val includeMockSignals: Boolean = true,
    val scenario: String? = null
)

data class MultiCrisisRequest(
    val crises: List<CrisisInput>
)

data class CrisisInput(
    val text: String,
    val location: String? = null,
    @SerializedName("include_mock_signals") val includeMockSignals: Boolean = true,
    val scenario: String? = null
)

// ═══════════════════════════════════════════════════════════════
// Auth Models
// ═══════════════════════════════════════════════════════════════

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)

data class VerifyRequest(
    val email: String,
    val code: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class AuthResponse(
    val status: String = "",
    val message: String = "",
    val token: String? = null,
    val user: UserProfile? = null
)

data class UserProfile(
    val name: String = "",
    val email: String = ""
)

data class ForgotPasswordRequest(
    val email: String
)

data class ResetPasswordRequest(
    val email: String,
    val code: String,
    @SerializedName("new_password") val newPassword: String
)

// ═══════════════════════════════════════════════════════════════
// Response Models
// ═══════════════════════════════════════════════════════════════

data class AnalyzeResponse(
    @SerializedName("incident_id") val incidentId: String = "",
    val status: String = "",
    @SerializedName("processing_time_ms") val processingTimeMs: Int = 0,
    val crisis: CrisisClassification = CrisisClassification(),
    @SerializedName("signals_used") val signalsUsed: List<Signal> = emptyList(),
    val actions: List<ResponseAction> = emptyList(),
    @SerializedName("stakeholder_messages") val stakeholderMessages: StakeholderMessages = StakeholderMessages(),
    val simulation: SimulationResult = SimulationResult(),
    @SerializedName("agent_trace_id") val agentTraceId: String = ""
)

data class Signal(
    val source: String = "",
    val text: String = "",
    val credibility: Double = 0.0,
    val timestamp: String = "",
    val location: String? = null,
    val metadata: Map<String, Any>? = null
)

data class CrisisClassification(
    val type: String = "",
    val location: String = "",
    val severity: String = "",
    val confidence: Double = 0.0,
    @SerializedName("affected_population") val affectedPopulation: Int = 0,
    @SerializedName("affected_radius_km") val affectedRadiusKm: Double = 0.0,
    @SerializedName("expected_duration_hours") val expectedDurationHours: Double = 0.0,
    val reasoning: String = "",
    @SerializedName("conflicting_signals") val conflictingSignals: Boolean = false
)

data class ResponseAction(
    @SerializedName("action_id") val actionId: String = "",
    val type: String = "",
    val description: String = "",
    val entity: String = "",
    val priority: Int = 1,
    @SerializedName("estimated_impact") val estimatedImpact: String = "",
    @SerializedName("resources_allocated") val resourcesAllocated: Map<String, Any> = emptyMap()
)

data class StakeholderMessages(
    val public: String = "",
    val hospital: String = "",
    @SerializedName("traffic_police") val trafficPolice: String = "",
    val utility: String = "",
    val media: String = ""
)

data class SimulationResult(
    val before: SimulationBefore = SimulationBefore(),
    val after: SimulationAfter = SimulationAfter()
)

data class SimulationBefore(
    val routes: Map<String, String> = emptyMap(),
    @SerializedName("congestion_level") val congestionLevel: Int = 0,
    @SerializedName("emergency_tickets") val emergencyTickets: List<String> = emptyList(),
    @SerializedName("active_alerts") val activeAlerts: List<String> = emptyList()
)

data class SimulationAfter(
    val routes: Map<String, String> = emptyMap(),
    @SerializedName("congestion_level") val congestionLevel: Int = 0,
    @SerializedName("emergency_tickets") val emergencyTickets: List<String> = emptyList(),
    @SerializedName("active_alerts") val activeAlerts: List<String> = emptyList(),
    @SerializedName("response_time_before_min") val responseTimeBeforeMin: Int = 0,
    @SerializedName("response_time_after_min") val responseTimeAfterMin: Int = 0
)

// ═══════════════════════════════════════════════════════════════
// Health & System Models
// ═══════════════════════════════════════════════════════════════

data class HealthResponse(
    val status: String = "",
    val service: String = "",
    val version: String = "",
    val timestamp: String = ""
)

data class IncidentsResponse(
    val incidents: List<IncidentSummary> = emptyList(),
    val total: Int = 0
)

data class IncidentSummary(
    @SerializedName("incident_id") val incidentId: String = "",
    @SerializedName("crisis_type") val crisisType: String = "",
    val location: String = "",
    val severity: String = "",
    val confidence: Double = 0.0,
    @SerializedName("affected_population") val affectedPopulation: Int = 0,
    @SerializedName("processing_time_ms") val processingTimeMs: Int = 0,
    val timestamp: String = "",
    val status: String = ""
)

data class SystemStateResponse(
    val before: Map<String, Any> = emptyMap(),
    val after: Map<String, Any> = emptyMap()
)

// ═══════════════════════════════════════════════════════════════
// Social Media Models
// ═══════════════════════════════════════════════════════════════

data class SocialMediaResponse(
    val area: String = "",
    val signals: List<SocialSignal> = emptyList(),
    @SerializedName("total_mentions") val totalMentions: Int = 0,
    @SerializedName("dominant_keyword") val dominantKeyword: String = "",
    val source: String = ""
)

data class SocialSignal(
    val text: String = "",
    val language: String = "",
    val timestamp: String = "",
    val credibility: Double = 0.0,
    @SerializedName("mention_velocity") val mentionVelocity: Int = 0
)
