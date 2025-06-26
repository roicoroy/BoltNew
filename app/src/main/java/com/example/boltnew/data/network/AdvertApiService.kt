package com.example.boltnew.data.network

import com.example.boltnew.data.model.ProfileAdvertUpdateData
import com.example.boltnew.data.model.ProfileAdvertUpdateRequest
import com.example.boltnew.data.model.StrapiCategoriesResponse
import com.example.boltnew.data.model.StrapiCategoryOption
import com.example.boltnew.data.model.advert.AdvertCreateResponse
import com.example.boltnew.data.model.advert.StrapiAdvertCreateRequest
import com.example.boltnew.data.model.advert.StrapiAdvertResponse
import com.example.boltnew.data.model.advert.StrapiAdvertSingleResponse
import com.example.boltnew.data.model.advert.StrapiAdvertUpdateRequest
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess

class AdvertApiService {

    suspend fun getAllAdverts(): Result<StrapiAdvertResponse> {
        return try {
            val response = client.get("$baseUrl/adverts") {
                parameter("populate", "*")
                contentType(ContentType.Application.Json)
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
    
    suspend fun deleteAdvert(id: String, token: String): Result<Boolean> {
        return try {
            val response = client.delete("$baseUrl/adverts/$id") {
                header("Authorization", "Bearer $token")
                contentType(ContentType.Application.Json)
            }
            if (response.status.value == 204) {
                Result.success(true)
            } else {
                val errorBody = response.bodyAsText()
                println("❌ Advert deletion failed: $errorBody")
                Result.failure(Exception("Advert deletion failed: ${response.status} - $errorBody"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
    
    suspend fun getCategories(): Result<List<String>> {
        return try {
            val response = client.get("$baseUrl/categories") {
                contentType(ContentType.Application.Json)
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

