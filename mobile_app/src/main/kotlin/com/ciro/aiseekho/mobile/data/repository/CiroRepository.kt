package com.ciro.aiseekho.mobile.data.repository

import com.ciro.aiseekho.mobile.data.api.RetrofitClient
import com.ciro.aiseekho.mobile.data.model.*

/**
 * Repository layer — single source of truth for all backend data.
 * Wraps Retrofit calls with Result<T> for clean error handling.
 */
class CiroRepository {

    private val api get() = RetrofitClient.apiService

    // ── Auth ──

    suspend fun register(name: String, email: String, password: String): Result<AuthResponse> = runCatching {
        api.register(RegisterRequest(name = name, email = email, password = password))
    }

    suspend fun verifyEmail(email: String, code: String): Result<AuthResponse> = runCatching {
        api.verifyEmail(VerifyRequest(email = email, code = code))
    }

    suspend fun login(email: String, password: String): Result<AuthResponse> = runCatching {
        api.login(LoginRequest(email = email, password = password))
    }

    suspend fun forgotPassword(email: String): Result<AuthResponse> = runCatching {
        api.forgotPassword(ForgotPasswordRequest(email = email))
    }

    suspend fun resetPassword(email: String, code: String, newPassword: String): Result<AuthResponse> = runCatching {
        api.resetPassword(ResetPasswordRequest(email = email, code = code, newPassword = newPassword))
    }

    // ── Health ──

    suspend fun healthCheck(): Result<HealthResponse> = runCatching {
        api.healthCheck()
    }

    // ── Pipeline ──

    suspend fun analyzeCrisis(text: String, location: String?): Result<AnalyzeResponse> = runCatching {
        api.analyzeCrisis(
            AnalyzeRequest(
                text = text,
                location = location,
                includeMockSignals = true
            )
        )
    }

    // ── Incidents ──

    suspend fun getIncidents(): Result<IncidentsResponse> = runCatching {
        api.getIncidents()
    }

    // ── System State ──

    suspend fun getSystemState(): Result<SystemStateResponse> = runCatching {
        api.getSystemState()
    }

    // ── Social Media ──

    suspend fun getSocialSignals(area: String): Result<SocialMediaResponse> = runCatching {
        api.getSocialSignals(area)
    }
}
