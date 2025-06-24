package com.example.boltnew.data.repository

import com.example.boltnew.data.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getUserProfile(): Flow<User?>
    suspend fun getUserProfileSync(): User?
    suspend fun saveUserProfile(user: User)
    suspend fun updateUserProfile(user: User)
    suspend fun updateAvatarPath(avatarPath: String?)
    suspend fun initializeDefaultProfile()
}