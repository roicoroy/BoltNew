package com.example.boltnew.data.mapper

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.boltnew.data.model.*
import com.example.boltnew.data.database.AdvertEntity
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

@RequiresApi(Build.VERSION_CODES.O)
fun AdvertEntity.toDomain(): Advert {
    return Advert(
        id = id,
        documentId = documentId,
        title = title,
        description = description,
        slug = slug,
        createdAt = LocalDateTime.parse(createdAt, dateFormatter),
        updatedAt = LocalDateTime.parse(updatedAt, dateFormatter),
        publishedAt = LocalDateTime.parse(publishedAt, dateFormatter),
        cover = if (coverUrl != null) {
            AdvertCover(
                id = 0, // Not stored in entity
                documentId = "", // Not stored in entity
                name = "",
                alternativeText = null,
                caption = null,
                width = 0,
                height = 0,
                url = coverUrl,
                formats = AdvertCoverFormats(
                    thumbnail = if (coverThumbnailUrl != null) {
                        AdvertCoverFormat(
                            name = "",
                            width = 0,
                            height = 0,
                            size = 0.0,
                            url = coverThumbnailUrl
                        )
                    } else null,
                    small = if (coverSmallUrl != null) {
                        AdvertCoverFormat(
                            name = "",
                            width = 0,
                            height = 0,
                            size = 0.0,
                            url = coverSmallUrl
                        )
                    } else null
                )
            )
        } else null,
        category = AdvertCategory(
            id = categoryId,
            documentId = "",
            name = categoryName,
            slug = categorySlug,
            description = categoryDescription,
            createdAt = LocalDateTime.parse(createdAt, dateFormatter),
            updatedAt = LocalDateTime.parse(updatedAt, dateFormatter),
            publishedAt = LocalDateTime.parse(publishedAt, dateFormatter)
        )
    )
}

@RequiresApi(Build.VERSION_CODES.O)
fun Advert.toEntity(): AdvertEntity {
    return AdvertEntity(
        id = id,
        documentId = documentId,
        title = title,
        description = description,
        slug = slug,
        createdAt = createdAt.format(dateFormatter),
        updatedAt = updatedAt.format(dateFormatter),
        publishedAt = publishedAt.format(dateFormatter),
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
fun List<AdvertEntity>.toDomain(): List<Advert> {
    return map { it.toDomain() }
}

@RequiresApi(Build.VERSION_CODES.O)
fun List<Advert>.toEntity(): List<AdvertEntity> {
    return map { it.toEntity() }
}