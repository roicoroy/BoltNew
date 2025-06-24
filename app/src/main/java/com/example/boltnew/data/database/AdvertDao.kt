package com.example.boltnew.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AdvertDao {
    
    @Query("SELECT * FROM adverts ORDER BY publishedAt DESC")
    fun getAllAdverts(): Flow<List<AdvertEntity>>
    
    @Query("SELECT * FROM adverts WHERE id = :id")
    suspend fun getAdvertById(id: Int): AdvertEntity?
    
    @Query("SELECT * FROM adverts WHERE categorySlug = :categorySlug ORDER BY publishedAt DESC")
    fun getAdvertsByCategory(categorySlug: String): Flow<List<AdvertEntity>>
    
    @Query("SELECT DISTINCT categoryName FROM adverts ORDER BY categoryName ASC")
    fun getAllCategories(): Flow<List<String>>
    
    @Query("SELECT * FROM adverts WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' ORDER BY publishedAt DESC")
    fun searchAdverts(query: String): Flow<List<AdvertEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAdvert(advert: AdvertEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAdverts(adverts: List<AdvertEntity>)
    
    @Update
    suspend fun updateAdvert(advert: AdvertEntity)
    
    @Delete
    suspend fun deleteAdvert(advert: AdvertEntity)
    
    @Query("DELETE FROM adverts")
    suspend fun deleteAllAdverts()
    
    @Query("SELECT COUNT(*) FROM adverts")
    suspend fun getAdvertCount(): Int
}