package com.example.boltnew.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.boltnew.data.database.ProfileDao
import com.example.boltnew.data.mapper.toDomain
import com.example.boltnew.data.mapper.toEntity
import com.example.boltnew.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
class ProfileRepositoryImpl(
    private val profileDao: ProfileDao
) : ProfileRepository {
    
    override fun getProfile(): Flow<Profile?> {
        return profileDao.getProfile().map { profileEntity ->
            profileEntity?.let { entity ->
                val addresses = profileDao.getAddressesByProfileId(entity.id)
                val userAdverts = profileDao.getUserAdvertsByProfileId(entity.id)
                entity.toDomain(addresses, userAdverts)
            }
        }
    }
    
    override suspend fun getProfileById(id: Int): Profile? {
        val profileEntity = profileDao.getProfileById(id) ?: return null
        val addresses = profileDao.getAddressesByProfileId(profileEntity.id)
        val userAdverts = profileDao.getUserAdvertsByProfileId(profileEntity.id)
        return profileEntity.toDomain(addresses, userAdverts)
    }
    
    override suspend fun insertProfile(profile: Profile) {
        val profileEntity = profile.toEntity()
        profileDao.insertProfile(profileEntity)
        
        if (profile.addresses.isNotEmpty()) {
            val addressEntities = profile.addresses.map { it.toEntity(profile.id) }
            profileDao.insertAddresses(addressEntities)
        }
        
        if (profile.userAdverts.isNotEmpty()) {
            val userAdvertEntities = profile.userAdverts.map { it.toEntity(profile.id) }
            profileDao.insertUserAdverts(userAdvertEntities)
        }
    }
    
    override suspend fun updateProfile(profile: Profile) {
        val profileEntity = profile.toEntity()
        profileDao.updateProfile(profileEntity)
        
        if (profile.addresses.isNotEmpty()) {
            val addressEntities = profile.addresses.map { it.toEntity(profile.id) }
            profileDao.insertAddresses(addressEntities)
        }
        
        if (profile.userAdverts.isNotEmpty()) {
            val userAdvertEntities = profile.userAdverts.map { it.toEntity(profile.id) }
            profileDao.insertUserAdverts(userAdvertEntities)
        }
    }
    
    override suspend fun updateAvatar(avatarUrl: String, avatarPath: String) {
        profileDao.updateAvatar(avatarUrl)
    }
    
    override suspend fun deleteProfile(profile: Profile) {
        val profileEntity = profile.toEntity()
        profileDao.deleteProfile(profileEntity)
    }
    
    override suspend fun initializeDefaultProfile() {
        val count = profileDao.getProfileCount()
        if (count == 0) {
            val defaultProfile = getDefaultProfile()
            insertProfile(defaultProfile)
        }
    }
    
    private fun getDefaultProfile(): Profile {
        return Profile(
            id = 1,
            documentId = "default-profile",
            username = "john_doe",
            email = "john.doe@example.com",
            dateOfBirth = LocalDate.of(1990, 1, 1),
            addresses = listOf(
                Address(
                    id = 1,
                    documentId = "default-address",
                    firstName = "John",
                    lastName = "Doe",
                    firstLineAddress = "123 Main Street",
                    secondLineAddress = "Apt 4B",
                    city = "New York",
                    postCode = "10001",
                    country = "United States",
                    phoneNumber = "+1 (555) 123-4567"
                )
            ),
            avatar = null,
            userAdverts = emptyList(),
            role = UserRole(
                id = 1,
                documentId = "user-role",
                name = "User",
                description = "Standard user role",
                type = "authenticated"
            ),
            isBlocked = false,
            isConfirmed = true,
            provider = "local"
        )
    }
}