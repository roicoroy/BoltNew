package com.example.boltnew.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.boltnew.data.mapper.toDomain
import com.example.boltnew.data.model.auth.profile.Address
import com.example.boltnew.data.network.AddressApiService
import com.example.boltnew.data.network.StrapiAddressCreateData
import com.example.boltnew.data.network.StrapiAddressCreateRequest
import com.example.boltnew.data.network.StrapiAddressUpdateData
import com.example.boltnew.data.network.StrapiAddressUpdateRequest
import com.example.boltnew.data.network.TokenManager

@RequiresApi(Build.VERSION_CODES.O)
class AddressRepositoryImpl(
    private val addressApiService: AddressApiService,
    private val tokenManager: TokenManager
) : AddressRepository {
    
    override suspend fun createAddress(address: Address, profileDocumentId: String): Result<Address> {
        return try {
            val token = tokenManager.getToken()
                ?: return Result.failure(Exception("No authentication token available"))
            
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
            
            // Step 2: Link address to profile
            val linkResult = addressApiService.updateProfileAddresses(
                profileDocumentId = profileDocumentId,
                addressIds = listOf(createdAddress.data.id.toString()),
                token = token
            )
            
            if (linkResult.isFailure) {
                println("⚠️ Address created but linking to profile failed")
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
            
            val deleteResult = addressApiService.deleteAddress(addressDocumentId, token)
            
            if (deleteResult.isFailure) {
                return Result.failure(deleteResult.exceptionOrNull() ?: Exception("Address deletion failed"))
            }
            
            println("✅ Address deleted successfully")
            Result.success(true)
            
        } catch (e: Exception) {
            println("💥 Address deletion process failed: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }
}