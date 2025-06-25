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
import kotlinx.serialization.json.*

class AuthApiService {
    
    private val client = HttpClient.client
    private val baseUrl = "https://8c0c-86-156-238-78.ngrok-free.app/api"
    
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
                parameter("populate", "profile,role")
            }
            Result.success(response.body<StrapiUser>())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getUserProfile(token: String): Result<StrapiProfile> {
        return try {
            println("🔍 Fetching user profile with token: ${token.take(20)}...")
            
            // Try the most comprehensive population strategy first
            val response = client.get("$baseUrl/users/me") {
                contentType(ContentType.Application.Json)
                header("Authorization", "Bearer $token")
                header("ngrok-skip-browser-warning", "true")
                // Use Strapi 5 deep population
                parameter("populate", "deep")
            }
            
            // Get raw response text for debugging
            val rawResponseText = response.bodyAsText()
            println("📥 Raw API Response: $rawResponseText")
            
            // Parse the JSON manually to understand structure
            val json = Json { ignoreUnknownKeys = true }
            val jsonElement = json.parseToJsonElement(rawResponseText)
            println("📊 Parsed JSON Structure: $jsonElement")
            
            // Try to parse as StrapiProfile
            val profileResponse = json.decodeFromString<StrapiProfile>(rawResponseText)
            println("✅ Successfully parsed StrapiProfile: $profileResponse")
            
            Result.success(profileResponse)
        } catch (e: Exception) {
            println("❌ Primary profile fetch failed: ${e.message}")
            
            // Fallback 1: Try with specific population
            try {
                println("🔄 Trying fallback with specific population...")
                val response = client.get("$baseUrl/users/me") {
                    contentType(ContentType.Application.Json)
                    header("Authorization", "Bearer $token")
                    header("ngrok-skip-browser-warning", "true")
                    parameter("populate[profile][populate][0]", "avatar")
                    parameter("populate[profile][populate][1]", "addresses")
                    parameter("populate[profile][populate][2]", "adverts")
                }
                
                val rawText = response.bodyAsText()
                println("📥 Fallback 1 Response: $rawText")
                
                val profileResponse = Json { ignoreUnknownKeys = true }.decodeFromString<StrapiProfile>(rawText)
                Result.success(profileResponse)
            } catch (fallback1Error: Exception) {
                println("❌ Fallback 1 failed: ${fallback1Error.message}")
                
                // Fallback 2: Try with simple population
                try {
                    println("🔄 Trying fallback with simple population...")
                    val response = client.get("$baseUrl/users/me") {
                        contentType(ContentType.Application.Json)
                        header("Authorization", "Bearer $token")
                        header("ngrok-skip-browser-warning", "true")
                        parameter("populate", "*")
                    }
                    
                    val rawText = response.bodyAsText()
                    println("📥 Fallback 2 Response: $rawText")
                    
                    val profileResponse = Json { ignoreUnknownKeys = true }.decodeFromString<StrapiProfile>(rawText)
                    Result.success(profileResponse)
                } catch (fallback2Error: Exception) {
                    println("❌ All fallbacks failed: ${fallback2Error.message}")
                    Result.failure(Exception("Failed to fetch profile after all attempts: ${fallback2Error.message}"))
                }
            }
        }
    }
    
    // Simplified method to get raw profile data as string for debugging
    suspend fun getProfileDataRaw(token: String): Result<String> {
        return try {
            val response = client.get("$baseUrl/users/me") {
                contentType(ContentType.Application.Json)
                header("Authorization", "Bearer $token")
                header("ngrok-skip-browser-warning", "true")
                parameter("populate", "deep")
            }
            
            val rawData = response.bodyAsText()
            println("🔍 Raw Profile Data: $rawData")
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