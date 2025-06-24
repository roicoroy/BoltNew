package com.example.boltnew.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.boltnew.data.model.*
import com.example.boltnew.data.database.AdvertDao
import com.example.boltnew.data.mapper.toDomain
import com.example.boltnew.data.mapper.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime

class AdvertRepositoryImpl(
    private val advertDao: AdvertDao
) : AdvertRepository {
    
    @RequiresApi(Build.VERSION_CODES.O)
    override fun getAllAdverts(): Flow<List<Advert>> {
        return advertDao.getAllAdverts().map { entities ->
            entities.toDomain()
        }
    }
    
    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getAdvertById(id: Int): Advert? {
        return advertDao.getAdvertById(id)?.toDomain()
    }
    
    @RequiresApi(Build.VERSION_CODES.O)
    override fun getAdvertsByCategory(categorySlug: String): Flow<List<Advert>> {
        return advertDao.getAdvertsByCategory(categorySlug).map { entities ->
            entities.toDomain()
        }
    }
    
    override fun getAllCategories(): Flow<List<String>> {
        return advertDao.getAllCategories()
    }
    
    @RequiresApi(Build.VERSION_CODES.O)
    override fun searchAdverts(query: String): Flow<List<Advert>> {
        return advertDao.searchAdverts(query).map { entities ->
            entities.toDomain()
        }
    }
    
    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun insertAdvert(advert: Advert) {
        advertDao.insertAdvert(advert.toEntity())
    }
    
    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun insertAdverts(adverts: List<Advert>) {
        advertDao.insertAdverts(adverts.toEntity())
    }
    
    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun updateAdvert(advert: Advert) {
        advertDao.updateAdvert(advert.toEntity())
    }
    
    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun deleteAdvert(advert: Advert) {
        advertDao.deleteAdvert(advert.toEntity())
    }
    
    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun initializeData() {
        val count = advertDao.getAdvertCount()
        if (count == 0) {
            val sampleAdverts = getSampleAdverts()
            insertAdverts(sampleAdverts)
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