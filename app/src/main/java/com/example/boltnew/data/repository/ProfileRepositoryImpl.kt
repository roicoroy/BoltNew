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
                addresses = getSampleAddresses(),
                userAdverts = getSampleUserAdverts(),
                role = UserRole(
                    id = 1,
                    documentId = "default-role",
                    name = "Premium User",
                    description = "Premium user with advanced features",
                    type = "authenticated"
                )
            )
            saveProfile(defaultProfile)
        }
    }
    
    private fun getSampleAddresses(): List<Address> {
        return listOf(
            Address(
                id = 1,
                documentId = "addr-1",
                firstName = "John",
                lastName = "Doe",
                firstLineAddress = "123 Main Street",
                secondLineAddress = "Apartment 4B",
                city = "London",
                postCode = "SW1A 1AA",
                country = "United Kingdom",
                phoneNumber = "+44 20 7946 0958"
            ),
            Address(
                id = 2,
                documentId = "addr-2",
                firstName = "John",
                lastName = "Doe",
                firstLineAddress = "456 Business Avenue",
                secondLineAddress = "Suite 200",
                city = "Manchester",
                postCode = "M1 1AA",
                country = "United Kingdom",
                phoneNumber = "+44 161 123 4567"
            ),
            Address(
                id = 3,
                documentId = "addr-3",
                firstName = "John",
                lastName = "Doe",
                firstLineAddress = "789 Holiday Lane",
                city = "Edinburgh",
                postCode = "EH1 1AA",
                country = "Scotland",
                phoneNumber = "+44 131 987 6543"
            )
        )
    }
    
    private fun getSampleUserAdverts(): List<UserAdvert> {
        return listOf(
            UserAdvert(
                id = 101,
                documentId = "user-advert-1",
                title = "Professional Web Development Services",
                description = "Offering comprehensive web development services including React, Node.js, and mobile app development. 5+ years of experience in creating modern, responsive websites and applications for businesses of all sizes.",
                slug = "professional-web-development-services"
            ),
            UserAdvert(
                id = 102,
                documentId = "user-advert-2",
                title = "Digital Marketing & SEO Consultation",
                description = "Boost your online presence with expert digital marketing strategies. Specializing in SEO optimization, social media marketing, content creation, and Google Ads management to help grow your business.",
                slug = "digital-marketing-seo-consultation"
            ),
            UserAdvert(
                id = 103,
                documentId = "user-advert-3",
                title = "Graphic Design & Branding Solutions",
                description = "Creative graphic design services for logos, branding, marketing materials, and web graphics. Transform your brand identity with professional designs that capture your vision and engage your audience.",
                slug = "graphic-design-branding-solutions"
            ),
            UserAdvert(
                id = 104,
                documentId = "user-advert-4",
                title = "Photography & Video Production",
                description = "Professional photography and videography services for events, portraits, commercial projects, and social media content. High-quality equipment and creative expertise to bring your vision to life.",
                slug = "photography-video-production"
            ),
            UserAdvert(
                id = 105,
                documentId = "user-advert-5",
                title = "Business Consulting & Strategy",
                description = "Strategic business consulting to help startups and established companies optimize operations, improve efficiency, and achieve growth objectives. Expertise in market analysis and business planning.",
                slug = "business-consulting-strategy"
            ),
            UserAdvert(
                id = 106,
                documentId = "user-advert-6",
                title = "Language Translation Services",
                description = "Professional translation services for English, Spanish, French, and German. Specialized in business documents, legal texts, marketing materials, and technical documentation with certified accuracy.",
                slug = "language-translation-services"
            ),
            UserAdvert(
                id = 107,
                documentId = "user-advert-7",
                title = "Personal Fitness Training",
                description = "Certified personal trainer offering customized fitness programs, nutrition guidance, and wellness coaching. Available for one-on-one sessions, group training, and virtual consultations.",
                slug = "personal-fitness-training"
            ),
            UserAdvert(
                id = 108,
                documentId = "user-advert-8",
                title = "Home Renovation & Interior Design",
                description = "Complete home renovation services including kitchen remodeling, bathroom upgrades, and interior design consultation. Licensed contractor with 10+ years of experience in residential projects.",
                slug = "home-renovation-interior-design"
            ),
            UserAdvert(
                id = 109,
                documentId = "user-advert-9",
                title = "Music Lessons & Audio Production",
                description = "Professional music instruction for guitar, piano, and vocals. Also offering audio production services, recording, mixing, and mastering for musicians and content creators.",
                slug = "music-lessons-audio-production"
            ),
            UserAdvert(
                id = 110,
                documentId = "user-advert-10",
                title = "Pet Care & Dog Walking Services",
                description = "Reliable pet care services including dog walking, pet sitting, grooming, and veterinary transport. Experienced with all breeds and sizes, providing loving care for your furry family members.",
                slug = "pet-care-dog-walking-services"
            )
        )
    }
}