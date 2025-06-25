package com.example.boltnew.data.network

import android.content.Context
import android.net.Uri
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.bodyAsText
import io.ktor.http.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.io.File

class UploadApiService {
    
    private val client = HttpClient.client
    private val baseUrl = "https://8c0c-86-156-238-78.ngrok-free.app/api"
    
    /**
     * Upload image file to Strapi upload endpoint
     */
    suspend fun uploadImage(
        context: Context,
        imageUri: Uri,
        token: String
    ): Result<StrapiUploadResponse> {
        return try {
            println("🔄 Starting image upload to Strapi...")
            
            // Get file from URI
            val inputStream = context.contentResolver.openInputStream(imageUri)
                ?: return Result.failure(Exception("Cannot open image file"))
            
            val fileBytes = inputStream.readBytes()
            inputStream.close()
            
            // Get filename from URI or create one
            val fileName = getFileNameFromUri(context, imageUri) ?: "avatar_${System.currentTimeMillis()}.jpg"
            
            println("📁 Uploading file: $fileName (${fileBytes.size} bytes)")
            
            val response = client.submitFormWithBinaryData(
                url = "$baseUrl/upload",
                formData = formData {
                    append("files", fileBytes, Headers.build {
                        append(HttpHeaders.ContentType, "image/jpeg")
                        append(HttpHeaders.ContentDisposition, "filename=\"$fileName\"")
                    })
                }
            ) {
                header("Authorization", "Bearer $token")
                header("ngrok-skip-browser-warning", "true")
            }
            
            println("📤 Upload response status: ${response.status}")
            
            if (response.status.isSuccess()) {
                val uploadResponse = response.body<List<StrapiUploadedFile>>()
                println("✅ Upload successful: ${uploadResponse.firstOrNull()?.id}")
                
                if (uploadResponse.isNotEmpty()) {
                    Result.success(StrapiUploadResponse(uploadResponse))
                } else {
                    Result.failure(Exception("Upload response is empty"))
                }
            } else {
                val errorBody = response.bodyAsText()
                println("❌ Upload failed: $errorBody")
                Result.failure(Exception("Upload failed: ${response.status} - $errorBody"))
            }
            
        } catch (e: Exception) {
            println("💥 Upload error: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }
    
    /**
     * Link uploaded image to profile
     */
    suspend fun updateProfileAvatar(
        profileDocumentId: String,
        imageId: Int,
        token: String
    ): Result<Boolean> {
        return try {
            println("🔗 Linking image $imageId to profile $profileDocumentId...")
            
            val requestBody = ProfileAvatarUpdateRequest(
                data = ProfileAvatarUpdateData(
                    avatar = listOf(imageId.toString())
                )
            )
            
            val response = client.put("$baseUrl/profiles/$profileDocumentId") {
                header("Authorization", "Bearer $token")
                header("ngrok-skip-browser-warning", "true")
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }
            
            println("🔄 Profile update response status: ${response.status}")
            
            if (response.status.isSuccess()) {
                println("✅ Profile avatar updated successfully")
                Result.success(true)
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
    
    private fun getFileNameFromUri(context: Context, uri: Uri): String? {
        return try {
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                cursor.moveToFirst()
                cursor.getString(nameIndex)
            }
        } catch (e: Exception) {
            null
        }
    }
}

// Data classes for upload response
@Serializable
data class StrapiUploadResponse(
    val files: List<StrapiUploadedFile>
)

@Serializable
data class StrapiUploadedFile(
    @SerialName("id")
    val id: Int,
    @SerialName("documentId")
    val documentId: String,
    @SerialName("name")
    val name: String,
    @SerialName("alternativeText")
    val alternativeText: String? = null,
    @SerialName("caption")
    val caption: String? = null,
    @SerialName("width")
    val width: Int,
    @SerialName("height")
    val height: Int,
    @SerialName("formats")
    val formats: StrapiUploadFormats? = null,
    @SerialName("hash")
    val hash: String,
    @SerialName("ext")
    val ext: String,
    @SerialName("mime")
    val mime: String,
    @SerialName("size")
    val size: Double,
    @SerialName("url")
    val url: String,
    @SerialName("previewUrl")
    val previewUrl: String? = null,
    @SerialName("provider")
    val provider: String,
    @SerialName("provider_metadata")
    val providerMetadata: StrapiProviderMetadata? = null,
    @SerialName("createdAt")
    val createdAt: String,
    @SerialName("updatedAt")
    val updatedAt: String,
    @SerialName("publishedAt")
    val publishedAt: String
)

@Serializable
data class StrapiUploadFormats(
    @SerialName("thumbnail")
    val thumbnail: StrapiUploadFormat? = null,
    @SerialName("small")
    val small: StrapiUploadFormat? = null,
    @SerialName("medium")
    val medium: StrapiUploadFormat? = null,
    @SerialName("large")
    val large: StrapiUploadFormat? = null
)

@Serializable
data class StrapiUploadFormat(
    @SerialName("name")
    val name: String,
    @SerialName("hash")
    val hash: String,
    @SerialName("ext")
    val ext: String,
    @SerialName("mime")
    val mime: String,
    @SerialName("width")
    val width: Int,
    @SerialName("height")
    val height: Int,
    @SerialName("size")
    val size: Double,
    @SerialName("sizeInBytes")
    val sizeInBytes: Int,
    @SerialName("url")
    val url: String,
    @SerialName("provider_metadata")
    val providerMetadata: StrapiProviderMetadata? = null
)

@Serializable
data class StrapiProviderMetadata(
    @SerialName("public_id")
    val publicId: String = "",
    @SerialName("resource_type")
    val resourceType: String = ""
)

// Data classes for profile avatar update
@Serializable
data class ProfileAvatarUpdateRequest(
    @SerialName("data")
    val data: ProfileAvatarUpdateData
)

@Serializable
data class ProfileAvatarUpdateData(
    @SerialName("avatar")
    val avatar: List<String>
)