package com.example.boltnew.data.mapper

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.boltnew.data.database.AdvertEntity
import com.example.boltnew.data.model.advert.Advert
import com.example.boltnew.data.model.advert.AdvertCategory
import com.example.boltnew.data.model.advert.AdvertCover
import com.example.boltnew.data.model.advert.AdvertCoverFormat
import com.example.boltnew.data.model.advert.AdvertCoverFormats
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
private val dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
@RequiresApi(Build.VERSION_CODES.O)
private val isoDateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME
@RequiresApi(Build.VERSION_CODES.O)
private val zonedDateTimeFormatter = DateTimeFormatter.ISO_ZONED_DATE_TIME

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
                try {
                    // Try parsing with our standard formatter
                    LocalDateTime.parse(dateString, dateTimeFormatter)
                } catch (e4: Exception) {
                    // Fallback to current time if all parsing fails
                    LocalDateTime.now()
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun AdvertEntity.toDomain(): Advert {
    return Advert(
        id = id,
        documentId = documentId,
        title = title,
        description = description,
        slug = slug,
        createdAt = parseDateTime(createdAt),
        updatedAt = parseDateTime(updatedAt),
        publishedAt = parseDateTime(publishedAt),
        cover = if (coverUrl != null) {
            AdvertCover(
                id = id, // Using advert id as cover id for simplicity
                documentId = documentId,
                name = "cover_$id.jpg",
                alternativeText = title,
                caption = null,
                width = 615,
                height = 424,
                url = coverUrl,
                formats = AdvertCoverFormats(
                    thumbnail = coverThumbnailUrl?.let {
                        AdvertCoverFormat(
                            name = "thumbnail",
                            width = 226,
                            height = 156,
                            size = 9.95,
                            url = it
                        )
                    },
                    small = coverSmallUrl?.let {
                        AdvertCoverFormat(
                            name = "small",
                            width = 500,
                            height = 345,
                            size = 43.42,
                            url = it
                        )
                    }
                )
            )
        } else null,
        category = AdvertCategory(
            id = categoryId,
            documentId = categorySlug,
            name = categoryName,
            slug = categorySlug,
            description = categoryDescription,
            createdAt = parseDateTime(createdAt),
            updatedAt = parseDateTime(updatedAt),
            publishedAt = parseDateTime(publishedAt)
        )
    )
}

@RequiresApi(Build.VERSION_CODES.O)
fun List<AdvertEntity>.toDomain(): List<Advert> {
    return map { it.toDomain() }
}

@RequiresApi(Build.VERSION_CODES.O)
fun Advert.toEntity(): AdvertEntity {
    return AdvertEntity(
        id = id,
        documentId = documentId,
        title = title,
        description = description,
        slug = slug,
        createdAt = createdAt.format(dateTimeFormatter),
        updatedAt = updatedAt.format(dateTimeFormatter),
        publishedAt = publishedAt.format(dateTimeFormatter),
        coverUrl = cover?.url,
        coverThumbnailUrl = cover?.formats?.thumbnail?.url,
        coverSmallUrl = cover?.formats?.small?.url,
        categoryId = category.id,
        categoryName = category.name,
        categorySlug = category.slug,
        categoryDescription = category.description
    )
}

@RequiresApi(Build.VERSION_CODES.O)
fun List<Advert>.toEntity(): List<AdvertEntity> {
    return map { it.toEntity() }
}