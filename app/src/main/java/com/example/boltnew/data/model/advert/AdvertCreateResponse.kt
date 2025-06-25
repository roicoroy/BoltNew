package com.example.boltnew.data.model.advert

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AdvertCreateResponse(
    @SerialName("data")
    val data: AdvertCreateData,
    @SerialName("meta")
    val meta: AdvertCreateMeta = AdvertCreateMeta()
)

@Serializable
data class AdvertCreateData(
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
    val publishedAt: String
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