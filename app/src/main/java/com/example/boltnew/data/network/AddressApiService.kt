package com.example.boltnew.data.network

import com.example.boltnew.data.model.ProfileAddressUpdateData
import com.example.boltnew.data.model.ProfileAddressUpdateRequest
import com.example.boltnew.data.model.StrapiAddressCreateRequest
import com.example.boltnew.data.model.StrapiAddressSingleResponse
import com.example.boltnew.data.model.StrapiAddressUpdateRequest
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess

class AddressApiService {

    /**
     * Create a new address
     */
    suspend fun createAddress(
        request: StrapiAddressCreateRequest,
        token: String
    ): Result<StrapiAddressSingleResponse> {
        return try {
            println("🏠 Creating new address...")
            
            val response = client.post("$baseUrl/addresses") {
                header("Authorization", "Bearer $token")
                header("ngrok-skip-browser-warning", "true")
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            
            println("📤 Create address response status: ${response.status}")
            
            if (response.status.isSuccess()) {
                val addressResponse = response.body<StrapiAddressSingleResponse>()
                println("✅ Address created successfully: ${addressResponse.data.id}")
                Result.success(addressResponse)
            } else {
                val errorBody = response.bodyAsText()
                println("❌ Address creation failed: $errorBody")
                Result.failure(Exception("Address creation failed: ${response.status} - $errorBody"))
            }
            
        } catch (e: Exception) {
            println("💥 Address creation error: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }
    
    /**
     * Update an existing address
     */
    suspend fun updateAddress(
        addressId: String,
        request: StrapiAddressUpdateRequest,
        token: String
    ): Result<StrapiAddressSingleResponse> {
        return try {
            println("🔄 Updating address $addressId...")
            
            val response = client.put("$baseUrl/addresses/$addressId") {
                header("Authorization", "Bearer $token")
                header("ngrok-skip-browser-warning", "true")
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            
            println("📤 Update address response status: ${response.status}")
            
            if (response.status.isSuccess()) {
                val addressResponse = response.body<StrapiAddressSingleResponse>()
                println("✅ Address updated successfully: ${addressResponse.data.id}")
                Result.success(addressResponse)
            } else {
                val errorBody = response.bodyAsText()
                println("❌ Address update failed: $errorBody")
                Result.failure(Exception("Address update failed: ${response.status} - $errorBody"))
            }
            
        } catch (e: Exception) {
            println("💥 Address update error: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }
    
    /**
     * Delete an address
     */
    suspend fun deleteAddress(
        addressId: String,
        token: String
    ): Result<Boolean> {
        return try {
            println("🗑️ Deleting address $addressId...")
            
            val response = client.delete("$baseUrl/addresses/$addressId") {
                header("Authorization", "Bearer $token")
                header("ngrok-skip-browser-warning", "true")
            }
            
            println("📤 Delete address response status: ${response.status}")
            
            if (response.status.isSuccess()) {
                println("✅ Address deleted successfully")
                Result.success(true)
            } else {
                val errorBody = response.bodyAsText()
                println("❌ Address deletion failed: $errorBody")
                Result.failure(Exception("Address deletion failed: ${response.status} - $errorBody"))
            }
            
        } catch (e: Exception) {
            println("💥 Address deletion error: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }
    
    /**
     * Link addresses to profile
     */
    suspend fun updateProfileAddresses(
        profileDocumentId: String,
        addressIds: List<String>,
        token: String
    ): Result<Boolean> {
        return try {
            println("🔗 Linking addresses ${addressIds.joinToString()} to profile $profileDocumentId...")
            
            val requestBody = ProfileAddressUpdateRequest(
                data = ProfileAddressUpdateData(
                    addresses = addressIds
                )
            )
            
            val response = client.put("$baseUrl/profiles/$profileDocumentId") {
                header("Authorization", "Bearer $token")
                header("ngrok-skip-browser-warning", "true")
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }
            
            println("🔄 Profile addresses update response status: ${response.status}")
            
            if (response.status.isSuccess()) {
                println("✅ Profile addresses updated successfully")
                Result.success(true)
            } else {
                val errorBody = response.bodyAsText()
                println("❌ Profile addresses update failed: $errorBody")
                Result.failure(Exception("Profile addresses update failed: ${response.status} - $errorBody"))
            }
            
        } catch (e: Exception) {
            println("💥 Profile addresses update error: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }
}
