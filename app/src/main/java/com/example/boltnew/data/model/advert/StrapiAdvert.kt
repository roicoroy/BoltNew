package com.example.boltnew.data.model.advert

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StrapiAdvertResponse(
    @SerialName("data")
    val data: List<StrapiAdvert> = emptyList(),
    @SerialName("meta")
    val meta: StrapiMeta = StrapiMeta()
)

@Serializable
data class StrapiAdvertSingleResponse(
    @SerialName("data")
    val data: StrapiAdvert,
    @SerialName("meta")
    val meta: StrapiMeta = StrapiMeta()
)

@Serializable
data class StrapiAdvert(
    @SerialName("id")
    val id: Int,
    @SerialName("documentId")
    val documentId: String,
    @SerialName("title")
    val title: String,
    @SerialName("description")
    val description: String,
    @SerialName("slug")
    val slug: String,
    @SerialName("createdAt")
    val createdAt: String,
    @SerialName("updatedAt")
    val updatedAt: String,
    @SerialName("publishedAt")
    val publishedAt: String,
    @SerialName("cover")
    val cover: StrapiCover? = null,
    @SerialName("category")
    val category: StrapiCategory
)

@Serializable
data class StrapiCover(
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
    val formats: StrapiCoverFormats? = null,
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
data class StrapiCoverFormats(
    @SerialName("thumbnail")
    val thumbnail: StrapiCoverFormat? = null,
    @SerialName("small")
    val small: StrapiCoverFormat? = null,
    @SerialName("medium")
    val medium: StrapiCoverFormat? = null,
    @SerialName("large")
    val large: StrapiCoverFormat? = null
)

@Serializable
data class StrapiCoverFormat(
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
data class StrapiCategory(
    @SerialName("id")
    val id: Int,
    @SerialName("documentId")
    val documentId: String,
    @SerialName("name")
    val name: String,
    @SerialName("slug")
    val slug: String,
    @SerialName("description")
    val description: String,
    @SerialName("createdAt")
    val createdAt: String,
    @SerialName("updatedAt")
    val updatedAt: String,
    @SerialName("publishedAt")
    val publishedAt: String
)

@Serializable
data class StrapiProviderMetadata(
    @SerialName("public_id")
    val publicId: String = "",
    @SerialName("resource_type")
    val resourceType: String = ""
)

@Serializable
data class StrapiMeta(
    @SerialName("pagination")
    val pagination: StrapiPagination? = null
)

@Serializable
data class StrapiPagination(
    @SerialName("page")
    val page: Int,
    @SerialName("pageSize")
    val pageSize: Int,
    @SerialName("pageCount")
    val pageCount: Int,
    @SerialName("total")
    val total: Int
)

// Create request models for POST/PUT operations
@Serializable
data class StrapiAdvertCreateRequest(
    @SerialName("data")
    val data: StrapiAdvertCreateData
)

@Serializable
data class StrapiAdvertCreateData(
    @SerialName("title")
    val title: String,
    @SerialName("description")
    val description: String,
    @SerialName("slug")
    val slug: String,
    @SerialName("category")
    val category: Int // Category ID
)

@Serializable
data class StrapiAdvertUpdateRequest(
    @SerialName("data")
    val data: StrapiAdvertUpdateData
)

@Serializable
data class StrapiAdvertUpdateData(
    @SerialName("title")
    val title: String? = null,
    @SerialName("description")
    val description: String? = null,
    @SerialName("slug")
    val slug: String? = null,
    @SerialName("category")
    val category: Int? = null
)