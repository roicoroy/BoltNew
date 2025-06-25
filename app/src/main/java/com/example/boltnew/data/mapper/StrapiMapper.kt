package com.example.boltnew.data.mapper

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.boltnew.data.model.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE
@RequiresApi(Build.VERSION_CODES.O)
private val dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

@RequiresApi(Build.VERSION_CODES.O)
fun StrapiProfile.toDomain(): Profile {
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
        addresses = data.addresses.map { it.toDomain() },
        avatar = if (data.avatar.url.isNotBlank()) {
            data.avatar.toDomain()
        } else null,
        userAdverts = data.adverts.map { it.toDomain() }
    )
}

@RequiresApi(Build.VERSION_CODES.O)
fun StrapiProfile.Data.Addresse.toDomain(): Address {
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
fun StrapiProfile.Data.Avatar.toDomain(): Avatar {
    return Avatar(
        id = id,
        documentId = documentId,
        name = name,
        url = url,
        alternativeText = alternativeText,
        caption = caption,
        width = width,
        height = height,
        size = size,
        ext = ext,
        mime = mime,
        hash = hash,
        provider = provider,
        createdAt = createdAt,
        updatedAt = updatedAt,
        publishedAt = publishedAt,
        formats = formats.toDomain()
    )
}

@RequiresApi(Build.VERSION_CODES.O)
fun StrapiProfile.Data.Avatar.Formats.toDomain(): AvatarFormats {
    return AvatarFormats(
        thumbnail = if (thumbnail.url.isNotBlank()) {
            AvatarFormat(
                name = thumbnail.name,
                url = thumbnail.url,
                width = thumbnail.width,
                height = thumbnail.height,
                size = thumbnail.size,
                ext = thumbnail.ext,
                hash = thumbnail.hash,
                mime = thumbnail.mime,
                sizeInBytes = thumbnail.sizeInBytes
            )
        } else null,
        small = if (small.url.isNotBlank()) {
            AvatarFormat(
                name = small.name,
                url = small.url,
                width = small.width,
                height = small.height,
                size = small.size,
                ext = small.ext,
                hash = small.hash,
                mime = small.mime,
                sizeInBytes = small.sizeInBytes
            )
        } else null,
        medium = if (medium.url.isNotBlank()) {
            AvatarFormat(
                name = medium.name,
                url = medium.url,
                width = medium.width,
                height = medium.height,
                size = medium.size,
                ext = medium.ext,
                hash = medium.hash,
                mime = medium.mime,
                sizeInBytes = medium.sizeInBytes
            )
        } else null,
        large = if (large.url.isNotBlank()) {
            AvatarFormat(
                name = large.name,
                url = large.url,
                width = large.width,
                height = large.height,
                size = large.size,
                ext = large.ext,
                hash = large.hash,
                mime = large.mime,
                sizeInBytes = large.sizeInBytes
            )
        } else null
    )
}

@RequiresApi(Build.VERSION_CODES.O)
fun StrapiProfile.Data.Advert.toDomain(): UserAdvert {
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
fun StrapiUser.toDomain(): User {
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