package com.example.boltnew.data.repository

import com.example.boltnew.data.model.auth.profile.Profile
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    fun getProfile(): Flow<Profile?>
    suspend fun getProfileById(id: Int): Profile?
    suspend fun insertProfile(profile: Profile)
    suspend fun updateProfile(profile: Profile)
    suspend fun updateAvatar(avatarUrl: String, avatarPath: String)
    suspend fun deleteProfile(profile: Profile)
    suspend fun initializeDefaultProfile()
}