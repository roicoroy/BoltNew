package com.example.boltnew.data.model

import java.time.LocalDate

// Domain model built from StrapiProfile
data class Profile(
    val id: Int,
    val documentId: String,
    val dateOfBirth: String, // Keep as string to match Strapi format
    val createdAt: String,
    val updatedAt: String,
    val publishedAt: String,
    val user: ProfileUser,
    val addresses: List<Address> = emptyList(),
    val avatar: Avatar? = null,
    val userAdverts: List<UserAdvert> = emptyList()
) {
    val displayName: String
        get() = user.username.ifBlank { user.email.substringBefore("@") }
    
    val email: String
        get() = user.email
    
    val username: String
        get() = user.username
    
    val isBlocked: Boolean
        get() = user.blocked
    
    val isConfirmed: Boolean
        get() = user.confirmed
    
    val provider: String
        get() = user.provider
    
    val dateOfBirthFormatted: LocalDate?
        get() = try {
            if (dateOfBirth.isNotBlank()) {
                LocalDate.parse(dateOfBirth)
            } else null
        } catch (e: Exception) {
            null
        }
    
    val fullAddress: String
        get() = addresses.firstOrNull()?.let { address ->
            buildString {
                append(address.firstLineAddress)
                if (address.secondLineAddress.isNotBlank()) {
                    append(", ${address.secondLineAddress}")
                }
                append(", ${address.city} ${address.postCode}")
                append(", ${address.country}")
            }
        } ?: ""
}

data class ProfileUser(
    val id: Int,
    val documentId: String,
    val username: String,
    val email: String,
    val blocked: Boolean,
    val confirmed: Boolean,
    val provider: String,
    val createdAt: String,
    val updatedAt: String,
    val publishedAt: String
)

data class Address(
    val id: Int,
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
) {
    val fullName: String
        get() = "$firstName $lastName"
}

data class Avatar(
    val id: Int,
    val documentId: String,
    val name: String,
    val url: String,
    val alternativeText: String?,
    val caption: String?,
    val width: Int,
    val height: Int,
    val size: Double,
    val ext: String,
    val mime: String,
    val hash: String,
    val provider: String,
    val createdAt: String,
    val updatedAt: String,
    val publishedAt: String,
    val formats: AvatarFormats?
)

data class AvatarFormats(
    val thumbnail: AvatarFormat?,
    val small: AvatarFormat?,
    val medium: AvatarFormat?,
    val large: AvatarFormat?
)

data class AvatarFormat(
    val name: String,
    val url: String,
    val width: Int,
    val height: Int,
    val size: Double,
    val ext: String,
    val hash: String,
    val mime: String,
    val sizeInBytes: Int
)

data class UserAdvert(
    val id: Int,
    val documentId: String,
    val title: String,
    val description: String,
    val slug: String?,
    val createdAt: String,
    val updatedAt: String,
    val publishedAt: String
)

