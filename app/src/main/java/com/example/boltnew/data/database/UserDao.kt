package com.example.boltnew.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    
    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getUserProfile(): Flow<UserEntity?>
    
    @Query("SELECT * FROM user_profile WHERE id = 1")
    suspend fun getUserProfileSync(): UserEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfile(user: UserEntity)
    
    @Update
    suspend fun updateUserProfile(user: UserEntity)
    
    @Query("UPDATE user_profile SET avatarPath = :avatarPath WHERE id = 1")
    suspend fun updateAvatarPath(avatarPath: String?)
}