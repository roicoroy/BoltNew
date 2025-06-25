package com.example.boltnew.data.mapper

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.boltnew.data.database.*
import com.example.boltnew.data.model.auth.profile.Address
import com.example.boltnew.data.model.auth.profile.Avatar
import com.example.boltnew.data.model.auth.profile.AvatarFormat
import com.example.boltnew.data.model.auth.profile.AvatarFormats
import com.example.boltnew.data.model.auth.profile.Profile
import com.example.boltnew.data.model.auth.profile.ProfileUser
import com.example.boltnew.data.model.auth.profile.UserAdvert
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
private val dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

@RequiresApi(Build.VERSION_CODES.O)
fun ProfileEntity.toDomain(
    addresses: List<AddressEntity> = emptyList(),
    userAdverts: List<UserAdvertEntity> = emptyList()
): Profile {
    return Profile(
        id = id,
        documentId = documentId,
        dateOfBirth = dateOfBirth,
        createdAt = createdAt,
        updatedAt = updatedAt,
        publishedAt = publishedAt,
        user = ProfileUser(
            id = id, // Using same id for user
            documentId = documentId,
            username = username,
            email = email,
            blocked = isBlocked,
            confirmed = isConfirmed,
            provider = provider,
            createdAt = createdAt,
            updatedAt = updatedAt,
            publishedAt = publishedAt
        ),
        addresses = addresses.map { it.toDomain() },
        avatar = if (avatarUrl != null) {
            Avatar(
                id = avatarId ?: 0,
                documentId = avatarDocumentId ?: "",
                name = avatarName ?: "",
                url = avatarUrl,
                alternativeText = avatarAlternativeText,
                caption = avatarCaption,
                width = avatarWidth ?: 0,
                height = avatarHeight ?: 0,
                size = avatarSize ?: 0.0,
                ext = "",
                mime = "",
                hash = "",
                provider = "",
                createdAt = createdAt,
                updatedAt = updatedAt,
                publishedAt = publishedAt,
                formats = AvatarFormats(
                    thumbnail = avatarThumbnailUrl?.let {
                        AvatarFormat("thumbnail", it, 0, 0, 0.0, "", "", "", 0)
                    },
                    small = avatarSmallUrl?.let {
                        AvatarFormat("small", it, 0, 0, 0.0, "", "", "", 0)
                    },
                    medium = avatarMediumUrl?.let {
                        AvatarFormat("medium", it, 0, 0, 0.0, "", "", "", 0)
                    },
                    large = avatarLargeUrl?.let {
                        AvatarFormat("large", it, 0, 0, 0.0, "", "", "", 0)
                    }
                )
            )
        } else null,
        userAdverts = userAdverts.map { it.toDomain() }
    )
}

@RequiresApi(Build.VERSION_CODES.O)
fun Profile.toEntity(): ProfileEntity {
    val now = LocalDateTime.now().format(dateTimeFormatter)
    return ProfileEntity(
        id = id,
        documentId = documentId,
        username = user.username,
        email = user.email,
        dateOfBirth = dateOfBirth,
        isBlocked = user.blocked,
        isConfirmed = user.confirmed,
        provider = user.provider,
        createdAt = createdAt.ifBlank { now },
        updatedAt = updatedAt.ifBlank { now },
        publishedAt = publishedAt.ifBlank { now },
        // Avatar fields
        avatarId = avatar?.id,
        avatarDocumentId = avatar?.documentId,
        avatarName = avatar?.name,
        avatarUrl = avatar?.url,
        avatarAlternativeText = avatar?.alternativeText,
        avatarCaption = avatar?.caption,
        avatarWidth = avatar?.width,
        avatarHeight = avatar?.height,
        avatarSize = avatar?.size,
        avatarThumbnailUrl = avatar?.formats?.thumbnail?.url,
        avatarSmallUrl = avatar?.formats?.small?.url,
        avatarMediumUrl = avatar?.formats?.medium?.url,
        avatarLargeUrl = avatar?.formats?.large?.url,
        // Role fields - not available in current Profile model
        roleId = null,
        roleDocumentId = null,
        roleName = null,
        roleDescription = null,
        roleType = null
    )
}

@RequiresApi(Build.VERSION_CODES.O)
fun AddressEntity.toDomain(): Address {
    return Address(
        id = id,
        documentId = documentId,
        firstName = firstName,
        lastName = lastName,
        firstLineAddress = firstLineAddress,
        secondLineAddress = secondLineAddress,
        city = city,
        postCode = postCode,
        country = country,
        phoneNumber = phoneNumber,
        createdAt = createdAt,
        updatedAt = updatedAt,
        publishedAt = publishedAt
    )
}

@RequiresApi(Build.VERSION_CODES.O)
fun Address.toEntity(profileId: Int): AddressEntity {
    val now = LocalDateTime.now().format(dateTimeFormatter)
    return AddressEntity(
        id = id,
        profileId = profileId,
        documentId = documentId,
        firstName = firstName,
        lastName = lastName,
        firstLineAddress = firstLineAddress,
        secondLineAddress = secondLineAddress,
        city = city,
        postCode = postCode,
        country = country,
        phoneNumber = phoneNumber,
        createdAt = createdAt.ifBlank { now },
        updatedAt = updatedAt.ifBlank { now },
        publishedAt = publishedAt.ifBlank { now }
    )
}

@RequiresApi(Build.VERSION_CODES.O)
fun UserAdvertEntity.toDomain(): UserAdvert {
    return UserAdvert(
        id = id,
        documentId = documentId,
        title = title,
        description = description,
        slug = slug,
        createdAt = createdAt,
        updatedAt = updatedAt,
        publishedAt = publishedAt
    )
}

@RequiresApi(Build.VERSION_CODES.O)
fun UserAdvert.toEntity(profileId: Int): UserAdvertEntity {
    val now = LocalDateTime.now().format(dateTimeFormatter)
    return UserAdvertEntity(
        id = id,
        profileId = profileId,
        documentId = documentId,
        title = title,
        description = description,
        slug = slug ?: "",
        createdAt = createdAt.ifBlank { now },
        updatedAt = updatedAt.ifBlank { now },
        publishedAt = publishedAt.ifBlank { now }
    )
}