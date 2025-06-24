package com.example.boltnew.data.repository

import com.example.boltnew.data.model.Profile
import com.example.boltnew.data.model.Address
import com.example.boltnew.data.model.UserAdvert
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    fun getProfile(): Flow<Profile?>
    suspend fun getProfileSync(): Profile?
    suspend fun saveProfile(profile: Profile)
    suspend fun updateProfile(profile: Profile)
    suspend fun updateAvatar(avatarUrl: String?, thumbnailUrl: String?)
    
    // Address management
    fun getAddresses(): Flow<List<Address>>
    suspend fun getAddressesSync(): List<Address>
    suspend fun saveAddress(address: Address)
    suspend fun saveAddresses(addresses: List<Address>)
    suspend fun updateAddress(address: Address)
    suspend fun deleteAddress(address: Address)
    suspend fun deleteAllAddresses()
    
    // User adverts
    fun getUserAdverts(): Flow<List<UserAdvert>>
    suspend fun getUserAdvertsSync(): List<UserAdvert>
    suspend fun saveUserAdvert(userAdvert: UserAdvert)
    suspend fun saveUserAdverts(userAdverts: List<UserAdvert>)
    suspend fun updateUserAdvert(userAdvert: UserAdvert)
    suspend fun deleteUserAdvert(userAdvert: UserAdvert)
    suspend fun deleteAllUserAdverts()
    
    suspend fun initializeDefaultProfile()
}