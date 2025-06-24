package com.example.boltnew.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profile")
data class ProfileEntity(
    @PrimaryKey
    val id: Int = 1,
    val documentId: String,
    val username: String,
    val email: String,
    val dateOfBirth: String, // ISO date string
    val isBlocked: Boolean,
    val isConfirmed: Boolean,
    val provider: String,
    val createdAt: String,
    val updatedAt: String,
    val publishedAt: String,
    // Avatar fields (flattened)
    val avatarId: Int?,
    val avatarDocumentId: String?,
    val avatarName: String?,
    val avatarUrl: String?,
    val avatarAlternativeText: String?,
    val avatarCaption: String?,
    val avatarWidth: Int?,
    val avatarHeight: Int?,
    val avatarSize: Double?,
    val avatarThumbnailUrl: String?,
    val avatarSmallUrl: String?,
    val avatarMediumUrl: String?,
    val avatarLargeUrl: String?,
    // Role fields (flattened)
    val roleId: Int?,
    val roleDocumentId: String?,
    val roleName: String?,
    val roleDescription: String?,
    val roleType: String?
)

@Entity(tableName = "address")
data class AddressEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val profileId: Int, // Foreign key to profile
    val documentId: String,
    val firstName: String,
    val lastName: String,
    val firstLineAddress: String,
    val secondLineAddress: String,
    val city: String,
    val postCode: String,
    val country: String,
    val phoneNumber: String?,
    val createdAt: String,
    val updatedAt: String,
    val publishedAt: String
)

@Entity(tableName = "user_advert")
data class UserAdvertEntity(
    @PrimaryKey
    val id: Int,
    val profileId: Int, // Foreign key to profile
    val documentId: String,
    val title: String,
    val description: String,
    val slug: String?,
    val createdAt: String,
    val updatedAt: String,
    val publishedAt: String
)