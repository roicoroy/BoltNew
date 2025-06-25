package com.example.boltnew.data.network

import com.example.boltnew.data.model.advert.StrapiAdvertCreateRequest
import com.example.boltnew.data.model.advert.StrapiAdvertResponse
import com.example.boltnew.data.model.advert.StrapiAdvertSingleResponse
import com.example.boltnew.data.model.advert.StrapiAdvertUpdateRequest
import com.example.boltnew.data.model.advert.StrapiCategory
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

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
    
    suspend fun createAdvert(request: StrapiAdvertCreateRequest): Result<StrapiAdvertSingleResponse> {
        return try {
            val response = client.post("$baseUrl/adverts") {
                contentType(ContentType.Application.Json)
                header("ngrok-skip-browser-warning", "true")
                setBody(request)
            }
            Result.success(response.body<StrapiAdvertSingleResponse>())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateAdvert(id: Int, request: StrapiAdvertUpdateRequest): Result<StrapiAdvertSingleResponse> {
        return try {
            val response = client.put("$baseUrl/adverts/$id") {
                contentType(ContentType.Application.Json)
                header("ngrok-skip-browser-warning", "true")
                setBody(request)
            }
            Result.success(response.body<StrapiAdvertSingleResponse>())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun deleteAdvert(id: Int): Result<StrapiAdvertSingleResponse> {
        return try {
            val response = client.delete("$baseUrl/adverts/$id") {
                contentType(ContentType.Application.Json)
                header("ngrok-skip-browser-warning", "true")
            }
            Result.success(response.body<StrapiAdvertSingleResponse>())
        } catch (e: Exception) {
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