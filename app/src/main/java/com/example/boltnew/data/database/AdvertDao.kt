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
    
    // Additional methods for API integration
    @Query("SELECT * FROM adverts WHERE documentId = :documentId")
    suspend fun getAdvertByDocumentId(documentId: String): AdvertEntity?
    
    @Query("UPDATE adverts SET title = :title, description = :description, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateAdvertContent(id: Int, title: String, description: String, updatedAt: String)
    
    @Query("SELECT MAX(id) FROM adverts")
    suspend fun getMaxAdvertId(): Int?
    
    // Batch operations for better performance
    @Transaction
    suspend fun replaceAllAdverts(adverts: List<AdvertEntity>) {
        deleteAllAdverts()
        insertAdverts(adverts)
    }
    
    @Transaction
    suspend fun syncAdvertsFromApi(apiAdverts: List<AdvertEntity>) {
        // Get existing local adverts
        val existingIds = getAllAdvertIds()
        val apiIds = apiAdverts.map { it.id }
        
        // Delete adverts that no longer exist in API
        val toDelete = existingIds.filter { it !in apiIds }
        toDelete.forEach { id ->
            deleteAdvertById(id)
        }
        
        // Insert or update adverts from API
        insertAdverts(apiAdverts)
    }
    
    @Query("SELECT id FROM adverts")
    suspend fun getAllAdvertIds(): List<Int>
    
    @Query("DELETE FROM adverts WHERE id = :id")
    suspend fun deleteAdvertById(id: Int)
}