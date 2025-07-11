package com.example.boltnew.data.repository.auth

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.boltnew.data.mapper.toDomain
import com.example.boltnew.data.model.auth.login.LoginRequest
import com.example.boltnew.data.model.auth.login.LoginResponse
import com.example.boltnew.data.model.auth.register.RegisterRequest
import com.example.boltnew.data.model.auth.register.RegisterResponse
import com.example.boltnew.data.model.auth.profile.Profile
import com.example.boltnew.data.model.auth.user.User
import com.example.boltnew.data.network.AuthApiService
import com.example.boltnew.data.network.TokenManager
import com.example.boltnew.utils.RequestState
import kotlinx.coroutines.flow.Flow

@RequiresApi(Build.VERSION_CODES.O)
class AuthRepositoryImpl(
    private val authApiService: AuthApiService,
    private val tokenManager: TokenManager
) : AuthRepository {
    
    override suspend fun login(email: String, password: String): Result<LoginResponse> {
        return try {
            val request = LoginRequest(identifier = email, password = password)
            val result = authApiService.login(request)
            
            if (result.isSuccess) {
                val response = result.getOrThrow()
                // Save token and user info
                tokenManager.saveToken(response.jwt)
                tokenManager.saveUserInfo(
                    userId = response.user.id,
                    username = response.user.username,
                    email = response.user.email
                )
            }
            
            result
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun register(username: String, email: String, password: String): Result<RegisterResponse> {
        return try {
            val request = RegisterRequest(username = username, email = email, password = password)
            val result = authApiService.register(request)
            
            if (result.isSuccess) {
                val response = result.getOrThrow()
                // Save token and user info
                tokenManager.saveToken(response.jwt)
                tokenManager.saveUserInfo(
                    userId = response.user.id,
                    username = response.user.username,
                    email = response.user.email
                )
            }
            
            result
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun logout(): Result<Boolean> {
        return try {
            val token = tokenManager.getToken()
            if (token != null) {
                // Try to logout from server
                val result = authApiService.logout(token)
                // Clear local token regardless of server response
                tokenManager.clearToken()
                Result.success(true)
            } else {
                Result.success(true)
            }
        } catch (e: Exception) {
            // Clear local token even if server logout fails
            tokenManager.clearToken()
            Result.success(true)
        }
    }
    
    override suspend fun getCurrentUser(): Result<User> {
        return try {
            val token = tokenManager.getToken()
            if (token != null) {
                val result = authApiService.getCurrentUser(token)
                if (result.isSuccess) {
                    val strapiUser = result.getOrThrow()
                    Result.success(strapiUser.toDomain())
                } else {
                    result.map { it.toDomain() }
                }
            } else {
                Result.failure(Exception("No authentication token available"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getUserProfile(): RequestState<Profile> {
        return try {
            val token = tokenManager.getToken()
            if (token != null) {
                println("🚀 Getting user profile with token: ${token.take(20)}...")
                
                // First, get raw data to understand the structure
                val rawDataResult = authApiService.getProfileDataRaw(token)
                if (rawDataResult.isSuccess()) {
                    val rawData = rawDataResult.getSuccessData()
                    println("📋 Raw profile data: $rawData")
                    
                    // Check if the response indicates the user doesn't have a profile
                    if (rawData?.contains("\"profile\":null") == true || 
                        rawData?.contains("\"profile\":{}") == true ||
                        rawData?.isBlank() == true) {
                        println("⚠️ User has no profile data in Strapi")
                        return RequestState.Error(("User profile not found. Please ensure your Strapi user has an associated profile."))
                    }
                }
                
                // Now try to get structured profile data
                val result = authApiService.getUserProfile(token)
                if (result.isSuccess) {
                    val strapiProfile = result.getOrThrow()
                    println("✅ Strapi profile received: $strapiProfile")
                    
                    // Check if the profile data is actually populated
                    if (strapiProfile.data.id == 0 && strapiProfile.data.user.id == 0) {
                        println("⚠️ Profile data appears to be empty")
                        return RequestState.Error(("Profile data is empty. Please check your Strapi configuration and ensure the user has a profile."))
                    }
                    
                    val domainProfile = strapiProfile.toDomain()
                    println("🎯 Domain profile mapped: $domainProfile")

                    RequestState.Success(domainProfile)
                } else {
                    val error = result.exceptionOrNull()
                    println("❌ Profile API error: ${error?.message}")
                    RequestState.Error(("Unknown profile API error"))
                }
            } else {
                println("❌ No authentication token available")
                RequestState.Error(("No authentication token available"))
            }
        } catch (e: Exception) {
            println("💥 Profile repository error: ${e.message}")
            e.printStackTrace()
            RequestState.Error(e.toString())
        }
    }
    
    override suspend fun forgotPassword(email: String): Result<Boolean> {
        return try {
            authApiService.forgotPassword(email)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun resetPassword(
        code: String,
        password: String,
        passwordConfirmation: String
    ): Result<LoginResponse> {
        return try {
            val result = authApiService.resetPassword(code, password, passwordConfirmation)
            
            if (result.isSuccess) {
                val response = result.getOrThrow()
                // Save new token and user info
                tokenManager.saveToken(response.jwt)
                tokenManager.saveUserInfo(
                    userId = response.user.id,
                    username = response.user.username,
                    email = response.user.email
                )
            }
            
            result
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun changePassword(
        currentPassword: String,
        newPassword: String,
        passwordConfirmation: String
    ): Result<LoginResponse> {
        return try {
            val token = tokenManager.getToken()
            if (token != null) {
                val result = authApiService.changePassword(
                    token = token,
                    currentPassword = currentPassword,
                    password = newPassword,
                    passwordConfirmation = passwordConfirmation
                )
                
                if (result.isSuccess) {
                    val response = result.getOrThrow()
                    // Update token
                    tokenManager.saveToken(response.jwt)
                }
                
                result
            } else {
                Result.failure(Exception("No authentication token available"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override fun isLoggedIn(): Boolean {
        return tokenManager.isLoggedIn()
    }
    
    override fun getAuthState(): Flow<Boolean> {
        return tokenManager.authState
    }
    
    override fun getCurrentToken(): String? {
        return tokenManager.getToken()
    }
    
    override suspend fun refreshToken(): Result<Boolean> {
        return try {
            val token = tokenManager.getToken()
            if (token != null && !tokenManager.isTokenExpired()) {
                tokenManager.refreshTokenExpiry()
                Result.success(true)
            } else {
                tokenManager.clearToken()
                Result.failure(Exception("Token expired or invalid"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}