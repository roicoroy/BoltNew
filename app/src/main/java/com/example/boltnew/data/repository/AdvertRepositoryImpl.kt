package com.example.boltnew.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.boltnew.data.database.AdvertDao
import com.example.boltnew.data.mapper.toDomain
import com.example.boltnew.data.mapper.toEntity
import com.example.boltnew.data.mapper.toStrapiCreateRequest
import com.example.boltnew.data.mapper.toStrapiUpdateRequest
import com.example.boltnew.data.model.advert.Advert
import com.example.boltnew.data.model.advert.AdvertCategory
import com.example.boltnew.data.model.advert.AdvertCover
import com.example.boltnew.data.model.advert.AdvertCoverFormat
import com.example.boltnew.data.model.advert.AdvertCoverFormats
import com.example.boltnew.data.network.AdvertApiService
import com.example.boltnew.data.network.TokenManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.LocalDateTime

class AdvertRepositoryImpl(
    private val advertDao: AdvertDao,
    private val apiService: AdvertApiService,
    private val tokenManager: TokenManager
) : AdvertRepository {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun getAllAdverts(): Flow<List<Advert>> {
        return flow {
            try {
                // Try to fetch from API first
                val apiResult = apiService.getAllAdverts()
                if (apiResult.isSuccess) {
                    val strapiAdverts = apiResult.getOrNull()?.data ?: emptyList()
                    val domainAdverts = strapiAdverts.toDomain()

                    // Update local cache
                    advertDao.deleteAllAdverts()
                    advertDao.insertAdverts(domainAdverts.toEntity())

                    emit(domainAdverts)
                } else {
                    // If API fails, fall back to local data
                    val localAdverts = advertDao.getAllAdverts()
                    localAdverts.collect { entities ->
                        emit(entities.toDomain())
                    }
                }
            } catch (e: Exception) {
                // If API fails, fall back to local data
                val localAdverts = advertDao.getAllAdverts()
                localAdverts.collect { entities ->
                    emit(entities.toDomain())
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getAdvertById(id: Int): Advert? {
        // Try to get from API first, then fall back to local
        try {
            val apiResult = apiService.getAdvertById(id)
            if (apiResult.isSuccess) {
                val strapiAdvert = apiResult.getOrNull()?.data
                strapiAdvert?.let {
                    val domainAdvert = it.toDomain()
                    // Cache in local database
                    advertDao.insertAdvert(domainAdvert.toEntity())
                    return domainAdvert
                }
            }
        } catch (e: Exception) {
            // If API fails, fall back to local data
        }

        // Fall back to local database
        return advertDao.getAdvertById(id)?.toDomain()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun getAdvertsByCategory(categorySlug: String): Flow<List<Advert>> {
        return flow {
            try {
                // Try to fetch from API first
                val apiResult = apiService.getAdvertsByCategory(categorySlug)
                if (apiResult.isSuccess) {
                    val strapiAdverts = apiResult.getOrNull()?.data ?: emptyList()
                    val domainAdverts = strapiAdverts.toDomain()
                    emit(domainAdverts)
                } else {
                    // If API fails, fall back to local data
                    val localAdverts = advertDao.getAdvertsByCategory(categorySlug)
                    localAdverts.collect { entities ->
                        emit(entities.toDomain())
                    }
                }
            } catch (e: Exception) {
                // If API fails, fall back to local data
                val localAdverts = advertDao.getAdvertsByCategory(categorySlug)
                localAdverts.collect { entities ->
                    emit(entities.toDomain())
                }
            }
        }
    }

    override fun getAllCategories(): Flow<List<String>> {
        return flow {
            try {
                // Try to fetch from API first
                val apiResult = apiService.getCategories()
                if (apiResult.isSuccess) {
                    val categories = apiResult.getOrNull() ?: emptyList()
                    emit(categories)
                } else {
                    // If API fails, fall back to local data
                    val localCategories = advertDao.getAllCategories()
                    localCategories.collect { categories ->
                        emit(categories)
                    }
                }
            } catch (e: Exception) {
                // If API fails, fall back to local data
                val localCategories = advertDao.getAllCategories()
                localCategories.collect { categories ->
                    emit(categories)
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun searchAdverts(query: String): Flow<List<Advert>> {
        return flow {
            try {
                // Try to search via API first
                val apiResult = apiService.searchAdverts(query)
                if (apiResult.isSuccess) {
                    val strapiAdverts = apiResult.getOrNull()?.data ?: emptyList()
                    val domainAdverts = strapiAdverts.toDomain()
                    emit(domainAdverts)
                } else {
                    // If API fails, fall back to local search
                    val localAdverts = advertDao.searchAdverts(query)
                    localAdverts.collect { entities ->
                        emit(entities.toDomain())
                    }
                }
            } catch (e: Exception) {
                // If API fails, fall back to local search
                val localAdverts = advertDao.searchAdverts(query)
                localAdverts.collect { entities ->
                    emit(entities.toDomain())
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun insertAdvert(advert: Advert) {
        try {
            // Create advert via API first
            val token = tokenManager.getToken()
            val createRequest = advert.toStrapiCreateRequest()
            val apiResult = apiService.createAdvert(createRequest, token.toString())

            if (apiResult.isSuccess) {
                val createdAdvert = apiResult.getOrNull()?.data
                createdAdvert?.let {
                    // Convert AdvertCreateData to domain model
                    val domainAdvert = Advert(
                        id = it.id,
                        documentId = it.documentId,
                        title = it.title,
                        description = it.description,
                        slug = it.slug,
                        createdAt = LocalDateTime.parse(it.createdAt.replace("Z", "")),
                        updatedAt = LocalDateTime.parse(it.updatedAt.replace("Z", "")),
                        publishedAt = LocalDateTime.parse(it.publishedAt.replace("Z", "")),
                        cover = it.cover?.let { cover ->
                            AdvertCover(
                                id = cover.id,
                                documentId = cover.documentId,
                                name = cover.name,
                                alternativeText = cover.alternativeText,
                                caption = cover.caption,
                                width = cover.width,
                                height = cover.height,
                                url = cover.url,
                                formats = null // Simplified for now
                            )
                        },
                        category = it.category?.let { category ->
                            AdvertCategory(
                                id = category.id,
                                documentId = category.documentId,
                                name = category.name,
                                slug = category.slug,
                                description = category.description,
                                createdAt = LocalDateTime.now(),
                                updatedAt = LocalDateTime.now(),
                                publishedAt = LocalDateTime.now()
                            )
                        } ?: AdvertCategory(
                            id = 0,
                            documentId = "",
                            name = "Unknown",
                            slug = "unknown",
                            description = "",
                            createdAt = LocalDateTime.now(),
                            updatedAt = LocalDateTime.now(),
                            publishedAt = LocalDateTime.now()
                        )
                    )
                    
                    // Cache in local database
                    advertDao.insertAdvert(domainAdvert.toEntity())
                }
            } else {
                // If API fails, still save locally
                advertDao.insertAdvert(advert.toEntity())
            }
        } catch (e: Exception) {
            // If API fails, still save locally
            advertDao.insertAdvert(advert.toEntity())
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun insertAdverts(adverts: List<Advert>) {
        // For bulk insert, prioritize local storage
        advertDao.insertAdverts(adverts.toEntity())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun updateAdvert(advert: Advert) {
        try {
            // Update via API first
            val token = tokenManager.getToken()
            val updateRequest = advert.toStrapiUpdateRequest()
            val apiResult = apiService.updateAdvert(advert.id, updateRequest, token.toString())

            if (apiResult.isSuccess) {
                val updatedAdvert = apiResult.getOrNull()?.data
                updatedAdvert?.let {
                    // Convert AdvertCreateData to domain model
                    val domainAdvert = Advert(
                        id = it.id,
                        documentId = it.documentId,
                        title = it.title,
                        description = it.description,
                        slug = it.slug,
                        createdAt = LocalDateTime.parse(it.createdAt.replace("Z", "")),
                        updatedAt = LocalDateTime.parse(it.updatedAt.replace("Z", "")),
                        publishedAt = LocalDateTime.parse(it.publishedAt.replace("Z", "")),
                        cover = it.cover?.let { cover ->
                            AdvertCover(
                                id = cover.id,
                                documentId = cover.documentId,
                                name = cover.name,
                                alternativeText = cover.alternativeText,
                                caption = cover.caption,
                                width = cover.width,
                                height = cover.height,
                                url = cover.url,
                                formats = null // Simplified for now
                            )
                        },
                        category = it.category?.let { category ->
                            AdvertCategory(
                                id = category.id,
                                documentId = category.documentId,
                                name = category.name,
                                slug = category.slug,
                                description = category.description,
                                createdAt = LocalDateTime.now(),
                                updatedAt = LocalDateTime.now(),
                                publishedAt = LocalDateTime.now()
                            )
                        } ?: AdvertCategory(
                            id = 0,
                            documentId = "",
                            name = "Unknown",
                            slug = "unknown",
                            description = "",
                            createdAt = LocalDateTime.now(),
                            updatedAt = LocalDateTime.now(),
                            publishedAt = LocalDateTime.now()
                        )
                    )
                    
                    // Update local database
                    advertDao.updateAdvert(domainAdvert.toEntity())
                }
            } else {
                // If API fails, still update locally
                advertDao.updateAdvert(advert.toEntity())
            }
        } catch (e: Exception) {
            // If API fails, still update locally
            advertDao.updateAdvert(advert.toEntity())
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun deleteAdvert(advert: Advert) {
        try {
            // Delete via API first
            val token = tokenManager.getToken()

            val apiResult = apiService.deleteAdvert(advert.id, token.toString())

            if (apiResult.isSuccess) {
                // Delete from local database
                advertDao.deleteAdvert(advert.toEntity())
            }
        } catch (e: Exception) {
            // If API fails, still delete locally
            advertDao.deleteAdvert(advert.toEntity())
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun initializeData() {
        // Always try to fetch from API first
        try {
            val apiResult = apiService.getAllAdverts()
            if (apiResult.isSuccess) {
                val strapiAdverts = apiResult.getOrNull()?.data ?: emptyList()
                if (strapiAdverts.isNotEmpty()) {
                    val domainAdverts = strapiAdverts.toDomain()
                    insertAdverts(domainAdverts)
                    return
                }
            }
        } catch (e: Exception) {
            println("EEEEE :::: $e")

            // If API fails, check if we have local data
            val count = advertDao.getAdvertCount()
            if (count == 0) {
                val sampleAdverts = getSampleAdverts()
                insertAdverts(sampleAdverts)
            }
        }

        // Only use sample data if no API data and no local data

    }

    // New method to refresh data from API
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun refreshFromApi(): Boolean {
        return try {
            val apiResult = apiService.getAllAdverts()
            if (apiResult.isSuccess) {
                val strapiAdverts = apiResult.getOrNull()?.data ?: emptyList()
                val domainAdverts = strapiAdverts.toDomain()

                // Clear existing data and insert fresh data
                advertDao.deleteAllAdverts()
                advertDao.insertAdverts(domainAdverts.toEntity())
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getSampleAdverts(): List<Advert> {
        val now = LocalDateTime.now()

        return listOf(
            Advert(
                id = 1,
                documentId = "sample1",
                title = "Professional Physiotherapy Services",
                description = "Expert physiotherapy treatment for sports injuries, chronic pain, and rehabilitation. Our certified physiotherapists provide personalized treatment plans to help you recover faster and prevent future injuries.",
                slug = "professional-physiotherapy-services",
                createdAt = now.minusDays(5),
                updatedAt = now.minusDays(1),
                publishedAt = now.minusDays(1),
                cover = AdvertCover(
                    id = 1,
                    documentId = "cover1",
                    name = "physiotherapy.jpg",
                    alternativeText = "Physiotherapy session",
                    caption = null,
                    width = 615,
                    height = 424,
                    url = "https://images.pexels.com/photos/7176026/pexels-photo-7176026.jpeg",
                    formats = AdvertCoverFormats(
                        thumbnail = AdvertCoverFormat(
                            name = "thumbnail_physiotherapy.jpg",
                            width = 226,
                            height = 156,
                            size = 9.95,
                            url = "https://images.pexels.com/photos/7176026/pexels-photo-7176026.jpeg?auto=compress&cs=tinysrgb&w=300"
                        ),
                        small = AdvertCoverFormat(
                            name = "small_physiotherapy.jpg",
                            width = 500,
                            height = 345,
                            size = 43.42,
                            url = "https://images.pexels.com/photos/7176026/pexels-photo-7176026.jpeg?auto=compress&cs=tinysrgb&w=500"
                        )
                    )
                ),
                category = AdvertCategory(
                    id = 1,
                    documentId = "therapy-cat",
                    name = "Therapy",
                    slug = "therapy",
                    description = "Professional therapy services",
                    createdAt = now.minusDays(10),
                    updatedAt = now.minusDays(5),
                    publishedAt = now.minusDays(5)
                )
            ),
            Advert(
                id = 2,
                documentId = "sample2",
                title = "Mental Health Counseling",
                description = "Professional mental health counseling services for anxiety, depression, and stress management. Our licensed therapists provide a safe and supportive environment for your healing journey.",
                slug = "mental-health-counseling",
                createdAt = now.minusDays(4),
                updatedAt = now.minusDays(2),
                publishedAt = now.minusDays(2),
                cover = AdvertCover(
                    id = 2,
                    documentId = "cover2",
                    name = "counseling.jpg",
                    alternativeText = "Counseling session",
                    caption = null,
                    width = 615,
                    height = 424,
                    url = "https://images.pexels.com/photos/7176319/pexels-photo-7176319.jpeg",
                    formats = AdvertCoverFormats(
                        thumbnail = AdvertCoverFormat(
                            name = "thumbnail_counseling.jpg",
                            width = 226,
                            height = 156,
                            size = 9.95,
                            url = "https://images.pexels.com/photos/7176319/pexels-photo-7176319.jpeg?auto=compress&cs=tinysrgb&w=300"
                        ),
                        small = AdvertCoverFormat(
                            name = "small_counseling.jpg",
                            width = 500,
                            height = 345,
                            size = 43.42,
                            url = "https://images.pexels.com/photos/7176319/pexels-photo-7176319.jpeg?auto=compress&cs=tinysrgb&w=500"
                        )
                    )
                ),
                category = AdvertCategory(
                    id = 1,
                    documentId = "therapy-cat",
                    name = "Therapy",
                    slug = "therapy",
                    description = "Professional therapy services",
                    createdAt = now.minusDays(10),
                    updatedAt = now.minusDays(5),
                    publishedAt = now.minusDays(5)
                )
            ),
            Advert(
                id = 3,
                documentId = "sample3",
                title = "Massage Therapy Services",
                description = "Relaxing and therapeutic massage services including Swedish massage, deep tissue massage, and sports massage. Perfect for stress relief and muscle recovery.",
                slug = "massage-therapy-services",
                createdAt = now.minusDays(3),
                updatedAt = now.minusHours(12),
                publishedAt = now.minusHours(12),
                cover = AdvertCover(
                    id = 3,
                    documentId = "cover3",
                    name = "massage.jpg",
                    alternativeText = "Massage therapy",
                    caption = null,
                    width = 615,
                    height = 424,
                    url = "https://images.pexels.com/photos/3757942/pexels-photo-3757942.jpeg",
                    formats = AdvertCoverFormats(
                        thumbnail = AdvertCoverFormat(
                            name = "thumbnail_massage.jpg",
                            width = 226,
                            height = 156,
                            size = 9.95,
                            url = "https://images.pexels.com/photos/3757942/pexels-photo-3757942.jpeg?auto=compress&cs=tinysrgb&w=300"
                        ),
                        small = AdvertCoverFormat(
                            name = "small_massage.jpg",
                            width = 500,
                            height = 345,
                            size = 43.42,
                            url = "https://images.pexels.com/photos/3757942/pexels-photo-3757942.jpeg?auto=compress&cs=tinysrgb&w=500"
                        )
                    )
                ),
                category = AdvertCategory(
                    id = 2,
                    documentId = "wellness-cat",
                    name = "Wellness",
                    slug = "wellness",
                    description = "Health and wellness services",
                    createdAt = now.minusDays(8),
                    updatedAt = now.minusDays(3),
                    publishedAt = now.minusDays(3)
                )
            ),
            Advert(
                id = 4,
                documentId = "sample4",
                title = "Yoga Classes for All Levels",
                description = "Join our yoga classes suitable for beginners to advanced practitioners. Improve flexibility, strength, and mental well-being in a peaceful environment with certified instructors.",
                slug = "yoga-classes-all-levels",
                createdAt = now.minusDays(2),
                updatedAt = now.minusHours(6),
                publishedAt = now.minusHours(6),
                cover = AdvertCover(
                    id = 4,
                    documentId = "cover4",
                    name = "yoga.jpg",
                    alternativeText = "Yoga class",
                    caption = null,
                    width = 615,
                    height = 424,
                    url = "https://images.pexels.com/photos/3822622/pexels-photo-3822622.jpeg",
                    formats = AdvertCoverFormats(
                        thumbnail = AdvertCoverFormat(
                            name = "thumbnail_yoga.jpg",
                            width = 226,
                            height = 156,
                            size = 9.95,
                            url = "https://images.pexels.com/photos/3822622/pexels-photo-3822622.jpeg?auto=compress&cs=tinysrgb&w=300"
                        ),
                        small = AdvertCoverFormat(
                            name = "small_yoga.jpg",
                            width = 500,
                            height = 345,
                            size = 43.42,
                            url = "https://images.pexels.com/photos/3822622/pexels-photo-3822622.jpeg?auto=compress&cs=tinysrgb&w=500"
                        )
                    )
                ),
                category = AdvertCategory(
                    id = 2,
                    documentId = "wellness-cat",
                    name = "Wellness",
                    slug = "wellness",
                    description = "Health and wellness services",
                    createdAt = now.minusDays(8),
                    updatedAt = now.minusDays(3),
                    publishedAt = now.minusDays(3)
                )
            ),
            Advert(
                id = 5,
                documentId = "sample5",
                title = "Personal Training Sessions",
                description = "One-on-one personal training sessions with certified fitness trainers. Customized workout plans to help you achieve your fitness goals safely and effectively.",
                slug = "personal-training-sessions",
                createdAt = now.minusDays(1),
                updatedAt = now.minusHours(3),
                publishedAt = now.minusHours(3),
                cover = AdvertCover(
                    id = 5,
                    documentId = "cover5",
                    name = "training.jpg",
                    alternativeText = "Personal training",
                    caption = null,
                    width = 615,
                    height = 424,
                    url = "https://images.pexels.com/photos/1552242/pexels-photo-1552242.jpeg",
                    formats = AdvertCoverFormats(
                        thumbnail = AdvertCoverFormat(
                            name = "thumbnail_training.jpg",
                            width = 226,
                            height = 156,
                            size = 9.95,
                            url = "https://images.pexels.com/photos/1552242/pexels-photo-1552242.jpeg?auto=compress&cs=tinysrgb&w=300"
                        ),
                        small = AdvertCoverFormat(
                            name = "small_training.jpg",
                            width = 500,
                            height = 345,
                            size = 43.42,
                            url = "https://images.pexels.com/photos/1552242/pexels-photo-1552242.jpeg?auto=compress&cs=tinysrgb&w=500"
                        )
                    )
                ),
                category = AdvertCategory(
                    id = 3,
                    documentId = "fitness-cat",
                    name = "Fitness",
                    slug = "fitness",
                    description = "Fitness and training services",
                    createdAt = now.minusDays(6),
                    updatedAt = now.minusDays(2),
                    publishedAt = now.minusDays(2)
                )
            ),
            Advert(
                id = 6,
                documentId = "sample6",
                title = "Nutritional Counseling",
                description = "Professional nutritional counseling to help you develop healthy eating habits. Our registered dietitians create personalized meal plans based on your health goals and dietary preferences.",
                slug = "nutritional-counseling",
                createdAt = now.minusHours(18),
                updatedAt = now.minusHours(2),
                publishedAt = now.minusHours(2),
                cover = AdvertCover(
                    id = 6,
                    documentId = "cover6",
                    name = "nutrition.jpg",
                    alternativeText = "Nutritional counseling",
                    caption = null,
                    width = 615,
                    height = 424,
                    url = "https://images.pexels.com/photos/1640777/pexels-photo-1640777.jpeg",
                    formats = AdvertCoverFormats(
                        thumbnail = AdvertCoverFormat(
                            name = "thumbnail_nutrition.jpg",
                            width = 226,
                            height = 156,
                            size = 9.95,
                            url = "https://images.pexels.com/photos/1640777/pexels-photo-1640777.jpeg?auto=compress&cs=tinysrgb&w=300"
                        ),
                        small = AdvertCoverFormat(
                            name = "small_nutrition.jpg",
                            width = 500,
                            height = 345,
                            size = 43.42,
                            url = "https://images.pexels.com/photos/1640777/pexels-photo-1640777.jpeg?auto=compress&cs=tinysrgb&w=500"
                        )
                    )
                ),
                category = AdvertCategory(
                    id = 4,
                    documentId = "health-cat",
                    name = "Health",
                    slug = "health",
                    description = "Health and medical services",
                    createdAt = now.minusDays(7),
                    updatedAt = now.minusDays(1),
                    publishedAt = now.minusDays(1)
                )
            )
        )
    }
}