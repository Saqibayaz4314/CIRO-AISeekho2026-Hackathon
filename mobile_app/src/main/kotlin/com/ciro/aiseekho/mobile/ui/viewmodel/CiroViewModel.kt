package com.ciro.aiseekho.mobile.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ciro.aiseekho.mobile.data.api.RetrofitClient
import com.ciro.aiseekho.mobile.data.model.*
import com.ciro.aiseekho.mobile.data.repository.CiroRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


/**
 * UI state shared across all screens in the app.
 */
data class CiroUiState(
    // Connection
    val isConnected: Boolean = false,
    val serverUrl: String = RetrofitClient.getBaseUrl(),

    // Auth
    val isLoggedIn: Boolean = false,
    val currentUser: UserProfile? = null,
    val authToken: String? = null,
    val pendingVerifyEmail: String? = null,
    val pendingResetEmail: String? = null,

    // Theme
    val isDarkMode: Boolean = true,

    // Loading / Error
    val isLoading: Boolean = false,
    val isAnalyzing: Boolean = false,
    val agentStep: Int = 0,          // 0=idle 1-5=agent steps during analysis
    val error: String? = null,
    val successMessage: String? = null,

    // Data
    val healthStatus: HealthResponse? = null,
    val currentAnalysis: AnalyzeResponse? = null,
    val incidents: List<IncidentSummary> = emptyList(),
    val myIncidentCount: Int = 0,    // count of incidents for THIS user session
    val socialSignals: SocialMediaResponse? = null,
)


/**
 * Shared ViewModel for the entire CIRO app.
 */
class CiroViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("ciro_prefs", Context.MODE_PRIVATE)

    private val repository = CiroRepository()

    private val _uiState = MutableStateFlow(CiroUiState())
    val uiState: StateFlow<CiroUiState> = _uiState.asStateFlow()

    init {
        val token = prefs.getString("auth_token", null)
        val name = prefs.getString("user_name", null)
        val email = prefs.getString("user_email", null)
        
        // Load saved server URL or default to Platform 2 (primary)
        val savedServerUrl = prefs.getString("server_url", "https://coral-app-r3jy3.ondigitalocean.app/")!!
        RetrofitClient.updateBaseUrl(savedServerUrl)

        if (token != null && name != null && email != null) {
            _uiState.update { 
                it.copy(
                    isLoggedIn = true,
                    authToken = token,
                    currentUser = UserProfile(name = name, email = email),
                    serverUrl = savedServerUrl
                )
            }
        } else {
            _uiState.update { it.copy(serverUrl = savedServerUrl) }
        }
        
        // Auto-connect to server on launch
        checkHealth()
    }

    // ── Auth ──

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, successMessage = null) }
            repository.register(name, email, password)
                .onSuccess { resp ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            pendingVerifyEmail = email,
                            successMessage = resp.message,
                            error = null
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(isLoading = false, error = parseError(e))
                    }
                }
        }
    }

    fun verifyEmail(code: String) {
        val email = _uiState.value.pendingVerifyEmail ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            repository.verifyEmail(email, code)
                .onSuccess { resp ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            successMessage = resp.message,
                            pendingVerifyEmail = null,
                            error = null
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(isLoading = false, error = parseError(e))
                    }
                }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            repository.login(email, password)
                .onSuccess { resp ->
                    prefs.edit().apply {
                        putString("auth_token", resp.token)
                        putString("user_name", resp.user?.name)
                        putString("user_email", resp.user?.email)
                        apply()
                    }
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isLoggedIn = true,
                            authToken = resp.token,
                            currentUser = resp.user,
                            error = null
                        )
                    }
                }
                .onFailure { e ->
                    val errorMsg = parseError(e)
                    _uiState.update {
                        // If user is not verified, set pendingVerifyEmail to trigger navigation
                        if (errorMsg.contains("not verified", ignoreCase = true)) {
                            it.copy(isLoading = false, error = errorMsg, pendingVerifyEmail = email)
                        } else {
                            it.copy(isLoading = false, error = errorMsg)
                        }
                    }
                }
        }
    }

    fun logout() {
        prefs.edit().clear().apply()
        _uiState.update {
            it.copy(
                isLoggedIn = false, currentUser = null, authToken = null,
                currentAnalysis = null, incidents = emptyList(), socialSignals = null
            )
        }
    }

    // ── Forgot / Reset Password ──

    fun forgotPassword(email: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, successMessage = null) }
            repository.forgotPassword(email)
                .onSuccess { resp ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            pendingResetEmail = email,
                            successMessage = resp.message,
                            error = null
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = parseError(e)) }
                }
        }
    }

    fun resetPassword(code: String, newPassword: String) {
        val email = _uiState.value.pendingResetEmail ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            repository.resetPassword(email, code, newPassword)
                .onSuccess { resp ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            pendingResetEmail = null,
                            successMessage = resp.message,
                            error = null
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = parseError(e)) }
                }
        }
    }

    // ── Server Connection ──

    fun connectToServer(url: String) {
        RetrofitClient.updateBaseUrl(url)
        val newUrl = RetrofitClient.getBaseUrl()
        prefs.edit().putString("server_url", newUrl).apply()
        _uiState.update { it.copy(serverUrl = newUrl) }
        checkHealth()
    }

    fun checkHealth() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            repository.healthCheck()
                .onSuccess { health ->
                    _uiState.update {
                        it.copy(isLoading = false, isConnected = true, healthStatus = health, error = null)
                    }
                    loadIncidents()
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(isLoading = false, isConnected = false, error = "Cannot reach server: ${e.message?.take(80)}")
                    }
                }
        }
    }

    // ── Crisis Pipeline ──

    fun submitCrisis(text: String, location: String?) {
        if (text.isBlank()) {
            _uiState.update { it.copy(error = "Crisis report text cannot be empty") }
            return
        }
        viewModelScope.launch {
            _uiState.update {
                it.copy(isAnalyzing = true, agentStep = 1, error = null, currentAnalysis = null, successMessage = null)
            }
            // Simulate agent step progression while the real API call runs in parallel
            val stepJob = launch {
                val stepDelays = longArrayOf(0, 4000, 8000, 14000, 20000)
                for (step in 2..5) {
                    delay(stepDelays[step - 1])
                    if (_uiState.value.isAnalyzing) {
                        _uiState.update { it.copy(agentStep = step) }
                    }
                }
            }
            repository.analyzeCrisis(text, location)
                .onSuccess { response ->
                    stepJob.cancel()
                    val newCount = _uiState.value.myIncidentCount + 1
                    _uiState.update {
                        it.copy(
                            isAnalyzing = false,
                            agentStep = 0,
                            currentAnalysis = response,
                            myIncidentCount = newCount,
                            successMessage = "Pipeline complete — ${response.crisis.type.replace("_", " ")} detected (${response.processingTimeMs}ms)",
                            error = null
                        )
                    }
                    loadIncidents()
                }
                .onFailure { e ->
                    stepJob.cancel()
                    _uiState.update {
                        it.copy(isAnalyzing = false, agentStep = 0, error = "Pipeline failed: ${e.message?.take(120)}")
                    }
                }
        }
    }

    // ── Incidents ──

    fun loadIncidents() {
        viewModelScope.launch {
            repository.getIncidents()
                .onSuccess { resp -> _uiState.update { it.copy(incidents = resp.incidents) } }
        }
    }

    // ── Social Media ──

    fun loadSocialSignals(area: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            repository.getSocialSignals(area)
                .onSuccess { resp ->
                    _uiState.update { it.copy(isLoading = false, socialSignals = resp, error = null) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = "Social feed error: ${e.message?.take(80)}") }
                }
        }
    }

    // ── Theme ──

    fun toggleDarkMode() {
        _uiState.update { it.copy(isDarkMode = !it.isDarkMode) }
    }

    // ── Helpers ──

    fun clearError() { _uiState.update { it.copy(error = null) } }
    fun clearSuccess() { _uiState.update { it.copy(successMessage = null) } }

    private fun parseError(e: Throwable): String {
        val msg = e.message ?: "Unknown error"
        // Try to extract "detail" from HTTP error body
        return if (msg.contains("\"detail\"")) {
            val start = msg.indexOf("\"detail\":\"") + 10
            val end = msg.indexOf("\"", start)
            if (start > 10 && end > start) msg.substring(start, end) else msg.take(120)
        } else {
            msg.take(120)
        }
    }
}
