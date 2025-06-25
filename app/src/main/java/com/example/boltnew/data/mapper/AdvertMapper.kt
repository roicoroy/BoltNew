package com.example.boltnew.data.mapper

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.boltnew.data.database.AdvertEntity
import com.example.boltnew.data.model.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
private val dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

@RequiresApi(Build.VERSION_CODES.O)
fun AdvertEntity.toDomain(): Advert {
    return Advert(
        id = id,
        documentId = documentId,
        title = title,
        description = description,
        slug = slug,
        createdAt = LocalDateTime.parse(createdAt, dateTimeFormatter),
        updatedAt = LocalDateTime.parse(updatedAt, dateTimeFormatter),
        publishedAt = LocalDateTime.parse(publishedAt, dateTimeFormatter),
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
            createdAt = LocalDateTime.parse(createdAt, dateTimeFormatter),
            updatedAt = LocalDateTime.parse(updatedAt, dateTimeFormatter),
            publishedAt = LocalDateTime.parse(publishedAt, dateTimeFormatter)
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