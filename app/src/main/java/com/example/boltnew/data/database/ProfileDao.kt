package com.example.boltnew.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileDao {
    
    @Query("SELECT * FROM profile WHERE id = 1")
    fun getProfile(): Flow<ProfileEntity?>
    
    @Query("SELECT * FROM profile WHERE id = 1")
    suspend fun getProfileSync(): ProfileEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: ProfileEntity)
    
    @Update
    suspend fun updateProfile(profile: ProfileEntity)
    
    @Query("UPDATE profile SET avatarUrl = :avatarUrl, avatarThumbnailUrl = :thumbnailUrl WHERE id = 1")
    suspend fun updateAvatar(avatarUrl: String?, thumbnailUrl: String?)
    
    // Address operations
    @Query("SELECT * FROM address WHERE profileId = :profileId ORDER BY id ASC")
    fun getAddressesByProfileId(profileId: Int): Flow<List<AddressEntity>>
    
    @Query("SELECT * FROM address WHERE profileId = :profileId ORDER BY id ASC")
    suspend fun getAddressesByProfileIdSync(profileId: Int): List<AddressEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAddress(address: AddressEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAddresses(addresses: List<AddressEntity>)
    
    @Update
    suspend fun updateAddress(address: AddressEntity)
    
    @Delete
    suspend fun deleteAddress(address: AddressEntity)
    
    @Query("DELETE FROM address WHERE profileId = :profileId")
    suspend fun deleteAddressesByProfileId(profileId: Int)
    
    // User Advert operations
    @Query("SELECT * FROM user_advert WHERE profileId = :profileId ORDER BY publishedAt DESC")
    fun getUserAdvertsByProfileId(profileId: Int): Flow<List<UserAdvertEntity>>
    
    @Query("SELECT * FROM user_advert WHERE profileId = :profileId ORDER BY publishedAt DESC")
    suspend fun getUserAdvertsByProfileIdSync(profileId: Int): List<UserAdvertEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserAdvert(userAdvert: UserAdvertEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserAdverts(userAdverts: List<UserAdvertEntity>)
    
    @Update
    suspend fun updateUserAdvert(userAdvert: UserAdvertEntity)
    
    @Delete
    suspend fun deleteUserAdvert(userAdvert: UserAdvertEntity)
    
    @Query("DELETE FROM user_advert WHERE profileId = :profileId")
    suspend fun deleteUserAdvertsByProfileId(profileId: Int)
}