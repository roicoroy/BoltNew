package com.example.boltnew.data.mapper

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.boltnew.data.model.advert.Advert
import com.example.boltnew.data.model.advert.AdvertCategory
import com.example.boltnew.data.model.advert.AdvertCover
import com.example.boltnew.data.model.advert.AdvertCoverFormat
import com.example.boltnew.data.model.advert.AdvertCoverFormats
import com.example.boltnew.data.model.advert.StrapiAdvert
import com.example.boltnew.data.model.advert.StrapiAdvertCreateData
import com.example.boltnew.data.model.advert.StrapiAdvertCreateRequest
import com.example.boltnew.data.model.advert.StrapiAdvertUpdateData
import com.example.boltnew.data.model.advert.StrapiAdvertUpdateRequest
import com.example.boltnew.data.model.advert.StrapiCategory
import com.example.boltnew.data.model.advert.StrapiCover
import com.example.boltnew.data.model.advert.StrapiCoverFormat
import com.example.boltnew.data.model.advert.StrapiCoverFormats
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
private val isoDateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME
@RequiresApi(Build.VERSION_CODES.O)
private val zonedDateTimeFormatter = DateTimeFormatter.ISO_ZONED_DATE_TIME
@RequiresApi(Build.VERSION_CODES.O)
private val instantFormatter = DateTimeFormatter.ISO_INSTANT

@RequiresApi(Build.VERSION_CODES.O)
private fun parseDateTime(dateString: String): LocalDateTime {
    return try {
        // Try parsing as ISO instant (with Z timezone)
        val instant = java.time.Instant.parse(dateString)
        LocalDateTime.ofInstant(instant, java.time.ZoneOffset.UTC)
    } catch (e: Exception) {
        try {
            // Try parsing as zoned date time
            ZonedDateTime.parse(dateString, zonedDateTimeFormatter).toLocalDateTime()
        } catch (e2: Exception) {
            try {
                // Try parsing as local date time
                LocalDateTime.parse(dateString, isoDateTimeFormatter)
            } catch (e3: Exception) {
                // Fallback to current time if all parsing fails
                LocalDateTime.now()
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun StrapiAdvert.toDomain(): Advert {
    return Advert(
        id = id,
        documentId = documentId,
        title = title,
        description = description,
        slug = slug,
        createdAt = parseDateTime(createdAt),
        updatedAt = parseDateTime(updatedAt),
        publishedAt = parseDateTime(publishedAt),
        cover = cover?.toDomain(),
        category = category.toDomain()
    )
}

@RequiresApi(Build.VERSION_CODES.O)
fun StrapiCover.toDomain(): AdvertCover {
    return AdvertCover(
        id = id,
        documentId = documentId,
        name = name,
        alternativeText = alternativeText,
        caption = caption,
        width = width,
        height = height,
        url = url,
        formats = formats?.toDomain()
    )
}

@RequiresApi(Build.VERSION_CODES.O)
fun StrapiCoverFormats.toDomain(): AdvertCoverFormats {
    return AdvertCoverFormats(
        thumbnail = thumbnail?.toDomain(),
        small = small?.toDomain()
    )
}

@RequiresApi(Build.VERSION_CODES.O)
fun StrapiCoverFormat.toDomain(): AdvertCoverFormat {
    return AdvertCoverFormat(
        name = name,
        width = width,
        height = height,
        size = size,
        url = url
    )
}

@RequiresApi(Build.VERSION_CODES.O)
fun StrapiCategory.toDomain(): AdvertCategory {
    return AdvertCategory(
        id = id,
        documentId = documentId,
        name = name,
        slug = slug,
        description = description,
        createdAt = parseDateTime(createdAt),
        updatedAt = parseDateTime(updatedAt),
        publishedAt = parseDateTime(publishedAt)
    )
}

@RequiresApi(Build.VERSION_CODES.O)
fun List<StrapiAdvert>.toDomain(): List<Advert> {
    return map { it.toDomain() }
}

// Convert domain models back to Strapi format for API calls
@RequiresApi(Build.VERSION_CODES.O)
fun Advert.toStrapiCreateRequest(): StrapiAdvertCreateRequest {
    return StrapiAdvertCreateRequest(
        data = StrapiAdvertCreateData(
            title = title,
            description = description,
            slug = slug,
            category = listOf(category.id.toString())
        )
    )
}

@RequiresApi(Build.VERSION_CODES.O)
fun Advert.toStrapiUpdateRequest(): StrapiAdvertUpdateRequest {
    return StrapiAdvertUpdateRequest(
        data = StrapiAdvertUpdateData(
            title = title,
            description = description,
            slug = slug,
            category = listOf(category.id.toString())
        )
    )
}