package com.example.boltnew.data.network

import com.example.boltnew.data.model.auth.login.LoginRequest
import com.example.boltnew.data.model.auth.login.LoginResponse
import com.example.boltnew.data.model.auth.register.RegisterRequest
import com.example.boltnew.data.model.auth.register.RegisterResponse
import com.example.boltnew.data.model.auth.profile.StrapiProfile
import com.example.boltnew.data.model.auth.user.StrapiUser
import com.example.boltnew.utils.RequestState
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class AuthApiService {

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

            // First, get the current user to find their profile ID
            val userResponse = client.get("$baseUrl/users/me") {
                contentType(ContentType.Application.Json)
                header("Authorization", "Bearer $token")
                parameter("populate", "profile")
            }
            val userResponseText = userResponse.bodyAsText()
            println("📥 User Response: $userResponseText")
            // Parse user response to get profile document ID
            val userJson = json.parseToJsonElement(userResponseText)
            val profileDocumentId = try {
                userJson.jsonObject["profile"]?.jsonObject?.get("documentId")?.jsonPrimitive?.content
            } catch (e: Exception) {
                null
            }
            if (profileDocumentId == null) {
                return Result.failure(Exception("User has no associated profile. Please create a profile in Strapi first."))
            }
            println("🎯 Found profile document ID: $profileDocumentId")
            // Now fetch the complete profile using the specific endpoint
            val profileResponse = client.get("$baseUrl/profiles/$profileDocumentId") {
                contentType(ContentType.Application.Json)
                header("Authorization", "Bearer $token")
                parameter("populate", "*")
            }
            
            // Get raw response text first
            val rawResponse = profileResponse.bodyAsText()
            println("📥 Raw Profile API Response: $rawResponse")
            
            // Check if response is empty
            if (rawResponse.isBlank()) {
                return Result.failure(Exception("Empty response from profile API"))
            }
            
            // Try to parse the JSON structure manually first
            try {
                val jsonElement = json.parseToJsonElement(rawResponse)
                println("📊 Parsed Profile JSON Structure: $jsonElement")
            } catch (e: Exception) {
                println("❌ Failed to parse profile JSON: ${e.message}")
                return Result.failure(Exception("Invalid JSON response: ${e.message}"))
            }
            
            // Now try to deserialize to StrapiProfile
            try {
                val profileResponse = json.decodeFromString<StrapiProfile>(rawResponse)
                println("✅ Successfully parsed StrapiProfile: $profileResponse")
                
                // Validate the profile data
                if (profileResponse.data.id == 0) {
                    println("⚠️ Profile data appears to be empty or default")
                    return Result.failure(Exception("Profile data is empty"))
                }
                
                if (profileResponse.data.user.id == 0) {
                    println("⚠️ Profile user data appears to be empty")
                    return Result.failure(Exception("Profile user data is empty"))
                }
                
                println("🎉 Profile successfully loaded: ID=${profileResponse.data.id}, User=${profileResponse.data.user.username}")
                Result.success(profileResponse)
            } catch (e: Exception) {
                println("❌ Failed to deserialize to StrapiProfile: ${e.message}")
                e.printStackTrace()
                Result.failure(Exception("Failed to parse profile response: ${e.message}"))
            }
            
        } catch (e: Exception) {
            println("💥 Profile API Error: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }
    
    // Method to get raw profile data for debugging
    suspend fun getProfileDataRaw(token: String): RequestState<String> {
        return try {
            val response = client.get("$baseUrl/users/me") {
                contentType(ContentType.Application.Json)
                header("Authorization", "Bearer $token")
                header("ngrok-skip-browser-warning", "true")
                parameter("populate", "*")
            }
            
            val rawData = response.bodyAsText()
            println("📋 Raw Profile Data: $rawData")
            RequestState.Success(rawData)
        } catch (e: Exception) {
            println("❌ Raw Profile Data Error: ${e.message}")
            RequestState.Error(e.toString())
        }
    }
    
    suspend fun refreshToken(token: String): Result<LoginResponse> {
        return try {
            val response = client.post("$baseUrl/auth/refresh") {
                contentType(ContentType.Application.Json)
                header("Authorization", "Bearer $token")
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
            val response = client.get("$baseUrl/auth/local")
            Result.success(response.status == HttpStatusCode.MethodNotAllowed || response.status.isSuccess())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}