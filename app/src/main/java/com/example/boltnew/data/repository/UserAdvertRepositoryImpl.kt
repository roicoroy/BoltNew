package com.example.boltnew.data.repository

import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.boltnew.data.model.advert.StrapiAdvertCreateData
import com.example.boltnew.data.model.advert.StrapiAdvertCreateRequest
import com.example.boltnew.data.model.advert.StrapiAdvertUpdateData
import com.example.boltnew.data.model.advert.StrapiAdvertUpdateRequest
import com.example.boltnew.data.model.auth.profile.UserAdvert
import com.example.boltnew.data.network.AdvertApiService
import com.example.boltnew.data.network.AuthApiService
import com.example.boltnew.data.network.StrapiCategoryOption
import com.example.boltnew.data.network.TokenManager
import com.example.boltnew.data.network.UploadApiService

@RequiresApi(Build.VERSION_CODES.O)
class UserAdvertRepositoryImpl(
    private val advertApiService: AdvertApiService,
    private val uploadApiService: UploadApiService,
    private val authApiService: AuthApiService,
    private val tokenManager: TokenManager
) : UserAdvertRepository {
    
    override suspend fun createAdvert(
        context: Context,
        title: String,
        description: String,
        slug: String,
        categoryId: Int,
        coverImageUri: Uri?,
        profileDocumentId: String
    ): Result<UserAdvert> {
        return try {
            val token = tokenManager.getToken()
                ?: return Result.failure(Exception("No authentication token available"))
            
            println("📝 Creating advert for profile: $profileDocumentId")
            
            var coverIdString: String? = null
            
            // Step 1: Upload cover image if provided
            if (coverImageUri != null) {
                println("📸 Uploading cover image...")
                val uploadResult = uploadApiService.uploadImage(context, coverImageUri, token)
                
                if (uploadResult.isFailure) {
                    return Result.failure(uploadResult.exceptionOrNull() ?: Exception("Image upload failed"))
                }
                
                val uploadResponse = uploadResult.getOrThrow()
                val uploadedFile = uploadResponse.files.firstOrNull()
                    ?: return Result.failure(Exception("No file in upload response"))
                
                coverIdString = uploadedFile.id.toString()
                println("📁 Cover image uploaded successfully with ID: $coverIdString")
            }
            
            // Step 2: Create advert with category as array
            val createRequest = StrapiAdvertCreateRequest(
                data = StrapiAdvertCreateData(
                    title = title,
                    description = description,
                    slug = slug,
                    category = listOf(categoryId.toString()), // Convert to array of strings
                    cover = coverIdString
                )
            )
            
            println("📤 Creating advert with request: $createRequest")
            
            val createResult = advertApiService.createAdvert(createRequest, token)
            
            if (createResult.isFailure) {
                return Result.failure(createResult.exceptionOrNull() ?: Exception("Advert creation failed"))
            }
            
            val createdAdvert = createResult.getOrThrow()
            println("✅ Advert created with ID: ${createdAdvert.data.id}")
            
            // Step 3: Query the Profile to get existing adverts
            println("🔍 Querying current profile to get existing adverts...")
            val profileResult = authApiService.getUserProfile(token)
            
            if (profileResult.isFailure) {
                println("⚠️ Advert created but failed to query profile for linking")
                return Result.failure(Exception("Advert created but failed to link to profile: ${profileResult.exceptionOrNull()?.message}"))
            }
            
            val currentProfile = profileResult.getOrThrow()
            println("📋 Current profile has ${currentProfile.data.adverts.size} existing adverts")
            
            // Step 4: Get existing advert IDs and add the new one
            val existingAdvertIds = currentProfile.data.adverts.map { it.id.toString() }
            val updatedAdvertIds = existingAdvertIds + createdAdvert.data.id.toString()
            
            println("🔗 Updating profile with advert IDs: ${updatedAdvertIds.joinToString()}")
            
            // Step 5: Link advert to profile with the complete list
            val linkResult = advertApiService.updateProfileAdverts(
                profileDocumentId = profileDocumentId,
                advertIds = updatedAdvertIds,
                token = token
            )
            
            if (linkResult.isFailure) {
                println("⚠️ Advert created but linking to profile failed: ${linkResult.exceptionOrNull()?.message}")
                // Still return success since advert was created
            } else {
                println("🔗 Advert linked to profile successfully")
            }
            
            // Convert simplified response to domain model
            val domainAdvert = UserAdvert(
                id = createdAdvert.data.id,
                documentId = createdAdvert.data.documentId,
                title = createdAdvert.data.title,
                description = createdAdvert.data.description,
                slug = createdAdvert.data.slug,
                createdAt = createdAdvert.data.createdAt,
                updatedAt = createdAdvert.data.updatedAt,
                publishedAt = createdAdvert.data.publishedAt
            )
            
            Result.success(domainAdvert)
            
        } catch (e: Exception) {
            println("💥 Advert creation process failed: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }
    
    override suspend fun updateAdvert(
        context: Context,
        advert: UserAdvert,
        title: String,
        description: String,
        slug: String,
        categoryId: Int,
        coverImageUri: Uri?,
        profileDocumentId: String
    ): Result<UserAdvert> {
        return try {
            val token = tokenManager.getToken()
                ?: return Result.failure(Exception("No authentication token available"))
            
            println("🔄 Updating advert: ${advert.documentId}")
            
            var coverIdString: String? = null
            
            // Step 1: Upload new cover image if provided
            if (coverImageUri != null) {
                println("📸 Uploading new cover image...")
                val uploadResult = uploadApiService.uploadImage(context, coverImageUri, token)
                
                if (uploadResult.isFailure) {
                    return Result.failure(uploadResult.exceptionOrNull() ?: Exception("Image upload failed"))
                }
                
                val uploadResponse = uploadResult.getOrThrow()
                val uploadedFile = uploadResponse.files.firstOrNull()
                    ?: return Result.failure(Exception("No file in upload response"))
                
                coverIdString = uploadedFile.id.toString()
                println("📁 New cover image uploaded successfully with ID: $coverIdString")
            }
            
            // Step 2: Update advert with category as array
            val updateRequest = StrapiAdvertUpdateRequest(
                data = StrapiAdvertUpdateData(
                    title = title,
                    description = description,
                    slug = slug,
                    category = listOf(categoryId.toString()), // Convert to array of strings
                    cover = coverIdString
                )
            )
            
            println("📤 Updating advert with request: $updateRequest")
            
            val updateResult = advertApiService.updateAdvert(advert.id, updateRequest, token)
            
            if (updateResult.isFailure) {
                return Result.failure(updateResult.exceptionOrNull() ?: Exception("Advert update failed"))
            }
            
            val updatedAdvert = updateResult.getOrThrow()
            println("✅ Advert updated successfully")
            
            // Convert simplified response to domain model
            val domainAdvert = UserAdvert(
                id = updatedAdvert.data.id,
                documentId = updatedAdvert.data.documentId,
                title = updatedAdvert.data.title,
                description = updatedAdvert.data.description,
                slug = updatedAdvert.data.slug,
                createdAt = updatedAdvert.data.createdAt,
                updatedAt = updatedAdvert.data.updatedAt,
                publishedAt = updatedAdvert.data.publishedAt
            )
            
            Result.success(domainAdvert)
            
        } catch (e: Exception) {
            println("💥 Advert update process failed: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }
    
    override suspend fun deleteAdvert(
        advertDocumentId: String,
        profileDocumentId: String
    ): Result<Boolean> {
        return try {
            val token = tokenManager.getToken()
                ?: return Result.failure(Exception("No authentication token available"))
            
            println("🗑️ Deleting advert: $advertDocumentId")
            
            // Step 1: Query the Profile to get current adverts
            println("🔍 Querying current profile to get existing adverts...")
            val profileResult = authApiService.getUserProfile(token)
            
            if (profileResult.isFailure) {
                println("⚠️ Failed to query profile for advert deletion")
                return Result.failure(Exception("Failed to query profile: ${profileResult.exceptionOrNull()?.message}"))
            }
            
            val currentProfile = profileResult.getOrThrow()
            println("📋 Current profile has ${currentProfile.data.adverts.size} existing adverts")
            
            // Step 2: Find the advert to delete and get its ID
            val advertToDelete = currentProfile.data.adverts.find { it.documentId == advertDocumentId }
            if (advertToDelete == null) {
                println("⚠️ Advert not found in profile")
                return Result.failure(Exception("Advert not found in profile"))
            }
            
            // Step 3: Delete the advert
            val deleteResult = advertApiService.deleteAdvert(advertToDelete.id, token)
            
            if (deleteResult.isFailure) {
                return Result.failure(deleteResult.exceptionOrNull() ?: Exception("Advert deletion failed"))
            }
            
            println("✅ Advert deleted from Strapi successfully")
            
            // Step 4: Update profile to remove the deleted advert from the list
            val remainingAdvertIds = currentProfile.data.adverts
                .filter { it.documentId != advertDocumentId }
                .map { it.id.toString() }
            
            println("🔗 Updating profile with remaining advert IDs: ${remainingAdvertIds.joinToString()}")
            
            val updateProfileResult = advertApiService.updateProfileAdverts(
                profileDocumentId = profileDocumentId,
                advertIds = remainingAdvertIds,
                token = token
            )
            
            if (updateProfileResult.isFailure) {
                println("⚠️ Advert deleted but failed to update profile: ${updateProfileResult.exceptionOrNull()?.message}")
                // Still return success since advert was deleted
            } else {
                println("🔗 Profile updated successfully after advert deletion")
            }
            
            Result.success(true)
            
        } catch (e: Exception) {
            println("💥 Advert deletion process failed: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }
    
    override suspend fun getCategories(): Result<List<StrapiCategoryOption>> {
        return try {
            advertApiService.getCategoriesWithDetails()
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}