package com.example.boltnew.data.mapper

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.boltnew.data.model.auth.profile.Address
import com.example.boltnew.data.model.auth.profile.Avatar
import com.example.boltnew.data.model.auth.profile.AvatarFormat
import com.example.boltnew.data.model.auth.profile.AvatarFormats
import com.example.boltnew.data.model.auth.profile.Profile
import com.example.boltnew.data.model.auth.profile.ProfileUser
import com.example.boltnew.data.model.auth.profile.StrapiProfile
import com.example.boltnew.data.model.auth.profile.UserAdvert
import com.example.boltnew.data.model.auth.user.StrapiUser
import com.example.boltnew.data.model.auth.user.User
import com.example.boltnew.data.model.auth.user.UserProfile
import com.example.boltnew.data.model.auth.user.UserRole
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE
@RequiresApi(Build.VERSION_CODES.O)
private val isoDateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME
@RequiresApi(Build.VERSION_CODES.O)
private val zonedDateTimeFormatter = DateTimeFormatter.ISO_ZONED_DATE_TIME

@RequiresApi(Build.VERSION_CODES.O)
private fun parseDateTime(dateString: String): String {
    return try {
        if (dateString.isBlank()) return ""
        
        // Try parsing as ISO instant (with Z timezone) and convert to local string
        val instant = java.time.Instant.parse(dateString)
        LocalDateTime.ofInstant(instant, java.time.ZoneOffset.UTC).toString()
    } catch (e: Exception) {
        try {
            // Try parsing as zoned date time
            ZonedDateTime.parse(dateString, zonedDateTimeFormatter).toLocalDateTime().toString()
        } catch (e2: Exception) {
            try {
                // Try parsing as local date time
                LocalDateTime.parse(dateString, isoDateTimeFormatter).toString()
            } catch (e3: Exception) {
                // Return original string if all parsing fails
                dateString
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun parseDate(dateString: String): String {
    return try {
        if (dateString.isBlank()) return ""
        
        // Try parsing as ISO instant first
        val instant = java.time.Instant.parse(dateString)
        LocalDate.ofInstant(instant, java.time.ZoneOffset.UTC).toString()
    } catch (e: Exception) {
        try {
            // Try parsing as date only
            LocalDate.parse(dateString, dateFormatter).toString()
        } catch (e2: Exception) {
            // Return original string if parsing fails
            dateString
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun StrapiProfile.toDomain(): Profile {
    println("Mapping StrapiProfile to Domain Profile")
    println("StrapiProfile data: ${this.data}")
    
    return Profile(
        id = data.id,
        documentId = data.documentId,
        dateOfBirth = parseDate(data.dob),
        createdAt = parseDateTime(data.createdAt),
        updatedAt = parseDateTime(data.updatedAt),
        publishedAt = parseDateTime(data.publishedAt),
        user = ProfileUser(
            id = data.user.id,
            documentId = data.user.documentId,
            username = data.user.username,
            email = data.user.email,
            blocked = data.user.blocked,
            confirmed = data.user.confirmed,
            provider = data.user.provider,
            createdAt = parseDateTime(data.user.createdAt),
            updatedAt = parseDateTime(data.user.updatedAt),
            publishedAt = parseDateTime(data.user.publishedAt)
        ),
        addresses = data.addresses.map { it.toDomain() },
        avatar = if (data.avatar.url.isNotBlank()) {
            data.avatar.toDomain()
        } else null,
        userAdverts = data.adverts.map { it.toDomain() }
    ).also {
        println("Mapped Profile: $it")
    }
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
        createdAt = parseDateTime(createdAt),
        updatedAt = parseDateTime(updatedAt),
        publishedAt = parseDateTime(publishedAt)
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
        createdAt = parseDateTime(createdAt),
        updatedAt = parseDateTime(updatedAt),
        publishedAt = parseDateTime(publishedAt),
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
        createdAt = parseDateTime(createdAt),
        updatedAt = parseDateTime(updatedAt),
        publishedAt = parseDateTime(publishedAt)
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
        createdAt = parseDateTime(createdAt),
        updatedAt = parseDateTime(updatedAt),
        publishedAt = parseDateTime(publishedAt),
        profile = UserProfile(
            id = profile.id,
            documentId = profile.documentId,
            dateOfBirth = parseDate(profile.dob),
            createdAt = parseDateTime(profile.createdAt),
            updatedAt = parseDateTime(profile.updatedAt),
            publishedAt = parseDateTime(profile.publishedAt)
        ),
        role = UserRole(
            id = role.id,
            documentId = role.documentId,
            name = role.name,
            description = role.description,
            type = role.type,
            createdAt = parseDateTime(role.createdAt),
            updatedAt = parseDateTime(role.updatedAt),
            publishedAt = parseDateTime(role.publishedAt)
        )
    )
}