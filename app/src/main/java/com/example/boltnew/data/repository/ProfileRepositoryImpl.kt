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
            // Technology & Development Services
            UserAdvert(
                id = 101,
                documentId = "user-advert-1",
                title = "Full-Stack Web Development",
                description = "Expert full-stack developer offering comprehensive web development services. Specializing in React, Vue.js, Node.js, Python Django, and cloud deployment. 8+ years of experience building scalable web applications for startups and enterprises. Portfolio includes e-commerce platforms, SaaS applications, and custom business solutions.",
                slug = "full-stack-web-development"
            ),
            UserAdvert(
                id = 102,
                documentId = "user-advert-2",
                title = "Mobile App Development (iOS & Android)",
                description = "Native and cross-platform mobile app development using React Native, Flutter, Swift, and Kotlin. From concept to App Store deployment, I handle the entire development lifecycle. Experienced in building social apps, productivity tools, and business applications with modern UI/UX design.",
                slug = "mobile-app-development-ios-android"
            ),
            UserAdvert(
                id = 103,
                documentId = "user-advert-3",
                title = "Digital Marketing & SEO Optimization",
                description = "Boost your online presence with data-driven digital marketing strategies. Services include SEO optimization, Google Ads management, social media marketing, content strategy, and conversion rate optimization. Proven track record of increasing organic traffic by 300%+ and improving ROI for small to medium businesses.",
                slug = "digital-marketing-seo-optimization"
            ),
            
            // Creative & Design Services
            UserAdvert(
                id = 104,
                documentId = "user-advert-4",
                title = "Professional Graphic Design & Branding",
                description = "Creative graphic designer specializing in brand identity, logo design, marketing materials, and digital graphics. Adobe Creative Suite expert with 6+ years of experience working with fashion brands, tech startups, and local businesses. Complete branding packages available including style guides and brand strategy.",
                slug = "professional-graphic-design-branding"
            ),
            UserAdvert(
                id = 105,
                documentId = "user-advert-5",
                title = "Photography & Video Production",
                description = "Professional photographer and videographer offering services for weddings, corporate events, product photography, and social media content. High-end equipment including 4K cameras, professional lighting, and drone photography. Quick turnaround times and unlimited revisions included.",
                slug = "photography-video-production"
            ),
            UserAdvert(
                id = 106,
                documentId = "user-advert-6",
                title = "UI/UX Design & Prototyping",
                description = "User-centered design specialist creating intuitive interfaces for web and mobile applications. Expertise in Figma, Sketch, Adobe XD, and prototyping tools. Services include user research, wireframing, visual design, and usability testing. Worked with fintech, healthcare, and e-commerce companies.",
                slug = "ui-ux-design-prototyping"
            ),
            
            // Business & Consulting Services
            UserAdvert(
                id = 107,
                documentId = "user-advert-7",
                title = "Business Strategy & Management Consulting",
                description = "MBA-qualified business consultant helping startups and SMEs optimize operations, develop growth strategies, and improve profitability. Specializing in market analysis, financial planning, process improvement, and digital transformation. 10+ years of experience across various industries including tech, retail, and manufacturing.",
                slug = "business-strategy-management-consulting"
            ),
            UserAdvert(
                id = 108,
                documentId = "user-advert-8",
                title = "Financial Planning & Investment Advisory",
                description = "Certified Financial Planner offering personalized financial planning services. Expertise in retirement planning, investment portfolio management, tax optimization, and estate planning. Help individuals and families achieve their financial goals through strategic planning and smart investment decisions.",
                slug = "financial-planning-investment-advisory"
            ),
            UserAdvert(
                id = 109,
                documentId = "user-advert-9",
                title = "Legal Services & Contract Review",
                description = "Qualified solicitor providing legal services for small businesses and individuals. Specializing in contract law, employment law, intellectual property, and business formation. Offering contract reviews, legal document drafting, and general legal advice at competitive rates.",
                slug = "legal-services-contract-review"
            ),
            
            // Education & Training Services
            UserAdvert(
                id = 110,
                documentId = "user-advert-10",
                title = "Programming Tutoring & Code Mentorship",
                description = "Experienced software engineer offering programming tutoring and mentorship. Teaching Python, JavaScript, Java, C++, and web development fundamentals. Suitable for beginners to intermediate developers looking to advance their careers. One-on-one sessions, code reviews, and career guidance available.",
                slug = "programming-tutoring-code-mentorship"
            ),
            UserAdvert(
                id = 111,
                documentId = "user-advert-11",
                title = "Language Lessons (English, Spanish, French)",
                description = "Certified language instructor offering personalized language lessons for English, Spanish, and French. Native English speaker with fluency in multiple languages. Specializing in business communication, exam preparation (IELTS, TOEFL), and conversational skills. Online and in-person sessions available.",
                slug = "language-lessons-english-spanish-french"
            ),
            UserAdvert(
                id = 112,
                documentId = "user-advert-12",
                title = "Music Production & Audio Engineering",
                description = "Professional music producer and audio engineer with state-of-the-art home studio. Services include music production, mixing, mastering, podcast editing, and voiceover recording. Worked with indie artists, commercial brands, and content creators. All genres welcome.",
                slug = "music-production-audio-engineering"
            ),
            
            // Health & Wellness Services
            UserAdvert(
                id = 113,
                documentId = "user-advert-13",
                title = "Personal Training & Fitness Coaching",
                description = "Certified personal trainer specializing in strength training, weight loss, and athletic performance. Offering personalized workout plans, nutrition guidance, and lifestyle coaching. Available for one-on-one sessions, small group training, and virtual consultations. Home gym setup and outdoor training options available.",
                slug = "personal-training-fitness-coaching"
            ),
            UserAdvert(
                id = 114,
                documentId = "user-advert-14",
                title = "Nutrition Counseling & Meal Planning",
                description = "Registered dietitian providing evidence-based nutrition counseling and personalized meal planning. Specializing in weight management, sports nutrition, digestive health, and chronic disease management. Comprehensive nutrition assessments and ongoing support to help you achieve your health goals.",
                slug = "nutrition-counseling-meal-planning"
            ),
            UserAdvert(
                id = 115,
                documentId = "user-advert-15",
                title = "Yoga Instruction & Mindfulness Coaching",
                description = "Certified yoga instructor (RYT-500) offering private yoga sessions, group classes, and mindfulness coaching. Specializing in Hatha, Vinyasa, and restorative yoga styles. Also providing meditation guidance, stress management techniques, and corporate wellness programs.",
                slug = "yoga-instruction-mindfulness-coaching"
            ),
            
            // Home & Lifestyle Services
            UserAdvert(
                id = 116,
                documentId = "user-advert-16",
                title = "Interior Design & Home Staging",
                description = "Professional interior designer with 12+ years of experience transforming residential and commercial spaces. Services include complete home makeovers, room redesigns, color consultations, and home staging for property sales. Sustainable design practices and budget-friendly options available.",
                slug = "interior-design-home-staging"
            ),
            UserAdvert(
                id = 117,
                documentId = "user-advert-17",
                title = "Handyman & Home Repair Services",
                description = "Skilled handyman offering comprehensive home repair and maintenance services. Expertise in plumbing, electrical work, carpentry, painting, and general repairs. Licensed and insured with 15+ years of experience. Same-day service available for urgent repairs. Free estimates provided.",
                slug = "handyman-home-repair-services"
            ),
            UserAdvert(
                id = 118,
                documentId = "user-advert-18",
                title = "Garden Design & Landscaping",
                description = "Professional landscape designer creating beautiful outdoor spaces. Services include garden design, plant selection, hardscaping, irrigation systems, and ongoing maintenance. Specializing in sustainable gardening practices, native plant gardens, and small space solutions.",
                slug = "garden-design-landscaping"
            ),
            
            // Specialized Services
            UserAdvert(
                id = 119,
                documentId = "user-advert-19",
                title = "Pet Training & Behavioral Consultation",
                description = "Certified animal behaviorist offering dog training, puppy socialization, and pet behavioral consultations. Positive reinforcement methods for obedience training, aggression management, and anxiety issues. In-home sessions and group classes available. Experience with all breeds and ages.",
                slug = "pet-training-behavioral-consultation"
            ),
            UserAdvert(
                id = 120,
                documentId = "user-advert-20",
                title = "Event Planning & Coordination",
                description = "Professional event planner specializing in weddings, corporate events, and private celebrations. Full-service planning including venue selection, vendor coordination, timeline management, and day-of coordination. Creating memorable experiences within any budget. Portfolio includes 200+ successful events.",
                slug = "event-planning-coordination"
            )
        )
    }
}