package com.example.boltnew.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "adverts")
data class AdvertEntity(
    @PrimaryKey
    val id: Int,
    val documentId: String,
    val title: String,
    val description: String,
    val slug: String,
    val createdAt: String, // ISO date string
    val updatedAt: String, // ISO date string
    val publishedAt: String, // ISO date string
    val coverUrl: String?,
    val coverThumbnailUrl: String?,
    val coverSmallUrl: String?,
    val categoryId: Int,
    val categoryName: String,
    val categorySlug: String,
    val categoryDescription: String
)