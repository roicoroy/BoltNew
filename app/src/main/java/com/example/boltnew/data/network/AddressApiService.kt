package com.example.boltnew.data.network

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import io.ktor.http.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class AddressApiService {
    
    private val client = HttpClient.client
    private val baseUrl = "https://8c0c-86-156-238-78.ngrok-free.app/api"
    
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

// Data classes for address operations
@Serializable
data class StrapiAddressSingleResponse(
    @SerialName("data")
    val data: StrapiAddressData,
    @SerialName("meta")
    val meta: StrapiMeta = StrapiMeta()
)

@Serializable
data class StrapiAddressData(
    @SerialName("id")
    val id: Int,
    @SerialName("documentId")
    val documentId: String,
    @SerialName("first_name")
    val firstName: String,
    @SerialName("last_name")
    val lastName: String,
    @SerialName("first_line_address")
    val firstLineAddress: String,
    @SerialName("second_line_address")
    val secondLineAddress: String? = null,
    @SerialName("post_code")
    val postCode: String,
    @SerialName("city")
    val city: String,
    @SerialName("country")
    val country: String,
    @SerialName("phone_number")
    val phoneNumber: String? = null,
    @SerialName("createdAt")
    val createdAt: String,
    @SerialName("updatedAt")
    val updatedAt: String,
    @SerialName("publishedAt")
    val publishedAt: String
)

@Serializable
data class StrapiAddressCreateRequest(
    @SerialName("data")
    val data: StrapiAddressCreateData
)

@Serializable
data class StrapiAddressCreateData(
    @SerialName("first_name")
    val firstName: String,
    @SerialName("last_name")
    val lastName: String,
    @SerialName("first_line_address")
    val firstLineAddress: String,
    @SerialName("second_line_address")
    val secondLineAddress: String? = null,
    @SerialName("post_code")
    val postCode: String,
    @SerialName("city")
    val city: String,
    @SerialName("country")
    val country: String,
    @SerialName("phone_number")
    val phoneNumber: String? = null
)

@Serializable
data class StrapiAddressUpdateRequest(
    @SerialName("data")
    val data: StrapiAddressUpdateData
)

@Serializable
data class StrapiAddressUpdateData(
    @SerialName("first_name")
    val firstName: String? = null,
    @SerialName("last_name")
    val lastName: String? = null,
    @SerialName("first_line_address")
    val firstLineAddress: String? = null,
    @SerialName("second_line_address")
    val secondLineAddress: String? = null,
    @SerialName("post_code")
    val postCode: String? = null,
    @SerialName("city")
    val city: String? = null,
    @SerialName("country")
    val country: String? = null,
    @SerialName("phone_number")
    val phoneNumber: String? = null
)

@Serializable
data class ProfileAddressUpdateRequest(
    @SerialName("data")
    val data: ProfileAddressUpdateData
)

@Serializable
data class ProfileAddressUpdateData(
    @SerialName("addresses")
    val addresses: List<String>
)

@Serializable
data class StrapiMeta(
    @SerialName("pagination")
    val pagination: StrapiPagination? = null
)

@Serializable
data class StrapiPagination(
    @SerialName("page")
    val page: Int = 1,
    @SerialName("pageSize")
    val pageSize: Int = 25,
    @SerialName("pageCount")
    val pageCount: Int = 1,
    @SerialName("total")
    val total: Int = 0
)