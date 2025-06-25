package com.example.boltnew.data.model.advert

import java.time.LocalDateTime

data class Advert(
    val id: Int,
    val documentId: String,
    val title: String,
    val description: String,
    val slug: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val publishedAt: LocalDateTime,
    val cover: AdvertCover?,
    val category: AdvertCategory
)

data class AdvertCover(
    val id: Int,
    val documentId: String,
    val name: String,
    val alternativeText: String?,
    val caption: String?,
    val width: Int,
    val height: Int,
    val url: String,
    val formats: AdvertCoverFormats?
)

data class AdvertCoverFormats(
    val thumbnail: AdvertCoverFormat?,
    val small: AdvertCoverFormat?
)

data class AdvertCoverFormat(
    val name: String,
    val width: Int,
    val height: Int,
    val size: Double,
    val url: String
)

data class AdvertCategory(
    val id: Int,
    val documentId: String,
    val name: String,
    val slug: String,
    val description: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val publishedAt: LocalDateTime
)