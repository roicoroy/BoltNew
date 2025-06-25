package com.example.boltnew.data.model

import java.time.LocalDate

// Domain model built from StrapiUser
data class User(
    val id: Int,
    val documentId: String,
    val username: String,
    val email: String,
    val blocked: Boolean,
    val confirmed: Boolean,
    val provider: String,
    val createdAt: String,
    val updatedAt: String,
    val publishedAt: String,
    val profile: UserProfile,
    val role: UserRole
) {
    val displayName: String
        get() = username.ifBlank { email.substringBefore("@") }
    
    val isActive: Boolean
        get() = confirmed && !blocked
}

data class UserProfile(
    val id: Int,
    val documentId: String,
    val dateOfBirth: String, // Keep as string to match Strapi format
    val createdAt: String,
    val updatedAt: String,
    val publishedAt: String
) {
    val dateOfBirthFormatted: LocalDate?
        get() = try {
            if (dateOfBirth.isNotBlank()) {
                LocalDate.parse(dateOfBirth)
            } else null
        } catch (e: Exception) {
            null
        }
}

data class UserRole(
    val id: Int,
    val documentId: String,
    val name: String,
    val description: String,
    val type: String,
    val createdAt: String,
    val updatedAt: String,
    val publishedAt: String
)

// Extension function to convert StrapiUser to User
fun StrapiUser.toDomainUser(): User {
    return User(
        id = id,
        documentId = documentId,
        username = username,
        email = email,
        blocked = blocked,
        confirmed = confirmed,
        provider = provider,
        createdAt = createdAt,
        updatedAt = updatedAt,
        publishedAt = publishedAt,
        profile = UserProfile(
            id = profile.id,
            documentId = profile.documentId,
            dateOfBirth = profile.dob,
            createdAt = profile.createdAt,
            updatedAt = profile.updatedAt,
            publishedAt = profile.publishedAt
        ),
        role = UserRole(
            id = role.id,
            documentId = role.documentId,
            name = role.name,
            description = role.description,
            type = role.type,
            createdAt = role.createdAt,
            updatedAt = role.updatedAt,
            publishedAt = role.publishedAt
        )
    )
}