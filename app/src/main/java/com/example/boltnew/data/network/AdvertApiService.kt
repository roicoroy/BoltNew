package com.example.boltnew.data.network

import com.example.boltnew.data.model.advert.AdvertCreateResponse
import com.example.boltnew.data.model.advert.StrapiAdvertCreateRequest
import com.example.boltnew.data.model.advert.StrapiAdvertResponse
import com.example.boltnew.data.model.advert.StrapiAdvertSingleResponse
import com.example.boltnew.data.model.advert.StrapiAdvertUpdateRequest
import com.example.boltnew.data.model.advert.StrapiCategory
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import io.ktor.http.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class AdvertApiService {
    
    private val client = HttpClient.client
    private val baseUrl = "https://8c0c-86-156-238-78.ngrok-free.app/api"
    
    suspend fun getAllAdverts(): Result<StrapiAdvertResponse> {
        return try {
            val response = client.get("$baseUrl/adverts") {
                parameter("populate", "*")
                contentType(ContentType.Application.Json)
                header("ngrok-skip-browser-warning", "true")
            }
            Result.success(response.body<StrapiAdvertResponse>())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getAdvertById(id: Int): Result<StrapiAdvertSingleResponse> {
        return try {
            val response = client.get("$baseUrl/adverts/$id") {
                parameter("populate", "*")
                contentType(ContentType.Application.Json)
                header("ngrok-skip-browser-warning", "true")
            }
            Result.success(response.body<StrapiAdvertSingleResponse>())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getAdvertsByCategory(categorySlug: String): Result<StrapiAdvertResponse> {
        return try {
            val response = client.get("$baseUrl/adverts") {
                parameter("populate", "*")
                parameter("filters[category][slug][\$eq]", categorySlug)
                contentType(ContentType.Application.Json)
                header("ngrok-skip-browser-warning", "true")
            }
            Result.success(response.body<StrapiAdvertResponse>())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun searchAdverts(query: String): Result<StrapiAdvertResponse> {
        return try {
            val response = client.get("$baseUrl/adverts") {
                parameter("populate", "*")
                parameter("filters[\$or][0][title][\$containsi]", query)
                parameter("filters[\$or][1][description][\$containsi]", query)
                contentType(ContentType.Application.Json)
                header("ngrok-skip-browser-warning", "true")
            }
            Result.success(response.body<StrapiAdvertResponse>())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun createAdvert(request: StrapiAdvertCreateRequest, token: String): Result<AdvertCreateResponse> {
        return try {
            println("📝 Creating new advert...")
            
            val response = client.post("$baseUrl/adverts") {
                header("Authorization", "Bearer $token")
                header("ngrok-skip-browser-warning", "true")
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            
            println("📤 Create advert response status: ${response.status}")
            
            if (response.status.isSuccess()) {
                val advertResponse = response.body<AdvertCreateResponse>()
                println("✅ Advert created successfully: ${advertResponse.data.id}")
                Result.success(advertResponse)
            } else {
                val errorBody = response.bodyAsText()
                println("❌ Advert creation failed: $errorBody")
                Result.failure(Exception("Advert creation failed: ${response.status} - $errorBody"))
            }
        } catch (e: Exception) {
            println("💥 Advert creation error: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }
    
    suspend fun updateAdvert(id: Int, request: StrapiAdvertUpdateRequest, token: String): Result<AdvertCreateResponse> {
        return try {
            println("🔄 Updating advert $id...")
            
            val response = client.put("$baseUrl/adverts/$id") {
                header("Authorization", "Bearer $token")
                header("ngrok-skip-browser-warning", "true")
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            
            println("📤 Update advert response status: ${response.status}")
            
            if (response.status.isSuccess()) {
                val advertResponse = response.body<AdvertCreateResponse>()
                println("✅ Advert updated successfully: ${advertResponse.data.id}")
                Result.success(advertResponse)
            } else {
                val errorBody = response.bodyAsText()
                println("❌ Advert update failed: $errorBody")
                Result.failure(Exception("Advert update failed: ${response.status} - $errorBody"))
            }
        } catch (e: Exception) {
            println("💥 Advert update error: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }
    
    suspend fun deleteAdvert(id: Int, token: String): Result<AdvertCreateResponse> {
        return try {
            println("🗑️ Deleting advert $id...")
            
            val response = client.delete("$baseUrl/adverts/$id") {
                header("Authorization", "Bearer $token")
                header("ngrok-skip-browser-warning", "true")
                contentType(ContentType.Application.Json)
            }
            
            println("📤 Delete advert response status: ${response.status}")
            
            if (response.status.isSuccess()) {
                val advertResponse = response.body<AdvertCreateResponse>()
                println("✅ Advert deleted successfully")
                Result.success(advertResponse)
            } else {
                val errorBody = response.bodyAsText()
                println("❌ Advert deletion failed: $errorBody")
                Result.failure(Exception("Advert deletion failed: ${response.status} - $errorBody"))
            }
        } catch (e: Exception) {
            println("💥 Advert deletion error: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }
    
    suspend fun getCategories(): Result<List<String>> {
        return try {
            val response = client.get("$baseUrl/categories") {
                contentType(ContentType.Application.Json)
                header("ngrok-skip-browser-warning", "true")
            }
            val categoriesResponse = response.body<StrapiCategoriesResponse>()
            val categoryNames = categoriesResponse.data.map { it.name }.distinct()
            Result.success(categoryNames)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getCategoriesWithDetails(): Result<List<StrapiCategoryOption>> {
        return try {
            val response = client.get("$baseUrl/categories") {
                contentType(ContentType.Application.Json)
                header("ngrok-skip-browser-warning", "true")
            }
            val categoriesResponse = response.body<StrapiCategoriesResponse>()
            val categoryOptions = categoriesResponse.data.map { 
                StrapiCategoryOption(
                    id = it.id,
                    name = it.name,
                    slug = it.slug
                )
            }
            Result.success(categoryOptions)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Link adverts to profile
     */
    suspend fun updateProfileAdverts(
        profileDocumentId: String,
        advertIds: List<String>,
        token: String
    ): Result<Boolean> {
        return try {
            println("🔗 Linking adverts ${advertIds.joinToString()} to profile $profileDocumentId...")
            
            val requestBody = ProfileAdvertUpdateRequest(
                data = ProfileAdvertUpdateData(
                    adverts = advertIds
                )
            )
            
            val response = client.put("$baseUrl/profiles/$profileDocumentId") {
                header("Authorization", "Bearer $token")
                header("ngrok-skip-browser-warning", "true")
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }
            
            println("🔄 Profile adverts update response status: ${response.status}")
            
            if (response.status.isSuccess()) {
                println("✅ Profile adverts updated successfully")
                Result.success(true)
            } else {
                val errorBody = response.bodyAsText()
                println("❌ Profile adverts update failed: $errorBody")
                Result.failure(Exception("Profile adverts update failed: ${response.status} - $errorBody"))
            }
            
        } catch (e: Exception) {
            println("💥 Profile adverts update error: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }
    
    // Health check method to test API connectivity
    suspend fun healthCheck(): Result<Boolean> {
        return try {
            val response = client.get("$baseUrl/adverts") {
                parameter("pagination[limit]", "1")
                contentType(ContentType.Application.Json)
                header("ngrok-skip-browser-warning", "true")
            }
            Result.success(response.status.isSuccess())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

@kotlinx.serialization.Serializable
data class StrapiCategoriesResponse(
    @kotlinx.serialization.SerialName("data")
    val data: List<StrapiCategory>
)

@Serializable
data class StrapiCategoryOption(
    val id: Int,
    val name: String,
    val slug: String
)

@Serializable
data class ProfileAdvertUpdateRequest(
    @SerialName("data")
    val data: ProfileAdvertUpdateData
)

@Serializable
data class ProfileAdvertUpdateData(
    @SerialName("adverts")
    val adverts: List<String>
)