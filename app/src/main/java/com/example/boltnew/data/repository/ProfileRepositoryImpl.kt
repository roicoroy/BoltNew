package com.example.boltnew.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.boltnew.data.database.ProfileDao
import com.example.boltnew.data.mapper.toDomain
import com.example.boltnew.data.mapper.toEntity
import com.example.boltnew.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class ProfileRepositoryImpl(
    private val profileDao: ProfileDao
) : ProfileRepository {
    
    @RequiresApi(Build.VERSION_CODES.O)
    override fun getProfile(): Flow<Profile?> {
        return combine(
            profileDao.getProfile(),
            profileDao.getAddressesByProfileId(1),
            profileDao.getUserAdvertsByProfileId(1)
        ) { profileEntity, addresses, userAdverts ->
            profileEntity?.toDomain(addresses, userAdverts)
        }
    }
    
    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getProfileSync(): Profile? {
        val profileEntity = profileDao.getProfileSync() ?: return null
        val addresses = profileDao.getAddressesByProfileIdSync(1)
        val userAdverts = profileDao.getUserAdvertsByProfileIdSync(1)
        return profileEntity.toDomain(addresses, userAdverts)
    }
    
    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun saveProfile(profile: Profile) {
        profileDao.insertProfile(profile.toEntity())
        
        // Save addresses
        profileDao.deleteAddressesByProfileId(1)
        if (profile.addresses.isNotEmpty()) {
            val addressEntities = profile.addresses.map { it.toEntity(1) }
            profileDao.insertAddresses(addressEntities)
        }
        
        // Save user adverts
        profileDao.deleteUserAdvertsByProfileId(1)
        if (profile.userAdverts.isNotEmpty()) {
            val userAdvertEntities = profile.userAdverts.map { it.toEntity(1) }
            profileDao.insertUserAdverts(userAdvertEntities)
        }
    }
    
    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun updateProfile(profile: Profile) {
        profileDao.updateProfile(profile.toEntity())
        
        // Update addresses
        profileDao.deleteAddressesByProfileId(1)
        if (profile.addresses.isNotEmpty()) {
            val addressEntities = profile.addresses.map { it.toEntity(1) }
            profileDao.insertAddresses(addressEntities)
        }
        
        // Update user adverts
        profileDao.deleteUserAdvertsByProfileId(1)
        if (profile.userAdverts.isNotEmpty()) {
            val userAdvertEntities = profile.userAdverts.map { it.toEntity(1) }
            profileDao.insertUserAdverts(userAdvertEntities)
        }
    }
    
    override suspend fun updateAvatar(avatarUrl: String?, thumbnailUrl: String?) {
        profileDao.updateAvatar(avatarUrl, thumbnailUrl)
    }
    
    @RequiresApi(Build.VERSION_CODES.O)
    override fun getAddresses(): Flow<List<Address>> {
        return profileDao.getAddressesByProfileId(1).map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getAddressesSync(): List<Address> {
        return profileDao.getAddressesByProfileIdSync(1).map { it.toDomain() }
    }
    
    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun saveAddress(address: Address) {
        profileDao.insertAddress(address.toEntity(1))
    }
    
    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun saveAddresses(addresses: List<Address>) {
        val entities = addresses.map { it.toEntity(1) }
        profileDao.insertAddresses(entities)
    }
    
    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun updateAddress(address: Address) {
        profileDao.updateAddress(address.toEntity(1))
    }
    
    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun deleteAddress(address: Address) {
        profileDao.deleteAddress(address.toEntity(1))
    }
    
    override suspend fun deleteAllAddresses() {
        profileDao.deleteAddressesByProfileId(1)
    }
    
    @RequiresApi(Build.VERSION_CODES.O)
    override fun getUserAdverts(): Flow<List<UserAdvert>> {
        return profileDao.getUserAdvertsByProfileId(1).map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getUserAdvertsSync(): List<UserAdvert> {
        return profileDao.getUserAdvertsByProfileIdSync(1).map { it.toDomain() }
    }
    
    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun saveUserAdvert(userAdvert: UserAdvert) {
        profileDao.insertUserAdvert(userAdvert.toEntity(1))
    }
    
    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun saveUserAdverts(userAdverts: List<UserAdvert>) {
        val entities = userAdverts.map { it.toEntity(1) }
        profileDao.insertUserAdverts(entities)
    }
    
    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun updateUserAdvert(userAdvert: UserAdvert) {
        profileDao.updateUserAdvert(userAdvert.toEntity(1))
    }
    
    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun deleteUserAdvert(userAdvert: UserAdvert) {
        profileDao.deleteUserAdvert(userAdvert.toEntity(1))
    }
    
    override suspend fun deleteAllUserAdverts() {
        profileDao.deleteUserAdvertsByProfileId(1)
    }
    
    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun initializeDefaultProfile() {
        val existingProfile = getProfileSync()
        if (existingProfile == null) {
            val defaultProfile = Profile(
                username = "johndoe",
                email = "john.doe@example.com",
                dateOfBirth = LocalDate.of(1990, 1, 1),
                addresses = listOf(
                    Address(
                        firstName = "John",
                        lastName = "Doe",
                        firstLineAddress = "123 Main Street",
                        city = "London",
                        postCode = "SW1A 1AA",
                        country = "United Kingdom",
                        phoneNumber = "+44 20 7946 0958"
                    )
                ),
                role = UserRole(
                    id = 1,
                    documentId = "default-role",
                    name = "User",
                    description = "Standard user role",
                    type = "authenticated"
                )
            )
            saveProfile(defaultProfile)
        }
    }
}