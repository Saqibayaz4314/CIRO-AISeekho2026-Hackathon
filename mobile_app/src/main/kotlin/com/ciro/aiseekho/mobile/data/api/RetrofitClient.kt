package com.ciro.aiseekho.mobile.data.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Singleton Retrofit client for the CIRO backend.
 * Default base URL points to 10.0.2.2:8000 (Android emulator → host localhost).
 * Call [updateBaseUrl] to change at runtime (e.g., from Login screen).
 */
object RetrofitClient {

    // Production backend — DigitalOcean App Platform
    private var baseUrl: String = "https://ciro-xi28m.ondigitalocean.app/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)   // pipeline can take 45-55s
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private var retrofit: Retrofit = buildRetrofit()

    private fun buildRetrofit(): Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService: CiroApiService
        get() = retrofit.create(CiroApiService::class.java)

    /**
     * Update the backend URL at runtime.
     * Call this from the Login screen if the user enters a custom server URL.
     */
    fun updateBaseUrl(url: String) {
        var normalized = url.trim()
        if (!normalized.startsWith("http")) normalized = "http://$normalized"
        if (!normalized.endsWith("/")) normalized = "$normalized/"
        baseUrl = normalized
        retrofit = buildRetrofit()
    }

    fun getBaseUrl(): String = baseUrl
}
