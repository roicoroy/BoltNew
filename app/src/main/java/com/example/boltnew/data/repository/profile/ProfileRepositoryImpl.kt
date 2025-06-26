package com.example.boltnew.data.repository.profile

import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.boltnew.data.database.ProfileDao
import com.example.boltnew.data.mapper.toDomain
import com.example.boltnew.data.mapper.toEntity
import com.example.boltnew.data.model.auth.profile.Address
import com.example.boltnew.data.model.auth.profile.Profile
import com.example.boltnew.data.model.auth.profile.ProfileUser
import com.example.boltnew.data.network.ProfileApiService
import com.example.boltnew.data.network.UploadApiService
import com.example.boltnew.data.network.TokenManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime

@RequiresApi(Build.VERSION_CODES.O)
class ProfileRepositoryImpl(
    private val profileDao: ProfileDao,
    private val uploadApiService: UploadApiService,
    private val tokenManager: TokenManager,
    private val profileApiService: ProfileApiService
) : ProfileRepository {
    
    override fun getProfile(): Flow<Profile?> {
        return profileDao.getProfile().map { profileEntity ->
            profileEntity?.let { entity ->
                val addresses = profileDao.getAddressesByProfileId(entity.id)
                val userAdverts = profileDao.getUserAdvertsByProfileId(entity.id)
                entity.toDomain(addresses, userAdverts)
            }
        }
    }
    
    override suspend fun getProfileById(id: Int): Profile? {
        val profileEntity = profileDao.getProfileById(id) ?: return null
        val addresses = profileDao.getAddressesByProfileId(profileEntity.id)
        val userAdverts = profileDao.getUserAdvertsByProfileId(profileEntity.id)
        return profileEntity.toDomain(addresses, userAdverts)
    }
    
    override suspend fun insertProfile(profile: Profile) {
        val profileEntity = profile.toEntity()
        profileDao.insertProfile(profileEntity)
        
        if (profile.addresses.isNotEmpty()) {
            val addressEntities = profile.addresses.map { it.toEntity(profile.id) }
            profileDao.insertAddresses(addressEntities)
        }
        
        if (profile.userAdverts.isNotEmpty()) {
            val userAdvertEntities = profile.userAdverts.map { it.toEntity(profile.id) }
            profileDao.insertUserAdverts(userAdvertEntities)
        }
    }
    
    override suspend fun updateProfile(profile: Profile) {
        val profileEntity = profile.toEntity()
        profileDao.updateProfile(profileEntity)
        
        if (profile.addresses.isNotEmpty()) {
            val addressEntities = profile.addresses.map { it.toEntity(profile.id) }
            profileDao.insertAddresses(addressEntities)
        }
        
        if (profile.userAdverts.isNotEmpty()) {
            val userAdvertEntities = profile.userAdverts.map { it.toEntity(profile.id) }
            profileDao.insertUserAdverts(userAdvertEntities)
        }
    }
    
    override suspend fun updateProfileDob(profileDocumentId: String, dateOfBirth: String): Result<Profile> {
        return try {
            val token = tokenManager.getToken()
                ?: return Result.failure(Exception("No authentication token available"))
            
            println("🔄 Updating profile DOB: $profileDocumentId with date: $dateOfBirth")
            
            // Step 1: Update profile DOB via API
            val updateResult = profileApiService.updateProfileDob(
                profileDocumentId = profileDocumentId,
                dateOfBirth = dateOfBirth,
                token = token
            )
            
            if (updateResult.isFailure) {
                return Result.failure(updateResult.exceptionOrNull() ?: Exception("Profile DOB update failed"))
            }
            
            val updatedProfileResponse = updateResult.getOrThrow()
            println("✅ Profile DOB updated successfully via API")
            
            // Step 2: Update local database
            // Get current profile from local database
            val currentProfile = profileDao.getProfileById(1) // Assuming single user profile with ID 1
            if (currentProfile != null) {
                val updatedEntity = currentProfile.copy(
                    dateOfBirth = dateOfBirth,
                    updatedAt = updatedProfileResponse.data.updatedAt
                )
                profileDao.updateProfile(updatedEntity)
                println("✅ Local profile DOB updated successfully")
                
                // Return updated domain model
                val addresses = profileDao.getAddressesByProfileId(updatedEntity.id)
                val userAdverts = profileDao.getUserAdvertsByProfileId(updatedEntity.id)
                val domainProfile = updatedEntity.toDomain(addresses, userAdverts)
                
                Result.success(domainProfile)
            } else {
                Result.failure(Exception("Local profile not found"))
            }
            
        } catch (e: Exception) {
            println("💥 Profile DOB update process failed: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }
    
    override suspend fun updateAvatar(avatarUrl: String, avatarPath: String) {
        profileDao.updateAvatar(avatarUrl)
    }
    
    /**
     * Upload avatar image to Strapi and link it to the profile
     */
    suspend fun uploadAndUpdateAvatar(
        context: Context,
        imageUri: Uri,
        profileDocumentId: String
    ): Result<String> {
        return try {
            val token = tokenManager.getToken()
                ?: return Result.failure(Exception("No authentication token available"))
            
            println("🚀 Starting avatar upload process...")
            
            // Step 1: Upload image to Strapi
            val uploadResult = uploadApiService.uploadImage(context, imageUri, token)
            
            if (uploadResult.isFailure) {
                return Result.failure(uploadResult.exceptionOrNull() ?: Exception("Upload failed"))
            }
            
            val uploadResponse = uploadResult.getOrThrow()
            val uploadedFile = uploadResponse.files.firstOrNull()
                ?: return Result.failure(Exception("No file in upload response"))
            
            println("📁 Image uploaded successfully with ID: ${uploadedFile.id}")
            
            // Step 2: Link uploaded image to profile
            val linkResult = uploadApiService.updateProfileAvatar(
                profileDocumentId = profileDocumentId,
                imageId = uploadedFile.id,
                token = token
            )
            
            if (linkResult.isFailure) {
                return Result.failure(linkResult.exceptionOrNull() ?: Exception("Profile update failed"))
            }
            
            println("🔗 Avatar linked to profile successfully")
            
            // Step 3: Update local database with new avatar URL
            profileDao.updateAvatar(uploadedFile.url)
            
            Result.success(uploadedFile.url)
            
        } catch (e: Exception) {
            println("💥 Avatar upload process failed: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }
    
    override suspend fun deleteProfile(profile: Profile) {
        val profileEntity = profile.toEntity()
        profileDao.deleteProfile(profileEntity)
    }
    
    override suspend fun initializeDefaultProfile() {
        val count = profileDao.getProfileCount()
        if (count == 0) {
            val defaultProfile = getDefaultProfile()
            insertProfile(defaultProfile)
        }
    }
    
    private fun getDefaultProfile(): Profile {
        val now = LocalDateTime.now().toString()
        
        return Profile(
            id = 1,
            documentId = "default-profile",
            dateOfBirth = "1990-01-01",
            createdAt = now,
            updatedAt = now,
            publishedAt = now,
            user = ProfileUser(
                id = 1,
                documentId = "default-user",
                username = "john_doe",
                email = "john.doe@example.com",
                blocked = false,
                confirmed = true,
                provider = "local",
                createdAt = now,
                updatedAt = now,
                publishedAt = now
            ),
            addresses = listOf(
                Address(
                    id = 1,
                    documentId = "default-address",
                    firstName = "John",
                    lastName = "Doe",
                    firstLineAddress = "123 Main Street",
                    secondLineAddress = "Apt 4B",
                    city = "New York",
                    postCode = "10001",
                    country = "United States",
                    phoneNumber = "+1 (555) 123-4567",
                    createdAt = now,
                    updatedAt = now,
                    publishedAt = now
                )
            ),
            avatar = null,
            userAdverts = emptyList()
        )
    }
}