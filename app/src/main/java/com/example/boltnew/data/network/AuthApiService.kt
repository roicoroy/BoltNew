package com.example.boltnew.data.network

import com.example.boltnew.data.model.auth.login.LoginRequest
import com.example.boltnew.data.model.auth.login.LoginResponse
import com.example.boltnew.data.model.auth.register.RegisterRequest
import com.example.boltnew.data.model.auth.register.RegisterResponse
import com.example.boltnew.data.model.auth.profile.StrapiProfile
import com.example.boltnew.data.model.auth.user.StrapiUser
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.Json

class AuthApiService {
    
    private val client = HttpClient.client
    private val baseUrl = "https://8c0c-86-156-238-78.ngrok-free.app/api"
    
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
        prettyPrint = true
    }
    
    suspend fun login(request: LoginRequest): Result<LoginResponse> {
        return try {
            val response = client.post("$baseUrl/auth/local") {
                contentType(ContentType.Application.Json)
                header("ngrok-skip-browser-warning", "true")
                setBody(request)
            }
            Result.success(response.body<LoginResponse>())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun register(request: RegisterRequest): Result<RegisterResponse> {
        return try {
            val response = client.post("$baseUrl/auth/local/register") {
                contentType(ContentType.Application.Json)
                header("ngrok-skip-browser-warning", "true")
                setBody(request)
            }
            Result.success(response.body<RegisterResponse>())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getCurrentUser(token: String): Result<StrapiUser> {
        return try {
            val response = client.get("$baseUrl/users/me") {
                contentType(ContentType.Application.Json)
                header("Authorization", "Bearer $token")
                header("ngrok-skip-browser-warning", "true")
                parameter("populate", "*")
            }
            Result.success(response.body<StrapiUser>())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getUserProfile(token: String): Result<StrapiProfile> {
        return try {
            println("🔍 Fetching user profile with token: ${token.take(20)}...")

            val response = client.get("$baseUrl/users/me") {
                contentType(ContentType.Application.Json)
                header("Authorization", "Bearer $token")
                header("ngrok-skip-browser-warning", "true")
                parameter("populate", "*")
            }
            
            // Get raw response text first
            val rawResponse = response.bodyAsText()
            println("📥 Raw API Response: $rawResponse")
            
            // Check if response is empty
            if (rawResponse.isBlank()) {
                println("❌ Empty response from API")
                return Result.failure(Exception("Empty response from profile API"))
            }
            
            // Try to parse the JSON structure manually first
            try {
                val jsonElement = json.parseToJsonElement(rawResponse)
                println("📊 Parsed JSON Structure: $jsonElement")
            } catch (e: Exception) {
                println("❌ Failed to parse JSON: ${e.message}")
                return Result.failure(Exception("Invalid JSON response: ${e.message}"))
            }
            
            // Now try to deserialize to StrapiProfile
            try {
                val profileResponse = json.decodeFromString<StrapiProfile>(rawResponse)
                println("✅ Successfully parsed StrapiProfile: $profileResponse")
                
                // Check if the data field is populated
                if (profileResponse.data.id == 0) {
                    println("⚠️ Profile data appears to be empty or default")
                }
                
                Result.success(profileResponse)
            } catch (e: Exception) {
                println("❌ Failed to deserialize to StrapiProfile: ${e.message}")
                
                // Try alternative population strategies
                return tryAlternativeProfileFetch(token)
            }
            
        } catch (e: Exception) {
            println("💥 Profile API Error: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }
    
    private suspend fun tryAlternativeProfileFetch(token: String): Result<StrapiProfile> {
        val strategies = listOf(
            "populate[profile][populate]=*",
            "populate[profile]=*&populate[addresses]=*&populate[adverts]=*",
            "populate=profile,addresses,adverts",
            "populate=deep"
        )
        
        for ((index, strategy) in strategies.withIndex()) {
            try {
                println("🔄 Trying strategy ${index + 1}: $strategy")
                
                val response = client.get("$baseUrl/users/me") {
                    contentType(ContentType.Application.Json)
                    header("Authorization", "Bearer $token")
                    header("ngrok-skip-browser-warning", "true")
                    url {
                        // Parse strategy and add parameters
                        if (strategy.contains("populate[")) {
                            // Complex populate strategy
                            parameters.append("populate[profile][populate]", "*")
                        } else {
                            parameters.append("populate", strategy.substringAfter("populate="))
                        }
                    }
                }
                
                val rawResponse = response.bodyAsText()
                println("📥 Strategy ${index + 1} Response: $rawResponse")
                
                if (rawResponse.isNotBlank()) {
                    try {
                        val profileResponse = json.decodeFromString<StrapiProfile>(rawResponse)
                        println("✅ Strategy ${index + 1} Success: $profileResponse")
                        return Result.success(profileResponse)
                    } catch (e: Exception) {
                        println("❌ Strategy ${index + 1} Parse Error: ${e.message}")
                        continue
                    }
                }
            } catch (e: Exception) {
                println("❌ Strategy ${index + 1} Request Error: ${e.message}")
                continue
            }
        }
        
        return Result.failure(Exception("All profile fetch strategies failed"))
    }
    
    // Method to get raw profile data for debugging
    suspend fun getProfileDataRaw(token: String): Result<String> {
        return try {
            val response = client.get("$baseUrl/users/me") {
                contentType(ContentType.Application.Json)
                header("Authorization", "Bearer $token")
                header("ngrok-skip-browser-warning", "true")
                parameter("populate", "*")
            }
            
            val rawData = response.bodyAsText()
            println("📋 Raw Profile Data: $rawData")
            Result.success(rawData)
        } catch (e: Exception) {
            println("❌ Raw Profile Data Error: ${e.message}")
            Result.failure(e)
        }
    }
    
    suspend fun refreshToken(token: String): Result<LoginResponse> {
        return try {
            val response = client.post("$baseUrl/auth/refresh") {
                contentType(ContentType.Application.Json)
                header("Authorization", "Bearer $token")
                header("ngrok-skip-browser-warning", "true")
            }
            Result.success(response.body<LoginResponse>())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun logout(token: String): Result<Boolean> {
        return try {
            val response = client.post("$baseUrl/auth/logout") {
                contentType(ContentType.Application.Json)
                header("Authorization", "Bearer $token")
                header("ngrok-skip-browser-warning", "true")
            }
            Result.success(response.status.isSuccess())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun forgotPassword(email: String): Result<Boolean> {
        return try {
            val response = client.post("$baseUrl/auth/forgot-password") {
                contentType(ContentType.Application.Json)
                header("ngrok-skip-browser-warning", "true")
                setBody(mapOf("email" to email))
            }
            Result.success(response.status.isSuccess())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun resetPassword(
        code: String,
        password: String,
        passwordConfirmation: String
    ): Result<LoginResponse> {
        return try {
            val response = client.post("$baseUrl/auth/reset-password") {
                contentType(ContentType.Application.Json)
                header("ngrok-skip-browser-warning", "true")
                setBody(mapOf(
                    "code" to code,
                    "password" to password,
                    "passwordConfirmation" to passwordConfirmation
                ))
            }
            Result.success(response.body<LoginResponse>())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun changePassword(
        token: String,
        currentPassword: String,
        password: String,
        passwordConfirmation: String
    ): Result<LoginResponse> {
        return try {
            val response = client.post("$baseUrl/auth/change-password") {
                contentType(ContentType.Application.Json)
                header("Authorization", "Bearer $token")
                header("ngrok-skip-browser-warning", "true")
                setBody(mapOf(
                    "currentPassword" to currentPassword,
                    "password" to password,
                    "passwordConfirmation" to passwordConfirmation
                ))
            }
            Result.success(response.body<LoginResponse>())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Health check for auth endpoints
    suspend fun healthCheck(): Result<Boolean> {
        return try {
            val response = client.get("$baseUrl/auth/local") {
                header("ngrok-skip-browser-warning", "true")
            }
            Result.success(response.status == HttpStatusCode.MethodNotAllowed || response.status.isSuccess())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}