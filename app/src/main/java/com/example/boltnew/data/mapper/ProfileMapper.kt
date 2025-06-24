package com.example.boltnew.data.mapper

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.boltnew.data.database.*
import com.example.boltnew.data.model.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE
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
        username = username,
        email = email,
        dateOfBirth = LocalDate.parse(dateOfBirth, dateFormatter),
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
                formats = AvatarFormats(
                    thumbnail = avatarThumbnailUrl?.let { 
                        AvatarFormat("thumbnail", it, 0, 0, 0.0) 
                    },
                    small = avatarSmallUrl?.let { 
                        AvatarFormat("small", it, 0, 0, 0.0) 
                    },
                    medium = avatarMediumUrl?.let { 
                        AvatarFormat("medium", it, 0, 0, 0.0) 
                    },
                    large = avatarLargeUrl?.let { 
                        AvatarFormat("large", it, 0, 0, 0.0) 
                    }
                )
            )
        } else null,
        userAdverts = userAdverts.map { it.toDomain() },
        role = if (roleId != null) {
            UserRole(
                id = roleId,
                documentId = roleDocumentId ?: "",
                name = roleName ?: "",
                description = roleDescription ?: "",
                type = roleType ?: ""
            )
        } else null,
        isBlocked = isBlocked,
        isConfirmed = isConfirmed,
        provider = provider
    )
}

@RequiresApi(Build.VERSION_CODES.O)
fun Profile.toEntity(): ProfileEntity {
    val now = LocalDateTime.now().format(dateTimeFormatter)
    return ProfileEntity(
        id = id,
        documentId = documentId,
        username = username,
        email = email,
        dateOfBirth = dateOfBirth.format(dateFormatter),
        isBlocked = isBlocked,
        isConfirmed = isConfirmed,
        provider = provider,
        createdAt = now,
        updatedAt = now,
        publishedAt = now,
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
        // Role fields
        roleId = role?.id,
        roleDocumentId = role?.documentId,
        roleName = role?.name,
        roleDescription = role?.description,
        roleType = role?.type
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
        phoneNumber = phoneNumber
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
        createdAt = now,
        updatedAt = now,
        publishedAt = now
    )
}

@RequiresApi(Build.VERSION_CODES.O)
fun UserAdvertEntity.toDomain(): UserAdvert {
    return UserAdvert(
        id = id,
        documentId = documentId,
        title = title,
        description = description,
        slug = slug
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
        slug = slug,
        createdAt = now,
        updatedAt = now,
        publishedAt = now
    )
}