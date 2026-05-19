package com.ciro.aiseekho.mobile.data.api

import com.ciro.aiseekho.mobile.data.model.*
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Retrofit API service for the CIRO backend.
 * Maps to the FastAPI endpoints defined in backend/main.py.
 */
interface CiroApiService {

    // ── Auth ──
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @POST("auth/verify")
    suspend fun verifyEmail(@Body request: VerifyRequest): AuthResponse

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("auth/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): AuthResponse

    @POST("auth/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): AuthResponse

    // ── Health ──
    @GET("health")
    suspend fun healthCheck(): HealthResponse

    // ── Pipeline ──
    @POST("analyze")
    suspend fun analyzeCrisis(@Body request: AnalyzeRequest): AnalyzeResponse

    // ── Incidents ──
    @GET("incidents")
    suspend fun getIncidents(): IncidentsResponse

    // ── System State ──
    @GET("system-state")
    suspend fun getSystemState(): SystemStateResponse

    // ── Social Media ──
    @GET("mock/social")
    suspend fun getSocialSignals(@Query("area") area: String): SocialMediaResponse

    // ── Cache ──
    @POST("cache/clear")
    suspend fun clearCache(): Map<String, Any>
}
