package com.example.boltnew.data.repository

import com.example.boltnew.data.model.auth.login.LoginRequest
import com.example.boltnew.data.model.auth.login.LoginResponse
import com.example.boltnew.data.model.auth.register.RegisterRequest
import com.example.boltnew.data.model.auth.register.RegisterResponse
import com.example.boltnew.data.model.auth.profile.Profile
import com.example.boltnew.data.model.auth.user.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<LoginResponse>
    suspend fun register(username: String, email: String, password: String): Result<RegisterResponse>
    suspend fun logout(): Result<Boolean>
    suspend fun getCurrentUser(): Result<User>
    suspend fun getUserProfile(): Result<Profile>
    suspend fun forgotPassword(email: String): Result<Boolean>
    suspend fun resetPassword(code: String, password: String, passwordConfirmation: String): Result<LoginResponse>
    suspend fun changePassword(currentPassword: String, newPassword: String, passwordConfirmation: String): Result<LoginResponse>
    fun isLoggedIn(): Boolean
    fun getAuthState(): Flow<Boolean>
    fun getCurrentToken(): String?
    suspend fun refreshToken(): Result<Boolean>
}