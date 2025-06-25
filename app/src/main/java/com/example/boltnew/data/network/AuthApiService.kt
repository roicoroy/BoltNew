package com.example.boltnew.data.network

import com.example.boltnew.data.model.auth.login.LoginRequest
import com.example.boltnew.data.model.auth.login.LoginResponse
import com.example.boltnew.data.model.auth.register.RegisterRequest
import com.example.boltnew.data.model.auth.register.RegisterResponse
import com.example.boltnew.data.model.auth.profile.StrapiProfile
import com.example.boltnew.data.model.auth.user.StrapiUser
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

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
                parameter("populate", "*")
            }
            Result.success(response.body<StrapiUser>())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getUserProfile(token: String): Result<StrapiProfile> {
        return try {
            val response = client.get("$baseUrl/users/me") {
                contentType(ContentType.Application.Json)
                header("Authorization", "Bearer $token")
                header("ngrok-skip-browser-warning", "true")
                parameter("populate", "profile,profile.avatar,profile.addresses,profile.adverts")
            }
            Result.success(response.body<StrapiProfile>())
        } catch (e: Exception) {
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