// Extension function to convert StrapiProfile to Profile
fun StrapiProfile.toDomainProfile(): Profile {
    return Profile(
        id = data.id,
        documentId = data.documentId,
        dateOfBirth = data.dob,
        createdAt = data.createdAt,
        updatedAt = data.updatedAt,
        publishedAt = data.publishedAt,
        user = ProfileUser(
            id = data.user.id,
            documentId = data.user.documentId,
            username = data.user.username,
            email = data.user.email,
            blocked = data.user.blocked,
            confirmed = data.user.confirmed,
            provider = data.user.provider,
            createdAt = data.user.createdAt,
            updatedAt = data.user.updatedAt,
            publishedAt = data.user.publishedAt
        ),
        addresses = data.addresses.map { strapiAddress ->
            Address(
                id = strapiAddress.id,
                documentId = strapiAddress.documentId,
                firstName = strapiAddress.firstName,
                lastName = strapiAddress.lastName,
                firstLineAddress = strapiAddress.firstLineAddress,
                secondLineAddress = strapiAddress.secondLineAddress,
                city = strapiAddress.city,
                postCode = strapiAddress.postCode,
                country = strapiAddress.country,
                phoneNumber = strapiAddress.phoneNumber,
                createdAt = strapiAddress.createdAt,
                updatedAt = strapiAddress.updatedAt,
                publishedAt = strapiAddress.publishedAt
            )
        },
        avatar = if (data.avatar.url.isNotBlank()) {
            Avatar(
                id = data.avatar.id,
                documentId = data.avatar.documentId,
                name = data.avatar.name,
                url = data.avatar.url,
                alternativeText = data.avatar.alternativeText,
                caption = data.avatar.caption,
                width = data.avatar.width,
                height = data.avatar.height,
                size = data.avatar.size,
                ext = data.avatar.ext,
                mime = data.avatar.mime,
                hash = data.avatar.hash,
                provider = data.avatar.provider,
                createdAt = data.avatar.createdAt,
                updatedAt = data.avatar.updatedAt,
                publishedAt = data.avatar.publishedAt,
                formats = AvatarFormats(
                    thumbnail = if (data.avatar.formats.thumbnail.url.isNotBlank()) {
                        AvatarFormat(
                            name = data.avatar.formats.thumbnail.name,
                            url = data.avatar.formats.thumbnail.url,
                            width = data.avatar.formats.thumbnail.width,
                            height = data.avatar.formats.thumbnail.height,
                            size = data.avatar.formats.thumbnail.size,
                            ext = data.avatar.formats.thumbnail.ext,
                            hash = data.avatar.formats.thumbnail.hash,
                            mime = data.avatar.formats.thumbnail.mime,
                            sizeInBytes = data.avatar.formats.thumbnail.sizeInBytes
                        )
                    } else null,
                    small = if (data.avatar.formats.small.url.isNotBlank()) {
                        AvatarFormat(
                            name = data.avatar.formats.small.name,
                            url = data.avatar.formats.small.url,
                            width = data.avatar.formats.small.width,
                            height = data.avatar.formats.small.height,
                            size = data.avatar.formats.small.size,
                            ext = data.avatar.formats.small.ext,
                            hash = data.avatar.formats.small.hash,
                            mime = data.avatar.formats.small.mime,
                            sizeInBytes = data.avatar.formats.small.sizeInBytes
                        )
                    } else null,
                    medium = if (data.avatar.formats.medium.url.isNotBlank()) {
                        AvatarFormat(
                            name = data.avatar.formats.medium.name,
                            url = data.avatar.formats.medium.url,
                            width = data.avatar.formats.medium.width,
                            height = data.avatar.formats.medium.height,
                            size = data.avatar.formats.medium.size,
                            ext = data.avatar.formats.medium.ext,
                            hash = data.avatar.formats.medium.hash,
                            mime = data.avatar.formats.medium.mime,
                            sizeInBytes = data.avatar.formats.medium.sizeInBytes
                        )
                    } else null,
                    large = if (data.avatar.formats.large.url.isNotBlank()) {
                        AvatarFormat(
                            name = data.avatar.formats.large.name,
                            url = data.avatar.formats.large.url,
                            width = data.avatar.formats.large.width,
                            height = data.avatar.formats.large.height,
                            size = data.avatar.formats.large.size,
                            ext = data.avatar.formats.large.ext,
                            hash = data.avatar.formats.large.hash,
                            mime = data.avatar.formats.large.mime,
                            sizeInBytes = data.avatar.formats.large.sizeInBytes
                        )
                    } else null
                )
            )
        } else null,
        userAdverts = data.adverts.map { strapiAdvert ->
            UserAdvert(
                id = strapiAdvert.id,
                documentId = strapiAdvert.documentId,
                title = strapiAdvert.title,
                description = strapiAdvert.description,
                slug = strapiAdvert.slug,
                createdAt = strapiAdvert.createdAt,
                updatedAt = strapiAdvert.updatedAt,
                publishedAt = strapiAdvert.publishedAt
            )
        }
    )
}