package com.example.boltnew.data.model.advert

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AdvertCreateResponse(
    @SerialName("data")
    val data: AdvertCreateData = AdvertCreateData(),
    @SerialName("meta")
    val meta: AdvertCreateMeta = AdvertCreateMeta()
)

@Serializable
data class AdvertCreateData(
    @SerialName("id")
    val id: Int = 0,
    @SerialName("documentId")
    val documentId: String = "",
    @SerialName("title")
    val title: String = "",
    @SerialName("description")
    val description: String = "",
    @SerialName("slug")
    val slug: String = "",
    @SerialName("createdAt")
    val createdAt: String = "",
    @SerialName("updatedAt")
    val updatedAt: String = "",
    @SerialName("publishedAt")
    val publishedAt: String = "",
    @SerialName("cover")
    val cover: AdvertCreateCover? = null,
    @SerialName("category")
    val category: AdvertCreateCategory? = null
)

@Serializable
data class AdvertCreateCover(
    @SerialName("id")
    val id: Int = 0,
    @SerialName("documentId")
    val documentId: String = "",
    @SerialName("name")
    val name: String = "",
    @SerialName("url")
    val url: String = "",
    @SerialName("alternativeText")
    val alternativeText: String? = null,
    @SerialName("caption")
    val caption: String? = null,
    @SerialName("width")
    val width: Int = 0,
    @SerialName("height")
    val height: Int = 0
)

@Serializable
data class AdvertCreateCategory(
    @SerialName("id")
    val id: Int = 0,
    @SerialName("documentId")
    val documentId: String = "",
    @SerialName("name")
    val name: String = "",
    @SerialName("slug")
    val slug: String = "",
    @SerialName("description")
    val description: String = ""
)

@Serializable
data class AdvertCreateMeta(
    @SerialName("pagination")
    val pagination: AdvertCreatePagination? = null
)

@Serializable
data class AdvertCreatePagination(
    @SerialName("page")
    val page: Int = 1,
    @SerialName("pageSize")
    val pageSize: Int = 25,
    @SerialName("pageCount")
    val pageCount: Int = 1,
    @SerialName("total")
    val total: Int = 0
)