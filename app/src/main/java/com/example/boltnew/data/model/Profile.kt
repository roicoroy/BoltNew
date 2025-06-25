package com.example.boltnew.data.model

import java.time.LocalDate

data class Profile(
    val id: Int,
    val documentId: String,
    val username: String,
    val email: String,
    val dateOfBirth: LocalDate,
    val addresses: List<Address> = emptyList(),
    val avatar: Avatar? = null,
    val userAdverts: List<UserAdvert> = emptyList(),
    val role: UserRole? = null,
    val isBlocked: Boolean = false,
    val isConfirmed: Boolean = false,
    val provider: String = "local"
) {
    val displayName: String
        get() = username.ifBlank { email.substringBefore("@") }
    
    val fullAddress: String
        get() = addresses.firstOrNull()?.let { address ->
            buildString {
                append(address.firstLineAddress)
                if (address.secondLineAddress.isNotBlank()) {
                    append(", ${address.secondLineAddress}")
                }
                append(", ${address.city} ${address.postCode}")
                append(", ${address.country}")
            }
        } ?: ""
}

data class Address(
    val id: Int,
    val documentId: String,
    val firstName: String,
    val lastName: String,
    val firstLineAddress: String,
    val secondLineAddress: String,
    val city: String,
    val postCode: String,
    val country: String,
    val phoneNumber: String?
) {
    val fullName: String
        get() = "$firstName $lastName"
}

data class Avatar(
    val id: Int,
    val documentId: String,
    val name: String,
    val url: String,
    val alternativeText: String?,
    val caption: String?,
    val width: Int,
    val height: Int,
    val size: Double,
    val formats: AvatarFormats?
)

data class AvatarFormats(
    val thumbnail: AvatarFormat?,
    val small: AvatarFormat?,
    val medium: AvatarFormat?,
    val large: AvatarFormat?
)

data class AvatarFormat(
    val name: String,
    val url: String,
    val width: Int,
    val height: Int,
    val size: Double
)

data class UserAdvert(
    val id: Int,
    val documentId: String,
    val title: String,
    val description: String,
    val slug: String
)

data class UserRole(
    val id: Int,
    val documentId: String,
    val name: String,
    val description: String,
    val type: String
)