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
        username = data.user.username,
        email = data.user.email,
        dateOfBirth = if (data.dob.isNotBlank()) {
            LocalDate.parse(data.dob, dateFormatter)
        } else {
            LocalDate.of(1990, 1, 1)
        },
        addresses = data.addresses.map { it.toDomain() },
        avatar = if (data.avatar.url.isNotBlank()) {
            data.avatar.toDomain()
        } else null,
        userAdverts = data.adverts.map { it.toDomain() },
        role = null, // Role information not available in StrapiProfile
        isBlocked = data.user.blocked,
        isConfirmed = data.user.confirmed,
        provider = data.user.provider
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
        phoneNumber = phoneNumber
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
                size = thumbnail.size
            )
        } else null,
        small = if (small.url.isNotBlank()) {
            AvatarFormat(
                name = small.name,
                url = small.url,
                width = small.width,
                height = small.height,
                size = small.size
            )
        } else null,
        medium = if (medium.url.isNotBlank()) {
            AvatarFormat(
                name = medium.name,
                url = medium.url,
                width = medium.width,
                height = medium.height,
                size = medium.size
            )
        } else null,
        large = if (large.url.isNotBlank()) {
            AvatarFormat(
                name = large.name,
                url = large.url,
                width = large.width,
                height = large.height,
                size = large.size
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
        slug = slug
    )
}

@RequiresApi(Build.VERSION_CODES.O)
fun StrapiUser.toDomain(): User {
    return User(
        id = id,
        firstName = profile.documentId, // Using documentId as firstName since actual name not available
        lastName = username,
        email = email,
        address = "", // Address not directly available in StrapiUser
        dateOfBirth = if (profile.dob.isNotBlank()) {
            LocalDate.parse(profile.dob, dateFormatter)
        } else {
            LocalDate.of(1990, 1, 1)
        },
        avatarPath = null
    )
}