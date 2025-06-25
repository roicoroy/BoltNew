package com.example.boltnew.data.mapper

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.boltnew.data.model.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
private val dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

@RequiresApi(Build.VERSION_CODES.O)
fun StrapiAdvert.toDomain(): Advert {
    return Advert(
        id = id,
        documentId = documentId,
        title = title,
        description = description,
        slug = slug,
        createdAt = LocalDateTime.parse(createdAt, dateTimeFormatter),
        updatedAt = LocalDateTime.parse(updatedAt, dateTimeFormatter),
        publishedAt = LocalDateTime.parse(publishedAt, dateTimeFormatter),
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
        createdAt = LocalDateTime.parse(createdAt, dateTimeFormatter),
        updatedAt = LocalDateTime.parse(updatedAt, dateTimeFormatter),
        publishedAt = LocalDateTime.parse(publishedAt, dateTimeFormatter)
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
            category = category.id
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
            category = category.id
        )
    )
}