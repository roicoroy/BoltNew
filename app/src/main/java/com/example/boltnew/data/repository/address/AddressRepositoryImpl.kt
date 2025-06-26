package com.example.boltnew.data.repository.address

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.boltnew.data.model.StrapiAddressCreateData
import com.example.boltnew.data.model.StrapiAddressCreateRequest
import com.example.boltnew.data.model.StrapiAddressUpdateData
import com.example.boltnew.data.model.StrapiAddressUpdateRequest
import com.example.boltnew.data.model.auth.profile.Address
import com.example.boltnew.data.network.AddressApiService
import com.example.boltnew.data.network.AuthApiService
import com.example.boltnew.data.network.TokenManager

@RequiresApi(Build.VERSION_CODES.O)
class AddressRepositoryImpl(
    private val addressApiService: AddressApiService,
    private val authApiService: AuthApiService,
    private val tokenManager: TokenManager
) : AddressRepository {
    
    override suspend fun createAddress(address: Address, profileDocumentId: String): Result<Address> {
        return try {
            val token = tokenManager.getToken()
                ?: return Result.failure(Exception("No authentication token available"))
            val loggedUser = tokenManager.getUserId()
            println("🏠 Creating address for profile: $profileDocumentId")
            
            // Step 1: Create address
            val createRequest = StrapiAddressCreateRequest(
                data = StrapiAddressCreateData(
                    firstName = address.firstName,
                    lastName = address.lastName,
                    firstLineAddress = address.firstLineAddress,
                    secondLineAddress = address.secondLineAddress.takeIf { it.isNotBlank() },
                    postCode = address.postCode,
                    city = address.city,
                    country = address.country,
                    phoneNumber = address.phoneNumber?.takeIf { it.isNotBlank() }
                )
            )
            
            val createResult = addressApiService.createAddress(createRequest, token)
            
            if (createResult.isFailure) {
                return Result.failure(createResult.exceptionOrNull() ?: Exception("Address creation failed"))
            }
            
            val createdAddress = createResult.getOrThrow()
            println("✅ Address created with ID: ${createdAddress.data.id}")
            
            // Step 2: Query the Profile of the logged user to get existing addresses
            println("🔍 Querying current profile to get existing addresses...")
            val profileResult = authApiService.getUserProfile(token)
            
            if (profileResult.isFailure) {
                println("⚠️ Address created but failed to query profile for linking")
                return Result.failure(Exception("Address created but failed to link to profile: ${profileResult.exceptionOrNull()?.message}"))
            }
            
            val currentProfile = profileResult.getOrThrow()
            println("📋 Current profile has ${currentProfile.data.addresses.size} existing addresses")
            
            // Step 3: Get existing address IDs and add the new one
            val existingAddressIds = currentProfile.data.addresses.map { it.id.toString() }
            val updatedAddressIds = existingAddressIds + createdAddress.data.id.toString()
            
            println("🔗 Updating profile with address IDs: ${updatedAddressIds.joinToString()}")
            
            // Step 4: Link address to profile with the complete list
            val linkResult = addressApiService.updateProfileAddresses(
                profileDocumentId = profileDocumentId,
                addressIds = updatedAddressIds,
                token = token
            )
            
            if (linkResult.isFailure) {
                println("⚠️ Address created but linking to profile failed: ${linkResult.exceptionOrNull()?.message}")
                // Still return success since address was created
            } else {
                println("🔗 Address linked to profile successfully")
            }
            
            // Convert to domain model
            val domainAddress = Address(
                id = createdAddress.data.id,
                documentId = createdAddress.data.documentId,
                firstName = createdAddress.data.firstName,
                lastName = createdAddress.data.lastName,
                firstLineAddress = createdAddress.data.firstLineAddress,
                secondLineAddress = createdAddress.data.secondLineAddress ?: "",
                city = createdAddress.data.city,
                postCode = createdAddress.data.postCode,
                country = createdAddress.data.country,
                phoneNumber = createdAddress.data.phoneNumber,
                createdAt = createdAddress.data.createdAt,
                updatedAt = createdAddress.data.updatedAt,
                publishedAt = createdAddress.data.publishedAt
            )
            
            Result.success(domainAddress)
            
        } catch (e: Exception) {
            println("💥 Address creation process failed: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }
    
    override suspend fun updateAddress(address: Address, profileDocumentId: String): Result<Address> {
        return try {
            val token = tokenManager.getToken()
                ?: return Result.failure(Exception("No authentication token available"))
            
            println("🔄 Updating address: ${address.documentId}")
            
            val updateRequest = StrapiAddressUpdateRequest(
                data = StrapiAddressUpdateData(
                    firstName = address.firstName,
                    lastName = address.lastName,
                    firstLineAddress = address.firstLineAddress,
                    secondLineAddress = address.secondLineAddress.takeIf { it.isNotBlank() },
                    postCode = address.postCode,
                    city = address.city,
                    country = address.country,
                    phoneNumber = address.phoneNumber?.takeIf { it.isNotBlank() }
                )
            )
            
            val updateResult = addressApiService.updateAddress(address.documentId, updateRequest, token)
            
            if (updateResult.isFailure) {
                return Result.failure(updateResult.exceptionOrNull() ?: Exception("Address update failed"))
            }
            
            val updatedAddress = updateResult.getOrThrow()
            println("✅ Address updated successfully")
            
            // Convert to domain model
            val domainAddress = Address(
                id = updatedAddress.data.id,
                documentId = updatedAddress.data.documentId,
                firstName = updatedAddress.data.firstName,
                lastName = updatedAddress.data.lastName,
                firstLineAddress = updatedAddress.data.firstLineAddress,
                secondLineAddress = updatedAddress.data.secondLineAddress ?: "",
                city = updatedAddress.data.city,
                postCode = updatedAddress.data.postCode,
                country = updatedAddress.data.country,
                phoneNumber = updatedAddress.data.phoneNumber,
                createdAt = updatedAddress.data.createdAt,
                updatedAt = updatedAddress.data.updatedAt,
                publishedAt = updatedAddress.data.publishedAt
            )
            
            Result.success(domainAddress)
            
        } catch (e: Exception) {
            println("💥 Address update process failed: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }
    
    override suspend fun deleteAddress(addressDocumentId: String, profileDocumentId: String): Result<Boolean> {
        return try {
            val token = tokenManager.getToken()
                ?: return Result.failure(Exception("No authentication token available"))
            
            println("🗑️ Deleting address: $addressDocumentId")
            
            // Step 1: Query the Profile to get current addresses
            println("🔍 Querying current profile to get existing addresses...")
            val profileResult = authApiService.getUserProfile(token)
            
            if (profileResult.isFailure) {
                println("⚠️ Failed to query profile for address deletion")
                return Result.failure(Exception("Failed to query profile: ${profileResult.exceptionOrNull()?.message}"))
            }
            
            val currentProfile = profileResult.getOrThrow()
            println("📋 Current profile has ${currentProfile.data.addresses.size} existing addresses")
            
            // Step 2: Find the address to delete and get its ID
            val addressToDelete = currentProfile.data.addresses.find { it.documentId == addressDocumentId }
            if (addressToDelete == null) {
                println("⚠️ Address not found in profile")
                return Result.failure(Exception("Address not found in profile"))
            }
            
            // Step 3: Delete the address
            val deleteResult = addressApiService.deleteAddress(addressDocumentId, token)
            
            if (deleteResult.isFailure) {
                return Result.failure(deleteResult.exceptionOrNull() ?: Exception("Address deletion failed"))
            }
            
            println("✅ Address deleted from Strapi successfully")
            
            // Step 4: Update profile to remove the deleted address from the list
            val remainingAddressIds = currentProfile.data.addresses
                .filter { it.documentId != addressDocumentId }
                .map { it.id.toString() }
            
            println("🔗 Updating profile with remaining address IDs: ${remainingAddressIds.joinToString()}")
            
            val updateProfileResult = addressApiService.updateProfileAddresses(
                profileDocumentId = profileDocumentId,
                addressIds = remainingAddressIds,
                token = token
            )
            
            if (updateProfileResult.isFailure) {
                println("⚠️ Address deleted but failed to update profile: ${updateProfileResult.exceptionOrNull()?.message}")
                // Still return success since address was deleted
            } else {
                println("🔗 Profile updated successfully after address deletion")
            }
            
            Result.success(true)
            
        } catch (e: Exception) {
            println("💥 Address deletion process failed: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }
}