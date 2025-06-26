package com.example.boltnew.data.repository.advert

import com.example.boltnew.data.model.advert.Advert
import kotlinx.coroutines.flow.Flow

interface AdvertRepository {
    fun getAllAdverts(): Flow<List<Advert>>
    suspend fun getAdvertById(id: Int): Advert?
    fun getAdvertsByCategory(categorySlug: String): Flow<List<Advert>>
    fun getAllCategories(): Flow<List<String>>
    fun searchAdverts(query: String): Flow<List<Advert>>
    suspend fun insertAdvert(advert: Advert)
    suspend fun insertAdverts(adverts: List<Advert>)
    suspend fun updateAdvert(advert: Advert)
    suspend fun deleteAdvert(advert: Advert)
    suspend fun initializeData()
}