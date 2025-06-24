package com.example.boltnew.data.repository

import com.example.boltnew.data.database.UserDao
import com.example.boltnew.data.mapper.toDomain
import com.example.boltnew.data.mapper.toEntity
import com.example.boltnew.data.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class UserRepositoryImpl(
    private val userDao: UserDao
) : UserRepository {
    
    override fun getUserProfile(): Flow<User?> {
        return userDao.getUserProfile().map { entity ->
            entity?.toDomain()
        }
    }
    
    override suspend fun getUserProfileSync(): User? {
        return userDao.getUserProfileSync()?.toDomain()
    }
    
    override suspend fun saveUserProfile(user: User) {
        userDao.insertUserProfile(user.toEntity())
    }
    
    override suspend fun updateUserProfile(user: User) {
        userDao.updateUserProfile(user.toEntity())
    }
    
    override suspend fun updateAvatarPath(avatarPath: String?) {
        userDao.updateAvatarPath(avatarPath)
    }
    
    override suspend fun initializeDefaultProfile() {
        val existingProfile = getUserProfileSync()
        if (existingProfile == null) {
            val defaultUser = User(
                firstName = "John",
                lastName = "Doe",
                email = "john.doe@example.com",
                address = "123 Main Street, City, State 12345",
                dateOfBirth = LocalDate.of(1990, 1, 1)
            )
            saveUserProfile(defaultUser)
        }
    }
}