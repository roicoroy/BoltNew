package com.example.boltnew.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileDao {
    
    @Query("SELECT * FROM profiles WHERE id = 1")
    fun getProfile(): Flow<ProfileEntity?>
    
    @Query("SELECT * FROM profiles WHERE id = :id")
    suspend fun getProfileById(id: Int): ProfileEntity?
    
    @Query("SELECT * FROM addresses WHERE profileId = :profileId")
    suspend fun getAddressesByProfileId(profileId: Int): List<AddressEntity>
    
    @Query("SELECT * FROM user_adverts WHERE profileId = :profileId")
    suspend fun getUserAdvertsByProfileId(profileId: Int): List<UserAdvertEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: ProfileEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAddresses(addresses: List<AddressEntity>)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserAdverts(userAdverts: List<UserAdvertEntity>)
    
    @Update
    suspend fun updateProfile(profile: ProfileEntity)
    
    @Delete
    suspend fun deleteProfile(profile: ProfileEntity)
    
    @Query("DELETE FROM profiles")
    suspend fun deleteAllProfiles()
    
    @Query("SELECT COUNT(*) FROM profiles")
    suspend fun getProfileCount(): Int
    
    @Query("UPDATE profiles SET avatarUrl = :avatarUrl WHERE id = 1")
    suspend fun updateAvatar(avatarUrl: String)
}