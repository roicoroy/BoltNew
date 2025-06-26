package com.example.boltnew.data.network

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import io.ktor.http.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class ProfileApiService {

    /**
     * Create a new profile
     */
    suspend fun createProfile(
        dateOfBirth: String,
        token: String
    ): Result<StrapiProfileCreateResponse> {
        return try {
            println("🆕 Creating new profile...")
            
            val requestBody = StrapiProfileCreateRequest(
                data = StrapiProfileCreateData(
                    dob = dateOfBirth
                )
            )
            
            val response = client.post("$baseUrl/profiles") {
                header("Authorization", "Bearer $token")
                header("ngrok-skip-browser-warning", "true")
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }
            
            println("📤 Create profile response status: ${response.status}")
            
            if (response.status.isSuccess()) {
                val profileResponse = response.body<StrapiProfileCreateResponse>()
                println("✅ Profile created successfully: ${profileResponse.data.id}")
                Result.success(profileResponse)
            } else {
                val errorBody = response.bodyAsText()
                println("❌ Profile creation failed: $errorBody")
                Result.failure(Exception("Profile creation failed: ${response.status} - $errorBody"))
            }
            
        } catch (e: Exception) {
            println("💥 Profile creation error: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }

    /**
     * Link profile to user
     */
    suspend fun linkProfileToUser(
        profileDocumentId: String,
        userId: Int,
        token: String
    ): Result<Boolean> {
        return try {
            println("🔗 Linking profile $profileDocumentId to user $userId...")
            
            val requestBody = StrapiProfileUserLinkRequest(
                data = StrapiProfileUserLinkData(
                    user = listOf(userId.toString())
                )
            )
            
            val response = client.put("$baseUrl/profiles/$profileDocumentId") {
                header("Authorization", "Bearer $token")
                header("ngrok-skip-browser-warning", "true")
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }
            
            println("🔄 Link profile response status: ${response.status}")
            
            if (response.status.isSuccess()) {
                println("✅ Profile linked to user successfully")
                Result.success(true)
            } else {
                val errorBody = response.bodyAsText()
                println("❌ Profile linking failed: $errorBody")
                Result.failure(Exception("Profile linking failed: ${response.status} - $errorBody"))
            }
            
        } catch (e: Exception) {
            println("💥 Profile linking error: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }

    /**
     * Update profile date of birth
     */
    suspend fun updateProfileDob(
        profileDocumentId: String,
        dateOfBirth: String,
        token: String
    ): Result<StrapiProfileUpdateResponse> {
        return try {
            println("🔄 Updating profile DOB for: $profileDocumentId")
            
            val requestBody = StrapiProfileUpdateRequest(
                data = StrapiProfileUpdateData(
                    dob = dateOfBirth
                )
            )
            
            val response = client.put("$baseUrl/profiles/$profileDocumentId") {
                header("Authorization", "Bearer $token")
                header("ngrok-skip-browser-warning", "true")
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }
            
            println("📤 Update profile response status: ${response.status}")
            
            if (response.status.isSuccess()) {
                val profileResponse = response.body<StrapiProfileUpdateResponse>()
                println("✅ Profile updated successfully: ${profileResponse.data.id}")
                Result.success(profileResponse)
            } else {
                val errorBody = response.bodyAsText()
                println("❌ Profile update failed: $errorBody")
                Result.failure(Exception("Profile update failed: ${response.status} - $errorBody"))
            }
            
        } catch (e: Exception) {
            println("💥 Profile update error: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }
}

// Data classes for profile creation
@Serializable
data class StrapiProfileCreateRequest(
    @SerialName("data")
    val data: StrapiProfileCreateData
)

@Serializable
data class StrapiProfileCreateData(
    @SerialName("dob")
    val dob: String
)

@Serializable
data class StrapiProfileCreateResponse(
    @SerialName("data")
    val data: StrapiProfileCreateResponseData,
    @SerialName("meta")
    val meta: StrapiProfileCreateMeta = StrapiProfileCreateMeta()
)

@Serializable
data class StrapiProfileCreateResponseData(
    @SerialName("id")
    val id: Int,
    @SerialName("documentId")
    val documentId: String,
    @SerialName("dob")
    val dob: String,
    @SerialName("createdAt")
    val createdAt: String,
    @SerialName("updatedAt")
    val updatedAt: String,
    @SerialName("publishedAt")
    val publishedAt: String
)

@Serializable
data class StrapiProfileCreateMeta(
    @SerialName("pagination")
    val pagination: StrapiProfileCreatePagination? = null
)

@Serializable
data class StrapiProfileCreatePagination(
    @SerialName("page")
    val page: Int = 1,
    @SerialName("pageSize")
    val pageSize: Int = 25,
    @SerialName("pageCount")
    val pageCount: Int = 1,
    @SerialName("total")
    val total: Int = 0
)

// Data classes for linking profile to user
@Serializable
data class StrapiProfileUserLinkRequest(
    @SerialName("data")
    val data: StrapiProfileUserLinkData
)

@Serializable
data class StrapiProfileUserLinkData(
    @SerialName("user")
    val user: List<String>
)

// Data classes for profile update
@Serializable
data class StrapiProfileUpdateRequest(
    @SerialName("data")
    val data: StrapiProfileUpdateData
)

@Serializable
data class StrapiProfileUpdateData(
    @SerialName("dob")
    val dob: String
)

@Serializable
data class StrapiProfileUpdateResponse(
    @SerialName("data")
    val data: StrapiProfileUpdateResponseData,
    @SerialName("meta")
    val meta: StrapiProfileUpdateMeta = StrapiProfileUpdateMeta()
)

@Serializable
data class StrapiProfileUpdateResponseData(
    @SerialName("id")
    val id: Int,
    @SerialName("documentId")
    val documentId: String,
    @SerialName("dob")
    val dob: String,
    @SerialName("createdAt")
    val createdAt: String,
    @SerialName("updatedAt")
    val updatedAt: String,
    @SerialName("publishedAt")
    val publishedAt: String
)

@Serializable
data class StrapiProfileUpdateMeta(
    @SerialName("pagination")
    val pagination: StrapiProfileUpdatePagination? = null
)

@Serializable
data class StrapiProfileUpdatePagination(
    @SerialName("page")
    val page: Int = 1,
    @SerialName("pageSize")
    val pageSize: Int = 25,
    @SerialName("pageCount")
    val pageCount: Int = 1,
    @SerialName("total")
    val total: Int = 0
)